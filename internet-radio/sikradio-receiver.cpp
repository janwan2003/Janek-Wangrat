#include <arpa/inet.h>
#include <chrono>
#include <inttypes.h>
#include <iostream>
#include <map>
#include <poll.h>
#include <pthread.h>
#include <semaphore.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <string>
#include <sys/socket.h>
#include <sys/types.h>
#include <termios.h>
#include <thread>
#include <unistd.h>
#include <vector>

#include "common.h"
#include "err.h"
// part2
#define BUF_SIZE 65536
#define QUEUE_LENGTH 128
#define TIMEOUT 5000
#define CONNECTIONS 128
static bool finish = false;
// part2
uint64_t buffer_size = BSIZE;
uint16_t port = DATA_PORT;
bool printing = false;
uint64_t BYTE0 = 0;
bool waiting = false;
uint64_t package_size;
pthread_t control1;
pthread_t control2;
pthread_t tcp;
pthread_t reading;
pthread_t listening;
pthread_t deleting;
pthread_mutex_t global_mutex;

sem_t free_;
sem_t taken;
sem_t mutex;
sem_t s_listening;

// PART II:
const char *discover_addr = DISCOVER_ADDR;
uint16_t ctrl_port = CTRL_PORT;
uint16_t ui_port = UI_PORT;
ssize_t rapport_time = RTIME;
time_t time_buffer;
char *fav_station;

const char *lookup = LOOKUP;
const char *rexmit = REXMIT;

int ctrl_socket_fd;
int ctrl_socket_fd2;
bool change_station = false;
int listening_socket_fd;
struct ip_mreq ip_mreq;
std::chrono::system_clock::time_point started =
    std::chrono::system_clock::now();

struct station {
  std::string mcast_addr;
  std::string data_port_str;
  std::string nazwa_stacji;

  bool operator<(const station &other) const {
    int result = mcast_addr.compare(other.mcast_addr);
    if (result != 0) {
      return result < 0;
    }

    result = data_port_str.compare(other.data_port_str);
    if (result != 0) {
      return result < 0;
    }

    return nazwa_stacji.compare(other.nazwa_stacji) < 0;
  }
};

std::map<struct station, std::chrono::time_point<std::chrono::system_clock>>
    stations;

auto selected_station = stations.begin();
auto fav_it = stations.end();
bool there_is_favourite = false;

struct station get_station(int number) {
  if (number < 0 || (long unsigned int)number >= stations.size()) {
    return station();
  }

  auto it = stations.begin();
  std::advance(it, number);
  return it->first;
}

struct pollfd poll_descriptors[CONNECTIONS];
size_t active_clients = 0;
// PART II

void init_change_station() {
  pthread_mutex_lock(&global_mutex);
  const auto &current_station = selected_station->first;
  started =
      std::chrono::system_clock::now();

  listening_socket_fd = open_udp_socket();

  /* podłączenie do grupy rozsyłania (ang. multicast) */
  ip_mreq.imr_interface.s_addr = htonl(INADDR_ANY);
  if (inet_aton(current_station.mcast_addr.c_str(), &ip_mreq.imr_multiaddr) ==
      0) {
    fatal("inet_aton - invalid multicast address\n");
  }
  CHECK_ERRNO(setsockopt(listening_socket_fd, IPPROTO_IP, IP_ADD_MEMBERSHIP,
                         (void *)&ip_mreq, sizeof(ip_mreq)));

  /* ustawienie adresu i portu lokalnego */
  bind_socket_udp(listening_socket_fd, stoi(current_station.data_port_str),
                  current_station.mcast_addr.c_str());
  change_station = true;
  pthread_mutex_unlock(&global_mutex);
}

void parse_args(int argc, char **argv) {
  int c;
  while ((c = getopt(argc, argv, "d:C:U:b:R:n:")) != -1) {
    switch (c) {
    case 'd':
      if (!is_valid_ip_addr(optarg))
        fatal("Invalid IP address!");
      discover_addr = optarg;
      break;
    case 'C':
      is_valid_port(optarg);
      ctrl_port = read_port(optarg);
      break;
    case 'U':
      is_valid_port(optarg);
      ui_port = read_port(optarg);
      break;
    case 'n':
      if (optarg[0] == '\0' || optarg[0] == ' ')
        fatal("Wrong name!");
      fav_station = optarg;
      break;
    case 'b':
      if (!isNumericPositive(optarg))
        fatal("Not a positive number!");
      buffer_size = atoi(optarg);
      break;
    case 'R':
      if (!isNumericPositive(optarg))
        fatal("Not a positive number!");
      rapport_time = atoi(optarg);
      break;
    case '?':
      fatal("Unknown option character `\\x%x'.\n", optopt);
    default:
      abort();
    }
  }
}

struct thread_args {
  char *shared_buffer;
  bool *packages;
};

void *reader(void *my_t) {
  struct thread_args *args = (struct thread_args *)my_t;
  char *shared_buffer = args->shared_buffer;
  bool *packages = args->packages;
  uint64_t idx = 0;
  while (printing) {
    sem_wait(&taken);
    if (packages[(idx / package_size) % (buffer_size / package_size)] != 1 &&
        printing)
      fwrite(shared_buffer + (idx % buffer_size), 1, package_size, stdout);
    sem_post(&free_);
    idx = (idx + package_size);
  }
  sem_post(&mutex);
  return 0;
}

void display_stations(bool send_to_everyone, int my_id) {
  // Prepare the station list header
  const std::string header =
      "\033c-------------------------------------------------------"
      "-----------------\n\r"
      " SIK Radio\n\r"
      "-------------------------------------------------------"
      "-----------------\n\r";

  std::string station_list;

  // Create the station list
  station_list += header;
  int counter = 0;
  for (auto it = stations.begin(); it != stations.end(); it++) {
    const auto &station = it->first;
    const std::string &station_name = station.nazwa_stacji;

    if (it == selected_station) {
      station_list += "> " + station_name + "\n\r";
    } else {
      station_list += station_name + "\n\r";
    }
    counter++;
  }
  station_list += "----------------------------------------------------"
                  "--------------------\n\r";

  if (send_to_everyone) {
    // Send the station list to each connected client
    for (int i = 1; i < CONNECTIONS; ++i) {
      if (poll_descriptors[i].fd != -1) {
        write(poll_descriptors[i].fd, station_list.c_str(),
              station_list.length());
      }
    }
  } else {
    write(poll_descriptors[my_id].fd, station_list.c_str(),
          station_list.length());
  }
}

void *deleteInactive(void *) {
  while (true) {
    sleep(20);

    auto currentTime = std::chrono::system_clock::now();
    bool something_deleted = false;
    // Iterate over the map and delete inactive elements
    for (auto it = stations.begin(); it != stations.end();) {
      auto elapsedTime = currentTime - it->second;
      if (elapsedTime >= std::chrono::seconds(20)) {
        if (it == selected_station) {
          selected_station = stations.begin();
          if(stations.size() > 0){
            pthread_mutex_lock(&global_mutex);
            CHECK_ERRNO(setsockopt(listening_socket_fd, IPPROTO_IP, IP_DROP_MEMBERSHIP,
                                  (void *)&ip_mreq, sizeof(ip_mreq)));
            /* koniec */
            shutdown(listening_socket_fd, SHUT_RDWR);
            CHECK_ERRNO(close(listening_socket_fd));
            pthread_mutex_unlock(&global_mutex);
            init_change_station();
          }
        }
        if (there_is_favourite){
          if (it->first.nazwa_stacji == fav_station)
            there_is_favourite = false;
          else selected_station = fav_it;
        }
        it = stations.erase(it);
        something_deleted = true;
      } else {
        ++it;
      }
    }
    if (something_deleted)
      display_stations(true, 0);
  }
}

// Function to handle input from clients
void handle_input(char *mes, ssize_t length) {
  if (length < 3)
    return;
  if ((int)mes[0] == 27 && (int)mes[1] == 91 &&
      (int)mes[2] == 66) { // Arrow Down
    selected_station++;
    if (selected_station == stations.end())
      selected_station = stations.begin();
  } else if ((int)mes[0] == 27 && (int)mes[1] == 91 &&
             (int)mes[2] == 65) { // Arrow Up
    if (selected_station == stations.begin())
      selected_station = stations.end();
    selected_station--;
  } else
    return;

  /* odłączenie od grupy rozsyłania */
  pthread_mutex_lock(&global_mutex);
  CHECK_ERRNO(setsockopt(listening_socket_fd, IPPROTO_IP, IP_DROP_MEMBERSHIP,
                         (void *)&ip_mreq, sizeof(ip_mreq)));
  /* koniec */
  shutdown(listening_socket_fd, SHUT_RDWR);
  CHECK_ERRNO(close(listening_socket_fd));
  pthread_mutex_unlock(&global_mutex);
  init_change_station();

  display_stations(true, 0);
  sleep(1);
}

void *establish_ui_connection(void *) {
  char buf[BUF_SIZE];
  /* Inicjujemy tablicę z gniazdkami klientów, poll_descriptors[0] to gniazdko
   * centrali */
  for (int i = 0; i < CONNECTIONS; ++i) {
    poll_descriptors[i].fd = -1;
    poll_descriptors[i].events = POLLIN;
    poll_descriptors[i].revents = 0;
  }
  /* Tworzymy gniazdko centrali */
  poll_descriptors[0].fd = open_socket();

  bind_socket_to_exact_port(poll_descriptors[0].fd, ui_port);

  start_listening(poll_descriptors[0].fd, QUEUE_LENGTH);

  do {
    for (int i = 0; i < CONNECTIONS; ++i) {
      poll_descriptors[i].revents = 0;
    }

    /* Po Ctrl-C zamykamy gniazdko centrali */
    if (finish && poll_descriptors[0].fd >= 0) {
      CHECK_ERRNO(close(poll_descriptors[0].fd));
      poll_descriptors[0].fd = -1;
    }

    int poll_status = poll(poll_descriptors, CONNECTIONS, -1);
    if (poll_status > 0) {
      if (!finish && (poll_descriptors[0].revents & POLLIN)) {
        /* Przyjmuję nowe połączenie */
        int client_fd = accept_connection(poll_descriptors[0].fd, NULL);

        for (int i = 1; i < CONNECTIONS; ++i) {
          if (poll_descriptors[i].fd == -1) {
            poll_descriptors[i].fd = client_fd;
            poll_descriptors[i].events = POLLIN;
            active_clients++;
            const unsigned char data[] = {0xFF, 0xFD, 0x22, 0xFF, 0xFB, 0x01};
            write(poll_descriptors[i].fd, data, sizeof(data));
            display_stations(0, i);
            break;
          }
        }
      }
      for (int i = 1; i < CONNECTIONS; ++i) {
        if (poll_descriptors[i].fd != -1 &&
            (poll_descriptors[i].revents & (POLLIN | POLLERR))) {
          ssize_t received_bytes = read(poll_descriptors[i].fd, buf, BUF_SIZE);
          if (received_bytes == 0) {
            CHECK_ERRNO(close(poll_descriptors[i].fd));
            poll_descriptors[i].fd = -1;
            active_clients -= 1;
          } else {
            handle_input(buf, received_bytes);
          }
        }
      }
    }
  } while (!finish || active_clients > 0);

  if (poll_descriptors[0].fd >= 0)
    CHECK_ERRNO(close(poll_descriptors[0].fd));
  exit(EXIT_SUCCESS);
}

void *control_request(void *) {
  char buffer[65537];
  open_udp_socket_broadcast(ctrl_socket_fd, ctrl_port, discover_addr);
  while (true) {
    const char *ctrl_message = lookup;
    strncpy(buffer, ctrl_message, BSIZE);
    size_t length = strnlen(buffer, BSIZE);
    send_message(ctrl_socket_fd, buffer, length, NO_FLAGS);
    sleep(5);
  }
}

void *receive_control(void *) {
  char buffer[65537];
  struct sockaddr_in client_address;
  size_t read_length;
  while (true) {
    read_length = read_message_direct(ctrl_socket_fd2, &client_address, buffer,
                                      sizeof(buffer));
    // Unpack the message
    char *token;
    const char *delim = " ";
    token = strtok(buffer, delim); // Get the first token "BOREWICZ_HERE"
    if (token != NULL && strcmp(token, "BOREWICZ_HERE") == 0) {
      std::string mcast_addr;
      std::string data_port_str;
      std::string nazwa_stacji;

      // Extract the multicast address
      token = strtok(NULL, delim);
      if (token != NULL) {
        mcast_addr = token;
      } else {
        continue; // Invalid message format, skip processing
      }

      // Extract the data port
      token = strtok(NULL, delim);
      if (token != NULL) {
        data_port_str = token;
      } else {
        continue; // Invalid message format, skip processing
      }

      // Extract the station name
      token = strtok(NULL, ""); // Get all remaining tokens as the station name
      if (token != NULL) {
        size_t station_name_length = read_length - (token - buffer);
        nazwa_stacji = std::string(token, station_name_length);
      } else {
        continue; // Invalid message format, skip processing
      }
      struct station new_station = {mcast_addr, data_port_str, nazwa_stacji};
      std::chrono::time_point<std::chrono::system_clock> creation_time =
          std::chrono::system_clock::now();
      bool is_new = false;
      if (!stations.count(new_station))
        is_new = true;
      stations[new_station] = creation_time;
      if (stations.size() == 1) {
          selected_station = stations.begin();
          sem_post(&s_listening);
          continue;
      }
      auto it = stations.find(new_station);
      if(fav_station != NULL){
        if (strcmp(nazwa_stacji.c_str(), fav_station) == 0 && is_new) {
          pthread_mutex_lock(&global_mutex);
          CHECK_ERRNO(setsockopt(listening_socket_fd, IPPROTO_IP,
                                IP_DROP_MEMBERSHIP, (void *)&ip_mreq,
                                sizeof(ip_mreq)));
          shutdown(listening_socket_fd, SHUT_RDWR);
          CHECK_ERRNO(close(listening_socket_fd));
          selected_station = it;
          fav_it = it;
          there_is_favourite = true;
          pthread_mutex_unlock(&global_mutex);
          init_change_station();

          display_stations(true, 0);
        } 
      }
      if (is_new)
        display_stations(0, true);
    }
  }
  return 0;
}

void *listener(void *) {
  while (true) {
    sem_wait(&s_listening);

    char shared_buffer[buffer_size];

    init_change_station();

    char buf[buffer_size + 16];
    bool packages[buffer_size];
    memset(packages, 0, sizeof(packages));
    memset(buf, '0', sizeof(buf));
    memset(shared_buffer, '0', buffer_size);

    sem_init(&mutex, 1, 0);
    uint64_t buff_idx = 0;         // latest position in buffer
    uint64_t last_byte_num = 0;    // latest frist byte received
    uint64_t largest_byte_num = 0; // the largest byte num received
    uint64_t curr_session_id = 0, curr_first_byte_num = 0;
    uint64_t savebuffsize = buffer_size;
    uint64_t session_id = 0;
    size_t received_length = 0;
    do {
      struct sockaddr_in client_address;
      pthread_mutex_lock(&global_mutex);
      pthread_mutex_unlock(&global_mutex);
      received_length = read_message_direct(listening_socket_fd,
                                            &client_address, buf, sizeof(buf));

      if (received_length == 0)
        continue;
      memcpy((char *)&curr_session_id, buf, sizeof(uint64_t));
      memcpy((char *)&curr_first_byte_num, buf + sizeof(uint64_t),
             sizeof(uint64_t));

      curr_session_id = be64toh(curr_session_id);
      curr_first_byte_num = be64toh(curr_first_byte_num);

      if (curr_session_id < session_id && !change_station)
        continue;
      change_station = false;
      if (curr_session_id != session_id) {
        if (session_id != 0) {
          printing = false;
          sem_post(&taken);
          sem_wait(&mutex);
          sem_destroy(&free_);
          sem_destroy(&taken);
        }
        memset(shared_buffer, '0', sizeof(shared_buffer));
        memset(packages, 0, sizeof(packages));

        session_id = curr_session_id;
        BYTE0 = curr_first_byte_num;
        package_size = received_length - 16;
        buffer_size = savebuffsize - (savebuffsize % package_size);
        sem_init(&taken, 1, 0);
        sem_init(&free_, 1, buffer_size / package_size);
        last_byte_num = 0;
        largest_byte_num = 0;
        buff_idx = 0;
      }
      if (curr_first_byte_num < BYTE0)
        continue;
      curr_first_byte_num -= BYTE0;

      if ((buffer_size * 3 / 4 <= curr_first_byte_num && !printing)) {
        printing = true;
        thread_args *args = new thread_args;
        args->shared_buffer = shared_buffer;
        args->packages = packages;
        pthread_create(&reading, NULL, reader, args);
      }
      // sem_wait(&free_);

      uint64_t where =
          (buff_idx + curr_first_byte_num - last_byte_num) % buffer_size;
      if (curr_first_byte_num > largest_byte_num + package_size)
        for (uint64_t i = 1;
             i < (curr_first_byte_num - largest_byte_num) / package_size; i++) {
          packages[(where / package_size - i) % (buffer_size / package_size)] =
              1;
          sem_post(&taken);
        }
      memcpy(shared_buffer + where, buf + 2 * sizeof(uint64_t), package_size);
      if (curr_first_byte_num >= largest_byte_num) {
        sem_post(&taken);
      }
      packages[where / package_size] = 0;

    auto currentTime = std::chrono::system_clock::now();

    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(currentTime - started);

    // Compare the duration with the desired `rapport_time`
    if (duration >= std::chrono::milliseconds(rapport_time)) {
        std::string s = "LOUDER_PLEASE ";
        int count = 0;
        for (uint64_t i = 1; i < buffer_size / package_size; i++) {

          if (curr_first_byte_num / package_size + i <
              buffer_size / package_size)
            continue;

          if (packages[(where / package_size + i) %
                       (buffer_size / package_size)] == 1) {
            uint64_t pac = ((curr_first_byte_num / package_size + i) -
                            (buffer_size / package_size)) * package_size;
            if (pac > largest_byte_num)
              continue;
            if (count == 0)
              s += std::to_string(pac);
            else
              s += "," + std::to_string(pac);
            count++;
          }
        }
        if (count > 0) {
          s += "\n";
          client_address.sin_port = htons(30012); // Set port 10012, using htons for network byte order
          send_message_direct(ctrl_socket_fd, &client_address, s.c_str(), s.size());
        }
      }

      buff_idx = where;
      last_byte_num = curr_first_byte_num;
      largest_byte_num = max(largest_byte_num, curr_first_byte_num);
    } while (true);
  }
}

int main(int argc, char *argv[]) {
  parse_args(argc, argv);
  pthread_mutex_init(&global_mutex, NULL);
  ctrl_socket_fd = bind_socket(0);
  uint16_t a = check_port(ctrl_socket_fd);
  ctrl_socket_fd2 = bind_socket(a);
  sem_init(&s_listening, 1, 0);

  pthread_create(&control1, NULL, control_request, NULL);
  pthread_create(&control2, NULL, receive_control, NULL);
  pthread_create(&tcp, NULL, establish_ui_connection, NULL);
  pthread_create(&listening, NULL, listener, NULL);
  pthread_create(&deleting, NULL, deleteInactive, NULL);

  pthread_join(control1, NULL);
  return 0;
}