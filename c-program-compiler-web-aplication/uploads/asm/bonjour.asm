;--------------------------------------------------------
; File Created by SDCC : free open source ANSI-C Compiler
; Version 4.0.0 #11528 (Linux)
;--------------------------------------------------------
	; MODULE bonjour
	.optsdcc -mz80
	;Generated using the isas tokens.
	LPREFIX '?'  ; Treat labels starting with ? as local.
	ONCNUM       ; Numbers are hex
	CAPSOFF      ; Case sensitive
	ISDMG        ; Gameboy mode
_CODE	GROUP
	; We have to define these here as sdcc doesn't make them global by default
	GLOBAL __mulschar
	GLOBAL __muluchar
	GLOBAL __mulint
	GLOBAL __divschar
	GLOBAL __divuchar
	GLOBAL __divsint
	GLOBAL __divuint
	GLOBAL __modschar
	GLOBAL __moduchar
	GLOBAL __modsint
	GLOBAL __moduint
	GLOBAL banked_call
	GLOBAL banked_ret

;--------------------------------------------------------
; Public variables in this module
;--------------------------------------------------------
	GLOBAL _main
	GLOBAL _printf
;--------------------------------------------------------
; special function registers
;--------------------------------------------------------
;--------------------------------------------------------
; ram data
;--------------------------------------------------------
	_DATA	GROUP
;--------------------------------------------------------
; ram data
;--------------------------------------------------------
	INITIALIZED	GROUP
;--------------------------------------------------------
; absolute external ram data
;--------------------------------------------------------
	DABS (ABS)	GROUP
;--------------------------------------------------------
; global & static initialisations
;--------------------------------------------------------
	HOME	GROUP
	GSINIT	GROUP
	GSFINAL	GROUP
	GSINIT	GROUP
;--------------------------------------------------------
; Home
;--------------------------------------------------------
	_CODE	GROUP
	_CODE	GROUP
;--------------------------------------------------------
; code
;--------------------------------------------------------
	_CODE	GROUP
;/home/janek/Janek-Wangrat/c-program-compiler-web-aplication/uploads/bonjour.c:2: int main() {
;	---------------------------------
; Function main
; ---------------------------------
_main::
;/home/janek/Janek-Wangrat/c-program-compiler-web-aplication/uploads/bonjour.c:3: printf("Bonjour!");
	ld	hl, ___str_0
	push	hl
	call	_printf
	pop	af
;/home/janek/Janek-Wangrat/c-program-compiler-web-aplication/uploads/bonjour.c:4: return 0;
	ld	hl, 0x0000
?l00101:
;/home/janek/Janek-Wangrat/c-program-compiler-web-aplication/uploads/bonjour.c:5: }
	ret
___str_0:
	DB "Bonjour!"
	DB 0x00
	_CODE	GROUP
	INITIALIZER	GROUP
	CABS (ABS)	GROUP
