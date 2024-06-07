#include <iostream>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <time.h>
#include <cinttypes>
#include <unistd.h>
#include <vector>

#include "common.h"
#include "err.h"

ssize_t package_size = PSIZE;
const char *mcast_addr = MCAST_ADDR;
uint16_t port = DATA_PORT;
const char *name = NAME;
uint64_t session_id;

// PART II
std::vector<bool> packages_to_rexmit;
std::vector<char> fifo_buff;
ssize_t queue_size = FSIZE;
ssize_t rapport_time = RTIME;
uint16_t ctrl_port = CTRL_PORT;
uint64_t last_saved = 0;
bool alive = true;
pthread_t t;
pthread_t t2;
int fifo_idx = 0;

pthread_mutex_t mutex;
// possible messages in ctrl
const char *lookup = LOOKUP;
const char *rexmit = REXMIT;

int socket_fd;
// PART II

void parse_args(int argc, char **argv) {
  int c;
  int mandatory_params = 0;

  while ((c = getopt(argc, argv, "a:P:C:p:f:n:R:")) != -1) {
    switch (c) {
    case 'a':
      if (!is_valid_ip_addr(optarg))
        fatal("Invalid IP address!");
      mcast_addr = optarg;
      mandatory_params++;
      break;
    case 'P':
      is_valid_port(optarg);
      port = read_port(optarg);
      break;
    case 'C':
      is_valid_port(optarg);
      ctrl_port = read_port(optarg);
      break;
    case 'p':
      if (!isNumericPositive(optarg))
        fatal("Not a positive number!");
      package_size = atoi(optarg);
      break;
    case 'f':
      if (!isNumericPositive(optarg))
        fatal("Not a positive number!");
      queue_size = atoi(optarg);
      break;
    case 'R':
      if (!isNumericPositive(optarg))
        fatal("Not a positive number!");
      rapport_time = atoi(optarg);
      break;
    case 'n':
      if (optarg[0] == '\0' || optarg[0] == ' ')
        fatal("Wrong name!");
      name = optarg;
      break;
    case '?':
      fatal("Unknown option character `\\x%x'.\n", optopt);
    default:
      abort();
    }
  }

  if (mandatory_params < 1) {
    fatal("Usage: %s -a mcast_address [optional arguments]\n", argv[0]);
  }
}

void send_audio_packet(int sockfd, uint64_t session_id, uint64_t first_byte_num,
                       char *audio_data) {
  // Serialize audio_packet struct to byte stream
  char message[2 * sizeof(uint64_t) + package_size];

  session_id = htobe64(session_id);
  first_byte_num = htobe64(first_byte_num);

  memcpy(message, (char *)&session_id, sizeof(uint64_t));
  memcpy(message + sizeof(uint64_t), (char *)&first_byte_num, sizeof(uint64_t));
  memcpy(message + 2 * sizeof(uint64_t), audio_data, package_size);

  struct sockaddr_in send_address = get_send_address(mcast_addr, port);
  send_message_direct(sockfd, &send_address, message,
                      2 * sizeof(uint64_t) + package_size);
}

void *rexmiting(void *) {
    char saved_message[package_size];
    int  very_own_socket_fd = open_udp_socket();
    bind_socket_udp(very_own_socket_fd, port, mcast_addr);
    while (true) {
        usleep(1000 * rapport_time);
        for (int i = 0; i < queue_size / package_size; i++) {
            if (packages_to_rexmit[i] != 1) {
                continue;
            }

            pthread_mutex_lock(&mutex);
            std::copy(fifo_buff.data() + i * package_size, fifo_buff.data() + (i + 1) * package_size, saved_message);
            packages_to_rexmit[i] = 0;
            pthread_mutex_unlock(&mutex);

            uint64_t last_idx = (last_saved / package_size) % (queue_size / package_size);
            uint64_t first_byte_sent=0;
            if (static_cast<uint64_t>(i) > last_idx) {
                first_byte_sent = last_saved - queue_size + i * package_size;
            } else {
                first_byte_sent = last_saved - (last_idx - i) * package_size;
            }
            send_audio_packet(very_own_socket_fd, session_id, first_byte_sent, saved_message);
        }
    }
}


// PART II:
void *control(void *) {
  int control_socket_fd = open_udp_socket();

  /* podłączenie do grupy rozsyłania (ang. multicast) */
  struct ip_mreq ip_mreq;
  ip_mreq.imr_interface.s_addr = htonl(INADDR_ANY);
  if (inet_aton(mcast_addr, &ip_mreq.imr_multiaddr) == 0) {
    fatal("inet_aton - invalid multicast address\n");
  }
  CHECK_ERRNO(setsockopt(control_socket_fd, IPPROTO_IP, IP_ADD_MEMBERSHIP,
                         (void *)&ip_mreq, sizeof(ip_mreq)));

  /* ustawienie adresu i portu lokalnego */
  bind_socket_udp(control_socket_fd, ctrl_port, NULL);

  char buffer[65536];
  while (alive) {
    struct sockaddr_in client_address;
    size_t received_length = read_message_direct(
        control_socket_fd, &client_address, buffer, sizeof(buffer));
    char ctrl_message[received_length + 1];
    memcpy(ctrl_message, buffer, received_length);
    ctrl_message[received_length] = '\0';

    if (strcmp(ctrl_message, lookup) == 0) {
      char response[512];
      snprintf(response, sizeof(response), "BOREWICZ_HERE %s %d %s", mcast_addr,
               port, name);
      send_message_direct(control_socket_fd, &client_address, response,
                          strlen(response));
      continue;
    }

    char part1[strlen(rexmit) + 1];
     if(strlen(ctrl_message) < strlen(rexmit))continue;
    strncpy(part1, ctrl_message, strlen(rexmit));
    part1[strlen(rexmit)] = '\0';

    if (strcmp(part1, rexmit) != 0) {
      continue;
    }

    size_t part2_length = received_length - strlen(part1) - 1;
    char part2[part2_length + 1];
    memcpy(part2, ctrl_message + strlen(part1) + 1, part2_length);
    part2[part2_length] = '\0';

    char num[32];
    int idx = 0;
    memset(num, '0', sizeof(num));
    for (size_t i = 0; i < strlen(part2); i++) {
      if (part2[i] >= '0' && part2[i] <= '9') {
        num[idx] = part2[i];
        idx++;
      } else if (idx != 0) {
        num[idx] = '\0';
        int number = atoi(num) / package_size;
        int mod_value = static_cast<int>(queue_size / package_size);
        packages_to_rexmit[number % mod_value] = true;
        idx = 0;
        memset(num, '0', sizeof(num));
      }
    }
  }

  /* odłączenie od grupy rozsyłania */
  CHECK_ERRNO(setsockopt(socket_fd, IPPROTO_IP, IP_DROP_MEMBERSHIP,
                         (void *)&ip_mreq, sizeof(ip_mreq)));
  /* koniec */
  CHECK_ERRNO(close(socket_fd));
  return 0;
}

void save_packet(char *data, uint64_t first_byte_num) {
  pthread_mutex_lock(&mutex);
  packages_to_rexmit[(first_byte_num / package_size) % queue_size] = 0;
  last_saved = first_byte_num;
  memcpy(fifo_buff.data() + first_byte_num % (queue_size), data, package_size);
  pthread_mutex_unlock(&mutex);
}
// PART II

int main(int argc, char *argv[]) {
  if (argc < 3) {
    fatal("Usage: %s <-a dest_adress> <-p psize> <-P data_port> <-n name> \n",
          argv[0]);
  }
  parse_args(argc, argv);
  fifo_buff.reserve(queue_size);

  queue_size = queue_size - queue_size % package_size;

  packages_to_rexmit.reserve(queue_size / package_size);
  packages_to_rexmit.assign(queue_size / package_size, 0);

  // PART II:
  pthread_mutex_init(&mutex, NULL);
  pthread_create(&t, NULL, control, NULL);
  pthread_create(&t2, NULL, rexmiting, NULL);
  // PART II

  char audio_data[package_size];
  time_t current_time = time(NULL);
  session_id = (uint64_t)current_time;
  uint64_t first_byte_num = 0;
  ssize_t read_size;
  first_byte_num = 0;

  socket_fd = open_udp_socket();

  /* podłączenie do grupy rozsyłania (ang. multicast) */
  struct ip_mreq ip_mreq;
  ip_mreq.imr_interface.s_addr = htonl(INADDR_ANY);
  if (inet_aton(mcast_addr, &ip_mreq.imr_multiaddr) == 0) {
    fatal("inet_aton - invalid multicast address\n");
  }
  CHECK_ERRNO(setsockopt(socket_fd, IPPROTO_IP, IP_ADD_MEMBERSHIP,
                         (void *)&ip_mreq, sizeof(ip_mreq)));

  /* ustawienie adresu i portu lokalnego */
  bind_socket_udp(socket_fd, port, mcast_addr);

  while ((read_size = fread(audio_data, 1, package_size, stdin)) > 0) {
    if (read_size < package_size) {
      break;
    }
    save_packet(audio_data, first_byte_num);
    send_audio_packet(socket_fd, session_id, first_byte_num, audio_data);
    first_byte_num += read_size;
  }
  CHECK_ERRNO(close(socket_fd));
  return 0;
}
