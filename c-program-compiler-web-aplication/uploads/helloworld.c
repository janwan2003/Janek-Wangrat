#include <stdio.h>
int main() {
   // printf() displays the string inside quotation
    __asm__( 
	"  mov %1,%%eax\n"
	"  add $2,%%eax\n"
	"  mov %%eax,%0\n"
  );
   printf("Hello, World!");
   return 0;
}