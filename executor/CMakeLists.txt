cmake_minimum_required(VERSION 3.1)
project(MIMUW-FORK C)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_C_STANDARD 11)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -g -Wall -Wextra -Wno-sign-compare")

function(add_example_program target_name)
  add_executable(${target_name} "${target_name}.c")
  target_link_libraries(${target_name} PRIVATE err pthread)
endfunction()

add_library(err STATIC err.c)
add_library(utils STATIC utils.c)

add_example_program(executor)
target_link_libraries(executor PRIVATE utils err)
