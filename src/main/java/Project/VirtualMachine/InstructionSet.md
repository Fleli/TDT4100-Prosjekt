The virtual computer has the following instruction set:


OpCode  Instruction     Description             Clarification
––––––––––––––––––—––––––––––––––––––––––––––––––––––––––––––––––––––––––—–––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
0       END             Exit program            End program normally (exit code 0)
1       PRINT           Print specified string  Read chars from heap, starting at operand, until 0.
-> Operand:     int* stringLocationInHeap
2       NEWVAR          New Variable            Push the value 0 (default for int) to the stack, "creating" a new variable.
3       PUSHFRAME       Push frame to stack     Push a new frame to the stack, used for function calls/new blocks.
4       POPFRAME        Pop frame from stack    Remove the most local frame from the stack, used for function returns
5       ADD                                     Pop two numbers, add, push result
6       SUB                                     Pop two numbers, subtract, push result
7       MUL                                     Pop two numbers, multiply, push result
8       DIV                                     Pop two numbers, divide, push result
9       BITAND                                  Pop two numbers, bitwise &, push result
10      BITOR                                   Pop two numbers, bitwise |, push result
11      BITXOR                                  Pop two numbers, bitwise ^, push result
12      NOTEQ                                   Pop two numbers, push 1 if not equal, 0 otherwise
13      EQ                                      Pop two numbers, push 1 if equal, 0 otherwise
14      MOD                                     Pop two numbers, modulo divide, push result
15      SMALLER                                 Pop two numbers, perform <, push result
16      GREATER                                 Pop two numbers, perform >, push result
17-21   [ UNUSED ]                              Can be used by binary operations added later on.
22      BITNOT                                  Pop a number, flip all bits, push result
23      NEGATE                                  Pop a number, negative it, push result
24-27   [ UNUSED ]                              Can be used by unary operations added later on.
28      PUSHINT                                 Push specific integer
-> Operand:     int integerToPush
29      PUSHVAR         Push variable           Peek at specified offset (operand), push to top of stack
-> Operand:     int localIndex (offset from frame pointer)
30      POPASSIGN       Pop and assign variable Pop from the stack, assign to specified (offset)
-> Operand:     int localIndex (offset from frame pointer)
31      ALLOCATE        Allocate space on heap  Allocate heap space. Size: top of stack is popped. Resulting pointer: Pushed to stack
32      ADJUSTPC        Adjust program counter  Add a number, either negative or positive, to the program counter
-> Operand:     int adjustment (the new program counter's offset from the old one's)
33      ADJUSTIFZERO                            Adjust program counter if the top of the stack (will pop) is zero
-> Operand:     int adjustment (the new program counter's offset from the old one's, if pop() yields 0)
34      ADJUSTSP        Adjust stack pointer    Adjust stack pointer by operand
-> Operand:     int adjustment (the new stack pointer's offset from the old one's)
35      HEAPASSIGN                              Pop heap address and new value (address popped first), write value to heap @ address
36      HEAPFETCH                               Pop heap address from stack, and push the value at that heap address back on stack
37      PRINTINT                                Pop from stack, convert to string, print.
38      NEWLINE                                 Delegates to console, runs ConsoleInstance.newLine()

NOTE to 3, 4:   These will not cover all function call functionality, as return addresses
must be pushed & popped like normal variables upon call & return.

NOTE to 5-21:   These pop two numbers. The number on the top is the second operand, while
the second number to be popped is the first operand. The result of the operation is pushed 
to the stack again, and it is all done in a single Runtime-instruction (but obviously costs 
several JVM instructions). Also: Make som adjustments so that the JVM does not throw
exceptions if the operands are illegal (handle this as a VMException instead).

NOTE to 22-27:  These will pop a number, perform an operation on it, and push the result
back to the top of the stack.

NOTE to 32: The adjustment happens *after* fetching operand.