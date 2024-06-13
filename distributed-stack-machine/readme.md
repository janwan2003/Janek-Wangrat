# Distributed Stack Machine

## Overview

This project implements a distributed stack machine simulator in x86_64 assembly. The machine consists of N cores, numbered from 0 to N-1, where N is a constant determined at compile time. The simulator is used from a C program by launching N threads, each invoking the function:

```c
uint64_t core(uint64_t n, char const *p);
```

## Instructions

The parameter `n` is the core number. The parameter `p` is a pointer to an ASCIIZ string defining the computation, consisting of operations on a stack. The operations are interpreted as follows:

- : Pop two values, push their sum.

* : Pop two values, push their product.

- : Negate the top value.
  0-9 : Push the corresponding value.
  n : Push the core number.
  B : Pop a value, use it as a two's complement offset to jump in the instruction string.
  C : Pop and discard the top value.
  D : Duplicate the top value.
  E : Swap the top two values.
  G : Push the result of `uint64_t get_value(uint64_t n)`.
  P : Pop a value and call `void put_value(uint64_t n, uint64_t w)`.
  S : Synchronize cores, swap the top values of the stacks of the current core and the core number popped from the stack.

The result of the function `core` is the value at the top of the stack after computation.

## Compilation

The solution is compiled with the following command:

```sh
nasm -DN=XXX -f elf64 -w+all -w+error -o core.o core.asm
```

where `XXX` specifies the value of `N`.

## Example Usage

An example usage is provided in `example.c`. It can be compiled and linked with:

```sh
nasm -DN=2 -f elf64 -w+all -w+error -o core.o core.asm
gcc -c -Wall -Wextra -std=c17 -O2 -o example.o example.c
gcc -z noexecstack -o example core.o example.o -lpthread
```

## Evaluation

The solution will be evaluated based on automatic tests for compliance with the specification, ABI rules, and memory usage. Code quality, including comments, will also be assessed.

For further questions, refer to the dedicated thread on Moodle.
