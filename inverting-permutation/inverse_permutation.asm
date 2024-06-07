section .data
    INT_MAX equ 2147483647
section .text
global inverse_permutation

inverse_permutation:
    ; Prologue
    push    rbp
    mov     rbp, rsp
    sub     rsp, 16
    ; Check if n is ok
    cmp     rdi, 0
    jle     .false
    cmp     rdi, INT_MAX
    ja      .false
    ; Check if it is a permutation
    xor     rcx, rcx    ; rcx = 0
    xor     rdx, rdx    ; rdx = 0
.check_range:                ; Check if numbers are between 0 and n-1
    mov     eax, [rsi + rcx*4]    ; Load the current value of p[i] into eax
    cmp     rax, rdi
    jae     .false          ; If p[i] >= n, jump to .false
    cmp     rax, 0
    jl      .false          ; If p[i] < 0, jump to .false
    inc     rcx
    cmp     rcx, rdi
    jne     .check_range    ; If we haven't checked all elements yet, go back to check_range
    xor     rcx, rcx
.check_uniqueness:           ; Check if numbers are unique
    mov     eax, [rsi + rcx*4]    ; Load the current value of p[i] into rax
    or     eax, 0x80000000
    xor     eax, 0x80000000
    mov     edx, [rsi + rax*4]    ; Load the current value of p[p[i]] into rdx
    test    edx, edx           ; Check if the sign byte is 0
    js      .clear_mess
    xor     edx, 0x80000000    ; Change its last byte to 1
    mov     [rsi + rax*4], edx    ; Put it back
    inc     rcx
    cmp     rcx, rdi
    jne     .check_uniqueness
    xor     rcx, rcx        ; idx = 0
    mov     eax, [rsi + rcx*4]
.reverse:                     ; Everything is fine - let's reverse the permutation
    test    eax, eax        ; Check if the sign byte is 1
    js      .next_iteration    ; If yes then go to next_iteration
    inc     rcx
    mov     eax, [rsi + rcx*4]    ; marked[idx] also next_idx = p[idx]
    cmp     rcx, rdi
    jne     .reverse
    jmp     .end
.next_iteration:
    xor     eax, 0x80000000
    mov     edx, [rsi + rcx*4]
    xor     edx, 0x80000000
    mov     [rsi + rcx*4], edx    ; Put it back - now marked[idx]=0
    mov     edx, eax            ; rdx = rax = next_idx (pom)
    mov     eax, [rsi + rdx*4]    ; next_idx = p[next_idx]
    test    eax, eax
    js      .neg_ecx
.continue:
    mov     [rsi + rdx*4], ecx    ; p[pom] = idx
    mov     ecx, edx        ; ecx = next_idx
    jmp     .reverse
global aaa
.end:
    mov     eax, 1   ; eax = true
    leave
    ret
.clear_mess:
    xor     rcx, rcx
.clear_loop: ;
    cmp     rcx, rdi
    je      .false
    mov     eax, [rsi + rcx*4]
    or      eax, 0x80000000
    xor     eax, 0x80000000
    mov     [rsi + rcx*4], eax
    inc     rcx
    jmp     .clear_loop
.false:
    xor     eax, eax     ; eax = false
    leave
    ret
.neg_ecx: ; Changing the sign of ecx
    xor     ecx, 0x80000000
    jmp     .continue

