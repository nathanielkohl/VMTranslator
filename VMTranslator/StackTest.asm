// push constant 17
@17
D=A
@SP
M=M+1
A=M-1
M=D
// push constant 17
@17
D=A
@SP
M=M+1
A=M-1
M=D
// eq
@SP
M=M-1
A=M-1
D=M
A=A+1
D=D-M
@TRUE0
D;JEQ
@0
D=A
@EXIT0
0;JMP
(TRUE0)
@1
D=-A
(EXIT0)
@SP
A=M-1
M=D
// push constant 17
@17
D=A
@SP
M=M+1
A=M-1
M=D
// push constant 16
@16
D=A
@SP
M=M+1
A=M-1
M=D
// eq
@SP
M=M-1
A=M-1
D=M
A=A+1
D=D-M
@TRUE1
D;JEQ
@0
D=A
@EXIT1
0;JMP
(TRUE1)
@1
D=-A
(EXIT1)
@SP
A=M-1
M=D
// push constant 16
@16
D=A
@SP
M=M+1
A=M-1
M=D
// push constant 17
@17
D=A
@SP
M=M+1
A=M-1
M=D
// eq
@SP
M=M-1
A=M-1
D=M
A=A+1
D=D-M
@TRUE2
D;JEQ
@0
D=A
@EXIT2
0;JMP
(TRUE2)
@1
D=-A
(EXIT2)
@SP
A=M-1
M=D
// push constant 892
@892
D=A
@SP
M=M+1
A=M-1
M=D
// push constant 891
@891
D=A
@SP
M=M+1
A=M-1
M=D
// lt
@SP
M=M-1
A=M-1
D=M
A=A+1
D=D-M
@TRUE3
D;JLT
@0
D=A
@EXIT3
0;JMP
(TRUE3)
@1
D=-A
(EXIT3)
@SP
A=M-1
M=D
// push constant 891
@891
D=A
@SP
M=M+1
A=M-1
M=D
// push constant 892
@892
D=A
@SP
M=M+1
A=M-1
M=D
// lt
@SP
M=M-1
A=M-1
D=M
A=A+1
D=D-M
@TRUE4
D;JLT
@0
D=A
@EXIT4
0;JMP
(TRUE4)
@1
D=-A
(EXIT4)
@SP
A=M-1
M=D
// push constant 891
@891
D=A
@SP
M=M+1
A=M-1
M=D
// push constant 891
@891
D=A
@SP
M=M+1
A=M-1
M=D
// lt
@SP
M=M-1
A=M-1
D=M
A=A+1
D=D-M
@TRUE5
D;JLT
@0
D=A
@EXIT5
0;JMP
(TRUE5)
@1
D=-A
(EXIT5)
@SP
A=M-1
M=D
// push constant 32767
@32767
D=A
@SP
M=M+1
A=M-1
M=D
// push constant 32766
@32766
D=A
@SP
M=M+1
A=M-1
M=D
// gt
@SP
M=M-1
A=M-1
D=M
A=A+1
D=D-M
@TRUE6
D;JGT
@0
D=A
@EXIT6
0;JMP
(TRUE6)
@1
D=-A
(EXIT6)
@SP
A=M-1
M=D
// push constant 32766
@32766
D=A
@SP
M=M+1
A=M-1
M=D
// push constant 32767
@32767
D=A
@SP
M=M+1
A=M-1
M=D
// gt
@SP
M=M-1
A=M-1
D=M
A=A+1
D=D-M
@TRUE7
D;JGT
@0
D=A
@EXIT7
0;JMP
(TRUE7)
@1
D=-A
(EXIT7)
@SP
A=M-1
M=D
// push constant 32766
@32766
D=A
@SP
M=M+1
A=M-1
M=D
// push constant 32766
@32766
D=A
@SP
M=M+1
A=M-1
M=D
// gt
@SP
M=M-1
A=M-1
D=M
A=A+1
D=D-M
@TRUE8
D;JGT
@0
D=A
@EXIT8
0;JMP
(TRUE8)
@1
D=-A
(EXIT8)
@SP
A=M-1
M=D
// push constant 57
@57
D=A
@SP
M=M+1
A=M-1
M=D
// push constant 31
@31
D=A
@SP
M=M+1
A=M-1
M=D
// push constant 53
@53
D=A
@SP
M=M+1
A=M-1
M=D
// add
@SP
AM=M-1
D=M
@SP
AM=M-1
M=M+D
@SP
M=M+1
// push constant 112
@112
D=A
@SP
M=M+1
A=M-1
M=D
// sub
@SP
AM=M-1
D=M
@SP
AM=M-1
M=M-D
@SP
M=M+1
// neg
@SP
A=M-1
M=-M
// and
@SP
M=M-1
A=M-1
D=M
A=A+1
D=D&M
A=A-1
M=D
// push constant 82
@82
D=A
@SP
M=M+1
A=M-1
M=D
// or
@SP
M=M-1
A=M-1
D=M
A=A+1
D=D|M
A=A-1
M=D
// not
@SP
A=M-1
M=!M
