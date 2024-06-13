# Prolog Program Verifier

## Overview

This tool is designed to analyze simple sequential programs to check for safety conditions in a multi-process environment where each process runs the same code. Specifically, it verifies if there is a possible interleaving of process executions where at least two processes are in the critical section simultaneously.

## Specification

- **Constants**: Integer numbers.
- **Variables**: Simple integer variables and one-dimensional integer arrays (indexed from 0, equal to the number of processes in the system).
- **Expressions**:
  - **Arithmetic**: Formed by operations (`+`, `-`, `*`, `/`) between numbers or variables.
  - **Logical**: Relational operations (`<`, `=`, `<>`) between arithmetic expressions.

## Instructions

- `assign(variable, expression)`: Assigns the result of the expression to a variable and moves to the next instruction.
- `goto(number)`: Unconditionally jumps to the specified instruction.
- `condGoto(logical_expression, number)`: Conditionally jumps based on the evaluation of the logical expression.
- `sekcja`: Marks a critical section.

## Usage

```prolog
verify(N, Program)
