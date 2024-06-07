# Inverting Permutations

## Overview

Implement a function in x86_64 assembly to be called from C:

```c
bool inverse_permutation(size_t n, int *p);
```

The function's arguments are a pointer `p` to a non-empty array of integers and the size `n` of that array. If the array pointed to by `p` contains a permutation of numbers from 0 to `n-1`, the function inverts this permutation in place and returns `true`. Otherwise, the function returns `false`, and the content of the array remains unchanged. The function should detect obviously incorrect values of `n` - see the example usage. It can be assumed that the pointer `p` is valid.

## Submission

Submit a file named `inverse_permutation.asm` on Moodle.

## Compilation

The solution will be compiled with the following command:

```sh
nasm -f elf64 -w+all -w+error -o inverse_permutation.o inverse_permutation.asm
```

The solution must compile in the computer lab environment.

## Example Usage

An example usage is provided in `inverse_permutation_example.c`. It can be compiled and linked with the solution using the commands:

```sh
gcc -c -Wall -Wextra -std=c17 -O2 -o inverse_permutation_example.o inverse_permutation_example.c
gcc -z noexecstack -o inverse_permutation_example inverse_permutation_example.o inverse_permutation.o
```

## Evaluation

The solution will be evaluated for compliance with the specification using automated tests. The evaluation will also check adherence to ABI rules, memory access correctness, and memory usage. The goal is to minimize the memory used by the solution. The score from the automated tests will be reduced proportionally to the size of additional memory used (.bss, .data, .rodata sections, stack, heap). A threshold for the .text section size will also be set. Exceeding this threshold will result in a score reduction proportional to the excess size. The solution's execution speed will be another criterion; excessively slow solutions will not receive the maximum score. The programming style will also be assessed. The score may depend on an explanation of the program's details to the instructor.

Traditional assembly programming style involves starting labels in the first column, mnemonics in the ninth column, and argument lists in the seventeenth column. Another acceptable style is shown in class examples. The code should be well-commented, explaining what each procedure does, how parameters are passed, how results are returned, and which registers are modified. The same applies to macros. Key or non-trivial lines inside procedures or macros should also be commented. In assembly language, it is not excessive to comment almost every line of code, but avoid comments that state the obvious.
