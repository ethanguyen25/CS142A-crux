    .globl _garble
_garble:
    enter $(8 * 88), $0
    movq %rdi, -8(%rbp)
    movq %rsi, -16(%rbp)
    /*** CopyInst ***/
    movq $0, %r10
    movq %r10, -24(%rbp)
    /*** CopyInst ***/
    movq -24(%rbp), %r10
    movq %r10, -32(%rbp)
    /*** CopyInst ***/
    movq $0, %r10
    movq %r10, -40(%rbp)
    /*** CopyInst ***/
    movq -40(%rbp), %r10
    movq %r10, -48(%rbp)
L9:
    /*** CopyInst ***/
    movq -8(%rbp), %r10
    movq %r10, -56(%rbp)
    /*** CopyInst ***/
    movq $0, %r10
    movq %r10, -64(%rbp)
    /*** CompareInst ***/
    movq $0, %r10
    movq $1, %rax
    movq -56(%rbp), %r11
    cmp -64(%rbp), %r11
    cmove %rax, %r10
    movq %r10, -72(%rbp)
    /*** CopyInst ***/
    movq %r10, -80(%rbp)
    /*** JumpInst ***/
    movq -80(%rbp), %r10
    cmp $1, %r10
    je L1
L2:
    /*** JumpInst ***/
    movq -80(%rbp), %r10
    cmp $1, %r10
    je L3
    /*** CopyInst ***/
    movq -8(%rbp), %r10
    movq %r10, -88(%rbp)
    /*** CopyInst ***/
    movq -8(%rbp), %r10
    movq %r10, -96(%rbp)
    /*** CopyInst ***/
    movq $2, %r10
    movq %r10, -104(%rbp)
    /*** BinaryOperator ***/
    movq -96(%rbp), %rax
    cqto
    idivq -104(%rbp)
    movq %rax, -112(%rbp)
    /*** CopyInst ***/
    movq $2, %r10
    movq %r10, -120(%rbp)
    /*** BinaryOperator ***/
    movq -112(%rbp), %r10
    imulq -120(%rbp), %r10
    movq %r10, -128(%rbp)
    /*** BinaryOperator ***/
    movq -88(%rbp), %r10
    subq -128(%rbp), %r10
    movq %r10, -136(%rbp)
    /*** CopyInst ***/
    movq -136(%rbp), %r10
    movq %r10, -144(%rbp)
    /*** CopyInst ***/
    movq -16(%rbp), %r10
    movq %r10, -152(%rbp)
    /*** CopyInst ***/
    movq -16(%rbp), %r10
    movq %r10, -160(%rbp)
    /*** CopyInst ***/
    movq $2, %r10
    movq %r10, -168(%rbp)
    /*** BinaryOperator ***/
    movq -160(%rbp), %rax
    cqto
    idivq -168(%rbp)
    movq %rax, -176(%rbp)
    /*** CopyInst ***/
    movq $2, %r10
    movq %r10, -184(%rbp)
    /*** BinaryOperator ***/
    movq -176(%rbp), %r10
    imulq -184(%rbp), %r10
    movq %r10, -192(%rbp)
    /*** BinaryOperator ***/
    movq -152(%rbp), %r10
    subq -192(%rbp), %r10
    movq %r10, -200(%rbp)
    /*** CopyInst ***/
    movq -200(%rbp), %r10
    movq %r10, -208(%rbp)
    /*** CopyInst ***/
    movq -144(%rbp), %r10
    movq %r10, -216(%rbp)
    /*** CopyInst ***/
    movq $1, %r10
    movq %r10, -224(%rbp)
    /*** CompareInst ***/
    movq $0, %r10
    movq $1, %rax
    movq -216(%rbp), %r11
    cmp -224(%rbp), %r11
    cmove %rax, %r10
    movq %r10, -232(%rbp)
    /*** CopyInst ***/
    movq %r10, -240(%rbp)
    /*** JumpInst ***/
    movq -240(%rbp), %r10
    cmp $1, %r10
    je L6
L7:
    /*** JumpInst ***/
    movq -240(%rbp), %r10
    cmp $1, %r10
    je L8
    /*** CopyInst ***/
    movq -144(%rbp), %r10
    movq %r10, -248(%rbp)
    /*** CopyInst ***/
    movq $0, %r10
    movq %r10, -256(%rbp)
    /*** CompareInst ***/
    movq $0, %r10
    movq $1, %rax
    movq -248(%rbp), %r11
    cmp -256(%rbp), %r11
    cmove %rax, %r10
    movq %r10, -264(%rbp)
    /*** CopyInst ***/
    movq %r10, -272(%rbp)
    /*** JumpInst ***/
    movq -272(%rbp), %r10
    cmp $1, %r10
    je L10
L11:
    /*** JumpInst ***/
    movq -272(%rbp), %r10
    cmp $1, %r10
    je L12
    /*** CopyInst ***/
    movq -32(%rbp), %r10
    movq %r10, -280(%rbp)
    /*** CopyInst ***/
    movq $2, %r10
    movq %r10, -288(%rbp)
    /*** BinaryOperator ***/
    movq -280(%rbp), %r10
    imulq -288(%rbp), %r10
    movq %r10, -296(%rbp)
    /*** CopyInst ***/
    movq -296(%rbp), %r10
    movq %r10, -32(%rbp)
L14:
L13:
    /*** CopyInst ***/
    movq -8(%rbp), %r10
    movq %r10, -304(%rbp)
    /*** CopyInst ***/
    movq $2, %r10
    movq %r10, -312(%rbp)
    /*** BinaryOperator ***/
    movq -304(%rbp), %rax
    cqto
    idivq -312(%rbp)
    movq %rax, -320(%rbp)
    /*** CopyInst ***/
    movq -320(%rbp), %r10
    movq %r10, -8(%rbp)
    /*** CopyInst ***/
    movq -16(%rbp), %r10
    movq %r10, -328(%rbp)
    /*** CopyInst ***/
    movq $2, %r10
    movq %r10, -336(%rbp)
    /*** BinaryOperator ***/
    movq -328(%rbp), %rax
    cqto
    idivq -336(%rbp)
    movq %rax, -344(%rbp)
    /*** CopyInst ***/
    movq -344(%rbp), %r10
    movq %r10, -16(%rbp)
    /*** CopyInst ***/
    movq -48(%rbp), %r10
    movq %r10, -352(%rbp)
    /*** CopyInst ***/
    movq $1, %r10
    movq %r10, -360(%rbp)
    /*** BinaryOperator ***/
    movq -352(%rbp), %r10
    addq -360(%rbp), %r10
    movq %r10, -368(%rbp)
    /*** CopyInst ***/
    movq -368(%rbp), %r10
    movq %r10, -48(%rbp)
    jmp L9
L12:
    /*** CopyInst ***/
    movq -32(%rbp), %r10
    movq %r10, -376(%rbp)
    /*** CopyInst ***/
    movq $2, %r10
    movq %r10, -384(%rbp)
    /*** BinaryOperator ***/
    movq -376(%rbp), %r10
    imulq -384(%rbp), %r10
    movq %r10, -392(%rbp)
    /*** CopyInst ***/
    movq $1, %r10
    movq %r10, -400(%rbp)
    /*** BinaryOperator ***/
    movq -392(%rbp), %r10
    addq -400(%rbp), %r10
    movq %r10, -408(%rbp)
    /*** CopyInst ***/
    movq -408(%rbp), %r10
    movq %r10, -32(%rbp)
    jmp L14
L10:
    /*** CopyInst ***/
    movq -208(%rbp), %r10
    movq %r10, -416(%rbp)
    /*** CopyInst ***/
    movq $1, %r10
    movq %r10, -424(%rbp)
    /*** CompareInst ***/
    movq $0, %r10
    movq $1, %rax
    movq -416(%rbp), %r11
    cmp -424(%rbp), %r11
    cmove %rax, %r10
    movq %r10, -432(%rbp)
    /*** CopyInst ***/
    movq %r10, -272(%rbp)
    jmp L11
L8:
    /*** CopyInst ***/
    movq -32(%rbp), %r10
    movq %r10, -440(%rbp)
    /*** CopyInst ***/
    movq $2, %r10
    movq %r10, -448(%rbp)
    /*** BinaryOperator ***/
    movq -440(%rbp), %r10
    imulq -448(%rbp), %r10
    movq %r10, -456(%rbp)
    /*** CopyInst ***/
    movq $1, %r10
    movq %r10, -464(%rbp)
    /*** BinaryOperator ***/
    movq -456(%rbp), %r10
    addq -464(%rbp), %r10
    movq %r10, -472(%rbp)
    /*** CopyInst ***/
    movq -472(%rbp), %r10
    movq %r10, -32(%rbp)
    jmp L13
L6:
    /*** CopyInst ***/
    movq -208(%rbp), %r10
    movq %r10, -480(%rbp)
    /*** CopyInst ***/
    movq $0, %r10
    movq %r10, -488(%rbp)
    /*** CompareInst ***/
    movq $0, %r10
    movq $1, %rax
    movq -480(%rbp), %r11
    cmp -488(%rbp), %r11
    cmove %rax, %r10
    movq %r10, -496(%rbp)
    /*** CopyInst ***/
    movq %r10, -240(%rbp)
    jmp L7
L3:
    /*** CopyInst ***/
    movq $0, %r10
    movq %r10, -504(%rbp)
    /*** CopyInst ***/
    movq -504(%rbp), %r10
    movq %r10, -512(%rbp)
L5:
    /*** CopyInst ***/
    movq -48(%rbp), %r10
    movq %r10, -520(%rbp)
    /*** CopyInst ***/
    movq $0, %r10
    movq %r10, -528(%rbp)
    /*** CompareInst ***/
    movq $0, %r10
    movq $1, %rax
    movq -520(%rbp), %r11
    cmp -528(%rbp), %r11
    cmove %rax, %r10
    movq %r10, -536(%rbp)
    /*** JumpInst ***/
    movq -536(%rbp), %r10
    cmp $1, %r10
    je L4
    /*** CopyInst ***/
    movq -512(%rbp), %r10
    movq %r10, -544(%rbp)
    /*** CopyInst ***/
    movq $2, %r10
    movq %r10, -552(%rbp)
    /*** BinaryOperator ***/
    movq -544(%rbp), %r10
    imulq -552(%rbp), %r10
    movq %r10, -560(%rbp)
    /*** CopyInst ***/
    movq -32(%rbp), %r10
    movq %r10, -568(%rbp)
    /*** CopyInst ***/
    movq -32(%rbp), %r10
    movq %r10, -576(%rbp)
    /*** CopyInst ***/
    movq $2, %r10
    movq %r10, -584(%rbp)
    /*** BinaryOperator ***/
    movq -576(%rbp), %rax
    cqto
    idivq -584(%rbp)
    movq %rax, -592(%rbp)
    /*** CopyInst ***/
    movq $2, %r10
    movq %r10, -600(%rbp)
    /*** BinaryOperator ***/
    movq -592(%rbp), %r10
    imulq -600(%rbp), %r10
    movq %r10, -608(%rbp)
    /*** BinaryOperator ***/
    movq -568(%rbp), %r10
    subq -608(%rbp), %r10
    movq %r10, -616(%rbp)
    /*** BinaryOperator ***/
    movq -560(%rbp), %r10
    addq -616(%rbp), %r10
    movq %r10, -624(%rbp)
    /*** CopyInst ***/
    movq -624(%rbp), %r10
    movq %r10, -512(%rbp)
    /*** CopyInst ***/
    movq -32(%rbp), %r10
    movq %r10, -632(%rbp)
    /*** CopyInst ***/
    movq $2, %r10
    movq %r10, -640(%rbp)
    /*** BinaryOperator ***/
    movq -632(%rbp), %rax
    cqto
    idivq -640(%rbp)
    movq %rax, -648(%rbp)
    /*** CopyInst ***/
    movq -648(%rbp), %r10
    movq %r10, -32(%rbp)
    /*** CopyInst ***/
    movq -48(%rbp), %r10
    movq %r10, -656(%rbp)
    /*** CopyInst ***/
    movq $1, %r10
    movq %r10, -664(%rbp)
    /*** BinaryOperator ***/
    movq -656(%rbp), %r10
    subq -664(%rbp), %r10
    movq %r10, -672(%rbp)
    /*** CopyInst ***/
    movq -672(%rbp), %r10
    movq %r10, -48(%rbp)
    jmp L5
L4:
    /*** CopyInst ***/
    movq -512(%rbp), %r10
    movq %r10, -680(%rbp)
    /*** ReturnInst ***/
    movq -680(%rbp), %rax
    leave
    ret
L1:
    /*** CopyInst ***/
    movq -16(%rbp), %r10
    movq %r10, -688(%rbp)
    /*** CopyInst ***/
    movq $0, %r10
    movq %r10, -696(%rbp)
    /*** CompareInst ***/
    movq $0, %r10
    movq $1, %rax
    movq -688(%rbp), %r11
    cmp -696(%rbp), %r11
    cmove %rax, %r10
    movq %r10, -704(%rbp)
    /*** CopyInst ***/
    movq %r10, -80(%rbp)
    jmp L2
    .globl _main
_main:
    enter $(8 * 104), $0
    /*** CopyInst ***/
    movq $0, %r10
    movq %r10, -712(%rbp)
    /*** CopyInst ***/
    movq -712(%rbp), %r10
    movq %r10, -720(%rbp)
L16:
    /*** CopyInst ***/
    movq -720(%rbp), %r10
    movq %r10, -728(%rbp)
    /*** CopyInst ***/
    movq $40, %r10
    movq %r10, -736(%rbp)
    /*** CompareInst ***/
    movq $0, %r10
    movq $1, %rax
    movq -728(%rbp), %r11
    cmp -736(%rbp), %r11
    cmovge %rax, %r10
    movq %r10, -744(%rbp)
    /*** JumpInst ***/
    movq -744(%rbp), %r10
    cmp $1, %r10
    je L15
    /*** CallInst ***/
    call _readChar
    movq %rax, -752(%rbp)
    /*** CopyInst ***/
    movq -752(%rbp), %r10
    movq %r10, -760(%rbp)
    /*** CopyInst ***/
    movq -760(%rbp), %r10
    movq %r10, -768(%rbp)
    /*** CopyInst ***/
    movq $7, %r10
    movq %r10, -776(%rbp)
    /*** CopyInst ***/
    movq -720(%rbp), %r10
    movq %r10, -784(%rbp)
    /*** BinaryOperator ***/
    movq -776(%rbp), %r10
    addq -784(%rbp), %r10
    movq %r10, -792(%rbp)
    /*** CallInst ***/
    movq -768(%rbp), %rdi
    movq -792(%rbp), %rsi
    call _garble
    movq %rax, -800(%rbp)
    /*** CallInst ***/
    movq -800(%rbp), %rdi
    call _printChar
    movq %rax, -808(%rbp)
    /*** CopyInst ***/
    movq -720(%rbp), %r10
    movq %r10, -816(%rbp)
    /*** CopyInst ***/
    movq $1, %r10
    movq %r10, -824(%rbp)
    /*** BinaryOperator ***/
    movq -816(%rbp), %r10
    addq -824(%rbp), %r10
    movq %r10, -832(%rbp)
    /*** CopyInst ***/
    movq -832(%rbp), %r10
    movq %r10, -720(%rbp)
    jmp L16
L15:
    leave
    ret
