#include <assert.h>
#include <errno.h>
#include <pthread.h>
#include <semaphore.h>
#include <stdarg.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

#include "err.h"
#include "utils.h"

int counter = 0;

struct task {
    pid_t pid;
    char last_line_out[1024];
    char last_buffor_out[1024];
    char last_line_err[1024];
    char last_buffor_err[1024];
    int pipe_out;
    int pipe_err;
    int num;
    pthread_mutex_t mutex_out;
    pthread_mutex_t mutex_err;
    pthread_t thread_out, thread_err, thread_wai;
};

struct task tasks[4096];
int lines_while_sleeping[4096][2];
int how_many_lines = 0;
pthread_mutex_t global_mutex;
bool is_sleeping = false;

void* reader_out(void* my_t)
{
    struct task* my_task = my_t;
    FILE* jakaszmienna = fdopen(my_task->pipe_out, "r");
    while (read_line(my_task->last_buffor_out, 1024, jakaszmienna)) {
        ASSERT_SYS_OK(pthread_mutex_lock(&my_task->mutex_out));
        memcpy(my_task->last_line_out, my_task->last_buffor_out, 1024);
        ASSERT_SYS_OK(pthread_mutex_unlock(&my_task->mutex_out));
    }
    return NULL;
}

void* wait_for_end(void* my_t)
{
    struct task* my_task = my_t;
    int s;
    ASSERT_SYS_OK(waitpid(my_task->pid, &s, 0));
    ASSERT_SYS_OK(pthread_mutex_lock(&global_mutex));
    if (!is_sleeping) {
        if (WIFSIGNALED(s))
            printf("Task %i ended: signalled.\n", my_task->num);
        else
            printf("Task %i ended: status %i.\n", my_task->num, WEXITSTATUS(s));
    } else {
        lines_while_sleeping[how_many_lines][0] = my_task->num;
        if (WIFSIGNALED(s))
            lines_while_sleeping[how_many_lines][1] = -1;
        else
            lines_while_sleeping[how_many_lines][1] = WEXITSTATUS(s);
        how_many_lines++;
    }
    ASSERT_SYS_OK(pthread_mutex_unlock(&global_mutex));
    return NULL;
}

void* reader_err(void* my_t)
{
    struct task* my_task = my_t;
    FILE* jakaszmienna = fdopen(my_task->pipe_err, "r");
    while (read_line(my_task->last_buffor_err, 1024, jakaszmienna)) {
        ASSERT_SYS_OK(pthread_mutex_lock(&my_task->mutex_err));
        memcpy(my_task->last_line_err, my_task->last_buffor_err, 1024);
        ASSERT_SYS_OK(pthread_mutex_unlock(&my_task->mutex_err));
    }
    return NULL;
}

void do_run(char** strings)
{
    int pipe_out[2];
    int pipe_err[2];
    ASSERT_ZERO(pthread_mutex_init(&tasks[counter].mutex_err, NULL));
    ASSERT_ZERO(pthread_mutex_init(&tasks[counter].mutex_out, NULL));
    ASSERT_SYS_OK(pipe(pipe_out));
    ASSERT_SYS_OK(pipe(pipe_err));
    set_close_on_exec(pipe_out[0], 1);
    set_close_on_exec(pipe_err[0], 1);
    pid_t pid = fork();
    ASSERT_SYS_OK(pid);
    if (!pid) {
        ASSERT_SYS_OK(dup2(pipe_out[1], STDOUT_FILENO));
        ASSERT_SYS_OK(dup2(pipe_err[1], STDERR_FILENO));
        ASSERT_SYS_OK(execvp(strings[0], strings));
    }
    ASSERT_SYS_OK(pthread_mutex_lock(&global_mutex));
    printf("Task %i started: pid %d.\n", counter, pid);
    ASSERT_SYS_OK(pthread_mutex_unlock(&global_mutex));
    ASSERT_SYS_OK(close(pipe_out[1]));
    ASSERT_SYS_OK(close(pipe_err[1]));
    tasks[counter].pid = pid;
    tasks[counter].pipe_out = pipe_out[0];
    tasks[counter].pipe_err = pipe_err[0];
    tasks[counter].num = counter;
    ASSERT_ZERO(pthread_create(&tasks[counter].thread_out, NULL, reader_out, &tasks[counter]));
    ASSERT_ZERO(pthread_create(&tasks[counter].thread_err, NULL, reader_err, &tasks[counter]));
    ASSERT_ZERO(pthread_create(&tasks[counter].thread_wai, NULL, wait_for_end, &tasks[counter]));
    counter++;
}

void do_out(int c)
{
    struct task* my_task = &tasks[c];
    ASSERT_SYS_OK(pthread_mutex_lock(&my_task->mutex_out));
    ASSERT_SYS_OK(pthread_mutex_lock(&global_mutex));
    printf("Task %i stdout: '%s'.\n", c, my_task->last_line_out);
    ASSERT_SYS_OK(pthread_mutex_unlock(&global_mutex));
    ASSERT_SYS_OK(pthread_mutex_unlock(&my_task->mutex_out));
}
void do_err(int c)
{
    struct task* my_task = &tasks[c];
    ASSERT_SYS_OK(pthread_mutex_lock(&my_task->mutex_err));
    ASSERT_SYS_OK(pthread_mutex_lock(&global_mutex));
    printf("Task %i stderr: '%s'.\n", c, my_task->last_line_err);
    ASSERT_SYS_OK(pthread_mutex_unlock(&global_mutex));
    ASSERT_SYS_OK(pthread_mutex_unlock(&my_task->mutex_err));
}

void do_kill(int c)
{
    kill(tasks[c].pid, SIGINT);
}

void do_sleep(size_t N)
{
    ASSERT_SYS_OK(pthread_mutex_lock(&global_mutex));
    is_sleeping = true;
    ASSERT_SYS_OK(pthread_mutex_unlock(&global_mutex));
    usleep(N * 1000);
    ASSERT_SYS_OK(pthread_mutex_lock(&global_mutex));
    for (int i = 0; i < how_many_lines; i++) {
        if (lines_while_sleeping[i][1] == -1)
            printf("Task %i ended: signalled.\n", lines_while_sleeping[i][0]);
        else
            printf("Task %i ended: status %i.\n", lines_while_sleeping[i][0], lines_while_sleeping[i][1]);
    }
    how_many_lines = 0;
    is_sleeping = false;
    ASSERT_SYS_OK(pthread_mutex_unlock(&global_mutex));
}

void do_quit()
{
    for (int i = 0; i < 4096; i++) {
        if (tasks[i].pid != 0) {
            kill(tasks[i].pid, SIGKILL);
            pthread_join(tasks[i].thread_err, NULL);
            pthread_join(tasks[i].thread_wai, NULL);
            pthread_join(tasks[i].thread_out, NULL);
        }
    }
    exit(0);
}

char line[512];

int main()
{
    ASSERT_ZERO(pthread_mutex_init(&global_mutex, NULL));
    while (read_line(line, 512, stdin)) {
        char** strings = split_string(line);
        if (!strcmp(strings[0], "run")) {
            do_run(strings + 1);
        }
        if (!strcmp(strings[0], "out")) {
            do_out(strtoul(strings[1], NULL, 10));
        }
        if (!strcmp(strings[0], "err")) {
            do_err(strtoul(strings[1], NULL, 10));
        }
        if (!strcmp(strings[0], "kill")) {
            do_kill(strtoul(strings[1], NULL, 10));
        }
        if (!strcmp(strings[0], "sleep")) {
            do_sleep(strtoul(strings[1], NULL, 10));
        }
        if (!strcmp(strings[0], "quit")) {
            free_split_string(strings);
            do_quit();
            break;
        }
        free_split_string(strings);
    }
    do_quit();
}