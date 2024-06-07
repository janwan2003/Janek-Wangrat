section .bss
    align 8
    stack_pointers : times N resq N ; 8 * N bites for stack pointers
section .data
    align 8
    table : times N dq N ;8 * N bites to communicate
section .text
global core
extern get_value
extern put_value

; Stack operations - sorted by length to save the size 
; 'C' Take the value off the stack and dump it
dump_stack:
    pop     rax
    jmp     core.loop

; 'n' Insert the core number on the stack
push_core_number:
    push    rdi
    jmp     core.loop

; '-' Negate arithmetically the value on the top of the stack
negate_stack:
    neg     qword [rsp]
    jmp     core.loop

; '0 - 9' Insert on the stack the value 0 to 9, respectively
push_number:
    sub     rax, 48 ; Convert ASCII digit to number
    push    rax
    jmp     core.loop

; 'D' Insert a value from the top of the stack, that is, duplicate the value on the top of the stack
dup_stack:
    mov     rax, [rsp]
    push    rax
    jmp     core.loop

; '+' Take two values off the stack, calculate their sum and put the result on the stack
add_stack:
    pop     rax
    pop     rcx
    add     rax, rcx
    push    rax
    jmp     core.loop

; '*' Take two values off the stack, calculate their product and insert the result on the stack
mul_stack:
    pop     rax
    pop     rcx
    mul     rcx
    push    rax
    jmp     core.loop

; 'B' Take the value off the stack, if there is now a value on the top of the stack that is different from zero,
; treat the removed value as a number in the complement to two code and move by that many operations
jmp_negative:
    pop     rax
    mov     rcx, [rsp]
    test    rcx, rcx
    jz      core.loop
    add     rsi, rax    
    jmp     core.loop

; Core function on the middle to save the binary size cause of smaller jumps -_-
core:
    push    rbp
    mov     rbp, rsp
    ; Loop over instructions
.loop:
    ; Load instruction
    movzx   rax, byte [rsi]
    inc     rsi
    ; Execute instruction
    cmp     rax, 43 ; '+'
    je      add_stack
    cmp     rax, 42 ; '*'
    je      mul_stack
    cmp     rax, 45 ; '-'
    je      negate_stack
    cmp     rax, 110 ; 'n'
    je      push_core_number
    cmp     rax, 66 ; 'B'
    je      jmp_negative
    cmp     rax, 67 ; 'C'
    je      dump_stack
    cmp     rax, 68 ; 'D'
    je      dup_stack
    cmp     rax, 69 ; 'E'
    je      swap_stack
    cmp     rax, 71 ; 'G'
    je      push_get_value
    cmp     rax, 80 ; 'P'
    je      call_put_value
    cmp     rax, 83 ; 'S'
    je      sync_cores
    ; End of instructions
    cmp rax, 0
    je end
    jmp push_number
end:
    mov     rax, [rsp]
    mov     rsp, rbp
    pop     rbp
    ret

; 'E' Swap two values on the top of the stack with each other
swap_stack:
    mov     rax, [rsp]
    mov     rcx, [rsp + 8]
    mov     [rsp], rcx
    mov     [rsp + 8], rax
    jmp     core.loop

; 'G' Insert on the stack the value obtained from a call (implemented somewhere else in C) to the uint64_t get_value(uint64_t n) function
push_get_value:
    push    rsi    ; We gotta save the registers
    push    rbx
    push    rdi
    mov     rbx, rsp
    and     rsp, -16
    call    get_value
    mov     rsp, rbx
    pop     rdi
    pop     rbx ; Restoring the registers
    pop     rsi
    push    rax
    jmp     core.loop

; 'P' Take the value off the stack (let's denote it by w) and call (implemented somewhere else in C) function void put_value(uint64_t n, uint64_t w)
call_put_value:
    push    rsi ; Saving the registers as before
    mov     rsi, [rsp + 8]
    push     rbx    
    push    rdi
    mov     rbx, rsp
    and     rsp, -16
    call    put_value
    mov     rsp, rbx
    pop     rdi
    pop     rbx
    pop     rsi
    pop     rax
    jmp     core.loop

; 'S' Synchronize the cores, take the value off the stack, treat it as core number m, 
; wait for core m's S operation with core number n taken off the stack,
; and swap the values on the stack vertices of cores m and n.
sync_cores:
    pop     rax               ; Move the value of the core we want to make an exchange with
    lea     r11, [rel stack_pointers]
    lea     r10, [rel table]
    mov     [r11 + rdi * 8], rsp
    mov     [r10 + rdi * 8], rax
    cmp     rax, rdi                 ; Let the smaller core do the work
    jg      .wait_for_other
.sync_cores_wait:
    cmp     rdi, [r10 + 8 * rax]                    ; Check if the other core put anything in their table
    jne     .sync_cores_wait          ; If not wait
    mov     rcx, [rsp]
    mov     r9, [r11 + rax * 8]
    xchg    rcx, [r9]
    mov     [rsp], rcx                  ; Exchange the values on the stacks
    mov     qword [r10 + rax * 8], N ; Clear the entry in the global table
    mov     qword [r10 + rdi * 8], N ; Clear the entry in the global table
.wait_for_other:
    cmp     qword [r10 + rdi * 8], N ; Wait for an information that exchange is over
    jne     .wait_for_other
    jmp     core.loop
    ; Return result
