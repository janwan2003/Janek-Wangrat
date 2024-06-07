# Automata Drawing Module

## Overview

This module is designed for drawing automata. It accepts input in the following format:

<number_of_vertices> <number_of_edges>
0 1 2 1 ... <-- vertex types
0 1 a <-- subsequent edges with labels
2 4 b
...


## Usage

Example uses can be found in the `Examples` folder. For debugging purposes, it is recommended to set the `drawing` flag in `main.cpp`.

## Vertex Types

- **Initial**: 0
- **Accepting**: 1
- **Default**: 2

## Example Command

To run an example, use the following command:

```sh
./main < Examples/test<number_of_test>
