.PHONY: all clean

CXXFLAGS = -Wall -O2 -g -std=c++20
LFLAGS = -lpthread

all: sikradio-receiver sikradio-sender

clean:
	rm -f sikradio-receiver sikradio-sender

sikradio-receiver: sikradio-receiver.cpp common.h err.h
	g++ $(CXXFLAGS) -o $@ $< $(LFLAGS)

sikradio-sender: sikradio-sender.cpp common.h err.h
	g++ $(CXXFLAGS) -o $@ $< $(LFLAGS)
