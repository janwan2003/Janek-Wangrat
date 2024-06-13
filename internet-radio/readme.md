# Internet Radio Transmitter and Receiver

## Overview

This project involves the implementation of an internet radio transmitter and receiver. The transmitter sends audio data streams over UDP, while the receiver processes these streams and outputs audio.

## Installation

Before running the transmitter or receiver, make sure to install all necessary dependencies. This typically involves setting up the appropriate audio tools and compilers.

## Transmitter (Part A)

The transmitter sends audio data received via standard input to a designated receiver using UDP packets.

### Key Parameters

- `DEST_ADDR` (-a): Receiver's address.
- `DATA_PORT` (-P): UDP port for data transmission, default is 20000 + (album_number % 10000).
- `PSIZE` (-p): Size of the audio_data field in the packet, default is 512 bytes.
- `NAME` (-n): Name of the transmitter, default is "Unnamed Transmitter".

### Usage

To transmit audio data, such as from a CD-quality MP3 file:

```bash
sox -S "sample.mp3" -r 44100 -b 16 -e signed-integer -c 2 -t raw - | pv -q -L $((44100*4)) | ./transmitter -a 192.168.1.2 -n "Radio Music"
```

This command line converts an MP3 file to a raw audio stream and sends it to the receiver at a controlled rate.

## Receiver (Part B)

The receiver captures audio data sent by the transmitter and outputs it to standard audio output.

### Key Parameters

- `DATA_PORT` (-P): UDP port to receive data, matching the transmitter's setting.
- `BSIZE` (-b): Buffer size for storing incoming packets, default is 64kB.

### Usage

To receive and play audio data:

```bash
./receiver -a 192.168.1.2 | play -t raw -c 2 -r 44100 -b 16 -e signed-integer --buffer 32768 -
```

This setup will start the receiver and pipe its output to an audio player capable of playing raw audio data.

## Protocols

### Audio Data Transmission

- Data is sent over UDP in packets, with each packet containing a session ID, the first byte number, and the audio data.
- Packets are discarded if they do not complete the PSIZE requirement.

### Control Protocol

- Uses UDP for control messages, including packet retransmission requests and station identification.
- The receiver periodically sends discovery messages and handles responses to maintain a list of active stations.

## Note

The transmitter and receiver should be configured to use the same DATA_PORT and have compatible PSIZE and BSIZE settings to ensure proper data handling and playback.

## Conclusion

This README provides a basic guide on setting up and running the internet radio transmitter and receiver for the given project. Adjust parameters and commands as necessary to fit specific deployment environments or use cases.
