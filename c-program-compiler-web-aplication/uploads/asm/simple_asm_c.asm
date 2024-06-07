;--------------------------------------------------------
; File Created by SDCC : free open source ANSI-C Compiler
; Version 4.2.0 #13081 (Linux)
;--------------------------------------------------------
	.module simple_asm_c
	.optsdcc -mstm8
	
;--------------------------------------------------------
; Public variables in this module
;--------------------------------------------------------
	.globl _main
	.globl _printf
;--------------------------------------------------------
; ram data
;--------------------------------------------------------
	.area DATA
;--------------------------------------------------------
; ram data
;--------------------------------------------------------
	.area INITIALIZED
;--------------------------------------------------------
; Stack segment in internal ram
;--------------------------------------------------------
	.area	SSEG
__start__stack:
	.ds	1

;--------------------------------------------------------
; absolute external ram data
;--------------------------------------------------------
	.area DABS (ABS)

; default segment ordering for linker
	.area HOME
	.area GSINIT
	.area GSFINAL
	.area CONST
	.area INITIALIZER
	.area CODE

;--------------------------------------------------------
; interrupt vector
;--------------------------------------------------------
	.area HOME
__interrupt_vect:
	int s_GSINIT ; reset
;--------------------------------------------------------
; global & static initialisations
;--------------------------------------------------------
	.area HOME
	.area GSINIT
	.area GSFINAL
	.area GSINIT
__sdcc_init_data:
; stm8_genXINIT() start
	ldw x, #l_DATA
	jreq	00002$
00001$:
	clr (s_DATA - 1, x)
	decw x
	jrne	00001$
00002$:
	ldw	x, #l_INITIALIZER
	jreq	00004$
00003$:
	ld	a, (s_INITIALIZER - 1, x)
	ld	(s_INITIALIZED - 1, x), a
	decw	x
	jrne	00003$
00004$:
; stm8_genXINIT() end
	.area GSFINAL
	jp	__sdcc_program_startup
;--------------------------------------------------------
; Home
;--------------------------------------------------------
	.area HOME
	.area HOME
__sdcc_program_startup:
	jpf	_main
;	return from main will return to caller
;--------------------------------------------------------
; code
;--------------------------------------------------------
	.area CODE
;	/home/janwan2003/AWWW/aplikacjaWWW/uploads/simple_asm_c.c: 2: int main() {
;	-----------------------------------------
;	 function main
;	-----------------------------------------
_main:
;	/home/janwan2003/AWWW/aplikacjaWWW/uploads/simple_asm_c.c: 9: );
	mov	%1,%%eax
	add	$2,%%eax
	mov	%%eax,%0
;	/home/janwan2003/AWWW/aplikacjaWWW/uploads/simple_asm_c.c: 10: printf("ama");
	push	#<(___str_0+0)
	push	#((___str_0+0) >> 8)
	callf	_printf
	addw	sp, #2
;	/home/janwan2003/AWWW/aplikacjaWWW/uploads/simple_asm_c.c: 15: );
	mov	%1,%%eax
	add	$2,%%eax
	mov	%%eax,%0
;	/home/janwan2003/AWWW/aplikacjaWWW/uploads/simple_asm_c.c: 16: return fred;
	ldw	x, #0x00ea
;	/home/janwan2003/AWWW/aplikacjaWWW/uploads/simple_asm_c.c: 17: }
	retf
	.area CODE
	.area CONST
	.area CONST
___str_0:
	.ascii "ama"
	.db 0x00
	.area CODE
	.area INITIALIZER
	.area CABS (ABS)
