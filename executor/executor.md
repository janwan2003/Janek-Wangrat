# Executor

## Project Overview

The 'Executor' project is designed to facilitate the testing of long-running programs. The executor can start tasks, monitor their output, and terminate them as needed, handling multiple tasks simultaneously identified by unique task IDs.

## Features

- **Run Command**: Starts a new task with a specified program and arguments.
- **Output Monitoring**: Prints the last line of standard output for a task.
- **Error Monitoring**: Prints the last line of standard error for a task.
- **Task Termination**: Sends a SIGINT signal to terminate a task.
- **Auxiliary Commands**: Includes commands to sleep, quit, and handle empty input.

## Command Descriptions

- `run A B C ...`:
  - Starts program `A` with arguments `B C ...` in the background.
  - Outputs: `Task T started: pid P.` where `T` is the task ID and `P` is the process ID.

- `out T`:
  - Prints: `Task T stdout: 'S'.` where `S` is the last line printed by the program of task `T` to standard output.

- `err T`:
  - Prints: `Task T stderr: 'S'.` where `S` is the last line printed by the program of task `T` to standard error.

- `kill T`:
  - Sends a SIGINT signal to terminate the program of task `T`.

- `sleep N`:
  - Pauses the executor for `N` milliseconds.

- `quit`:
  - Terminates the executor and all running tasks.

- Empty Line:
  - Does nothing and moves to the next command.

## Task Completion Notification

When a task completes, the executor prints:
- `Task T ended: status X.` if the task exits normally with status `X`.
- `Task T ended: signalled.` if the task is terminated by a signal or the system.

# Building the Project

To build the project, run the following commands:

```bash
mkdir build
cd build
cmake ..
make