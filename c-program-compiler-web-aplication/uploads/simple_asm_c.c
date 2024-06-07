#include <stdio.h>
int main() {
 int joe=1234, fred = 234;
 //random comment
  __asm__( 
	"  mov %1,%%eax\n"
	"  add $2,%%eax\n"
	"  mov %%eax,%0\n"
  );
  printf("ama");
  __asm__( 
	"  mov %1,%%eax\n"
	"  add $2,%%eax\n"
	"  mov %%eax,%0\n"
  );
  return fred;
}