.PHONY: all clean

CXXFLAGS = -Wall -Wextra -O2 -g -std=c++20
LFLAGS =

all: main

clean:
	rm -f main

main: main.cpp gen.h
	g++ $(CXXFLAGS) -o $@ $< $(LFLAGS)
