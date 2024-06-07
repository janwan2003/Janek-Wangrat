#ifndef COMMON
#define COMMON

#include "err.h"
#include <arpa/inet.h>
#include <netdb.h>
#include <netinet/in.h>
#include <signal.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

#define NO_FLAGS 0

#define PSIZE 512
#define BSIZE 65536
#define NAME "Nienazwany Nadajnik"
#define DATA_PORT 20012
#define MCAST_ADDR "239.19.11.12"

#define DISCOVER_ADDR "255.255.255.255"
#define CTRL_PORT 30012
#define UI_PORT 10012
#define FSIZE 131072
#define RTIME 250
#define LOOKUP "ZERO_SEVEN_COME_IN\n"
#define REXMIT "LOUDER_PLEASE\0"

inline static uint16_t read_port(char *string) {
  errno = 0;
  unsigned long port = strtoul(string, NULL, 10);
  PRINT_ERRNO();
  if (port > UINT16_MAX) {
    fatal("%ul is not a valid port number", port);
  }

  return (uint16_t)port;
}

inline static int bind_socket(uint16_t port) {
  int socket_fd = socket(AF_INET, SOCK_DGRAM, 0); // creating IPv4 UDP socket
  ENSURE(socket_fd >= 0);
  // after socket() call; we should close(sock) on any execution path;

  struct sockaddr_in server_address;
  server_address.sin_family = AF_INET; // IPv4
  server_address.sin_addr.s_addr =
      htonl(INADDR_ANY); // listening on all interfaces
  server_address.sin_port = htons(port);

  int optval = 1;
  if (setsockopt(socket_fd, SOL_SOCKET, SO_REUSEADDR, &optval,
                 sizeof(optval)) == -1) {
    perror("setsockopt");
    exit(EXIT_FAILURE);
  }

  // bind the socket to a concrete address
  CHECK_ERRNO(bind(socket_fd, (struct sockaddr *)&server_address,
                   (socklen_t)sizeof(server_address)));

  return socket_fd;
}

inline static int open_socket() {
  int socket_fd = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);
  if (socket_fd < 0) {
    PRINT_ERRNO();
  }

  return socket_fd;
}

inline static int bind_socket_tcp(uint16_t port) {
  int socket_fd = socket(AF_INET, SOCK_STREAM, 0);
  if (socket_fd == -1) {
    perror("Błąd przy tworzeniu gniazda TCP");
    exit(EXIT_FAILURE);
  }

  // Tworzenie struktury adresowej
  struct sockaddr_in server_address;
  server_address.sin_family = AF_INET;
  server_address.sin_port = htons(port);
  server_address.sin_addr.s_addr = INADDR_ANY; // Dowolny adres IP

  int optval = 1;
  if (setsockopt(socket_fd, SOL_SOCKET, SO_REUSEADDR, &optval,
                 sizeof(optval)) == -1) {
    perror("setsockopt");
    exit(EXIT_FAILURE);
  }

  // Bindowanie gniazda
  if (bind(socket_fd, (struct sockaddr *)&server_address,
           sizeof(server_address)) == -1) {
    perror("Błąd przy bindowaniu gniazda TCP");
    exit(EXIT_FAILURE);
  }

  return socket_fd;
}

uint16_t check_port(int socket) {
  struct sockaddr_in server_addr;
  socklen_t addr_len = sizeof(server_addr);

  if (getsockname(socket, (struct sockaddr *)&server_addr, &addr_len) == -1) {
    perror("getsockname");
    return 0; // Return 0 to indicate an error or failure
  }

  if (server_addr.sin_family != AF_INET) {
    fprintf(stderr, "Expected IPv4 address\n");
    return 0; // Return 0 to indicate an error or failure
  }

  uint16_t port = ntohs(server_addr.sin_port);
  return port;
}

inline static void start_listening(int socket_fd, size_t queue_length) {
  CHECK_ERRNO(listen(socket_fd, queue_length));
}

inline static void install_signal_handler(int signal, void (*handler)(int),
                                          int flags) {
  struct sigaction action;
  sigset_t block_mask;

  sigemptyset(&block_mask);
  action.sa_handler = handler;
  action.sa_mask = block_mask;
  action.sa_flags = flags;

  CHECK_ERRNO(sigaction(signal, &action, NULL));
}

inline static uint16_t bind_socket_to_any_port(int socket_fd) {
  struct sockaddr_in server_address;
  server_address.sin_family = AF_INET; // IPv4
  server_address.sin_addr.s_addr =
      htonl(INADDR_ANY); // listening on all interfaces
  server_address.sin_port = htons(0);

  // bind the socket to a concrete address
  CHECK_ERRNO(bind(socket_fd, (struct sockaddr *)&server_address,
                   (socklen_t)sizeof(server_address)));
  return check_port(socket_fd);
}

inline static uint16_t bind_socket_to_exact_port(int socket_fd, uint16_t port) {
  struct sockaddr_in server_address;
  server_address.sin_family = AF_INET; // IPv4
  server_address.sin_addr.s_addr =
      htonl(INADDR_ANY); // listening on all interfaces
  server_address.sin_port = htons(port);

  int optval = 1;
  if (setsockopt(socket_fd, SOL_SOCKET, SO_REUSEADDR, &optval,
                 sizeof(optval)) == -1) {
    perror("setsockopt");
    exit(EXIT_FAILURE);
  }

  // bind the socket to a concrete address
  CHECK_ERRNO(bind(socket_fd, (struct sockaddr *)&server_address,
                   (socklen_t)sizeof(server_address)));
  return check_port(socket_fd);
}

inline static int accept_connection(int socket_fd,
                                    struct sockaddr_in *client_address) {
  socklen_t client_address_length = (socklen_t)sizeof(*client_address);

  int client_fd = accept(socket_fd, (struct sockaddr *)client_address,
                         &client_address_length);
  if (client_fd < 0) {
    PRINT_ERRNO();
  }

  return client_fd;
}

inline static void bind_socket_udp(int socket_fd, uint16_t port,
                                   const char *mcast_addr) {
  struct sockaddr_in address;
  address.sin_family = AF_INET; // IPv4
  if (mcast_addr != NULL)
    address.sin_addr.s_addr = inet_addr(mcast_addr);
  else
    address.sin_addr.s_addr = htonl(INADDR_ANY); // listening on all interfaces
  address.sin_port = htons(port);

  int optval = 1;
  if (setsockopt(socket_fd, SOL_SOCKET, SO_REUSEADDR, &optval,
                 sizeof(optval)) == -1) {
    perror("setsockopt");
    exit(EXIT_FAILURE);
  }

  // bind the socket to a concrete address
  CHECK_ERRNO(
      bind(socket_fd, (struct sockaddr *)&address, (socklen_t)sizeof(address)));
}

inline static int open_udp_socket_broadcast(int socket_fd, uint16_t port,
                                            const char *adress) {

  /* uaktywnienie rozgłaszania (ang. broadcast) */
  int optval = 1;
  CHECK_ERRNO(setsockopt(socket_fd, SOL_SOCKET, SO_BROADCAST, (void *)&optval,
                         sizeof optval));

  /* ustawienie TTL dla datagramów rozsyłanych do grupy */
  optval = 4;
  CHECK_ERRNO(setsockopt(socket_fd, IPPROTO_IP, IP_MULTICAST_TTL,
                         (void *)&optval, sizeof optval));

  /* ustawienie adresu i portu odbiorcy */
  struct sockaddr_in remote_address;
  remote_address.sin_family = AF_INET;
  remote_address.sin_port = htons(port);
  if (inet_aton(adress, &remote_address.sin_addr) == 0) {
    fprintf(stderr, "ERROR: inet_aton - invalid multicast address\n");
    exit(EXIT_FAILURE);
  }
  connect(socket_fd, (struct sockaddr *)&remote_address,
          sizeof(remote_address));
  return socket_fd;
}

inline static int open_udp_socket() {
  int socket_fd = socket(AF_INET, SOCK_DGRAM, 0);
  if (socket_fd < 0) {
    PRINT_ERRNO();
  }
  return socket_fd;
}

inline static void send_message(int socket_fd, const void *message,
                                size_t length, int flags) {
  errno = 0;
  ssize_t sent_length = send(socket_fd, message, length, flags);
  if (sent_length < 0) {
    PRINT_ERRNO();
  }
  ENSURE(sent_length == (ssize_t)length);
}

inline static struct sockaddr_in get_send_address(const char *host,
                                                  uint16_t port) {
  struct addrinfo hints;
  memset(&hints, 0, sizeof(struct addrinfo));
  hints.ai_family = AF_INET; // IPv4
  hints.ai_socktype = SOCK_DGRAM;
  hints.ai_protocol = IPPROTO_UDP;

  struct addrinfo *address_result;
  CHECK(getaddrinfo(host, NULL, &hints, &address_result));

  struct sockaddr_in send_address;
  send_address.sin_family = AF_INET; // IPv4
  send_address.sin_addr.s_addr =
      ((struct sockaddr_in *)(address_result->ai_addr))
          ->sin_addr.s_addr;           // IP address
  send_address.sin_port = htons(port); // port from the command line

  freeaddrinfo(address_result);

  return send_address;
}

inline static size_t receive_message(int socket_fd, void *buffer,
                                     size_t max_length, int flags) {
  errno = 0;
  ssize_t received_length = recv(socket_fd, buffer, max_length, flags);
  if (received_length < 0) {
    PRINT_ERRNO();
  }
  return (size_t)received_length;
}

size_t read_message_direct(int socket_fd, struct sockaddr_in *client_address,
                           char *buffer, size_t max_length) {
  socklen_t address_length = (socklen_t)sizeof(*client_address);
  int flags = 0; // we do not request anything special
  errno = 0;
  ssize_t len = recvfrom(socket_fd, buffer, max_length, flags,
                         (struct sockaddr *)client_address, &address_length);
  if (len < 0) {
    return 0;
  }
  return (size_t)len;
}

inline bool is_valid_ip_addr(const std::string &addr) {
  struct sockaddr_in sa;
  return inet_pton(AF_INET, addr.c_str(), &(sa.sin_addr)) != 0;
}

bool isNumericPositive(const char *str) {
  if (str == nullptr) {
    return false; // handle null pointer
  }
  // Skip leading whitespace
  while (std::isspace(*str)) {
    ++str;
  }
  // Handle optional sign
  if (*str == '-') {
    return false;
  }
  // Check if remaining characters are digits or a decimal point
  bool hasDigits = false;
  while (*str != '\0') {
    if (std::isdigit(*str)) {
      hasDigits = true;
    } else {
      return false; // invalid character
    }
    ++str;
  }

  // At least one digit must be present
  return hasDigits;
}

void send_message_direct(int socket_fd,
                         const struct sockaddr_in *client_address,
                         const char *message, size_t length) {
  socklen_t address_length = (socklen_t)sizeof(*client_address);
  int flags = 0;
  ssize_t sent_length =
      sendto(socket_fd, message, length, flags,
             (struct sockaddr *)client_address, address_length);
  ENSURE(sent_length == (ssize_t)length);
}

inline static uint64_t max(uint64_t a, uint64_t b) {
  if (a > b)
    return a;
  else
    return b;
}

#endif