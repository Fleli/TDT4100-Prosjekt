package Project.VirtualMachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import Project.Console;
import Project.Compiler.InstructionGeneration.Instruction;
import Project.Compiler.InstructionGeneration.InstructionList;
import Project.VirtualMachine.Heap.VMHeap;

public class Runtime {
    
    private static final List<String> opcodeNames = new ArrayList<>( Arrays.asList(
        "END", "PRINT", "NEWVAR", "PUSHFRAME", "POPFRAME", "ADD", "SUB", "MUL", "DIV", "BITAND", "BITOR", "BITXOR",
        "NOTEQ", "EQ", "MOD", "SMALLER", "GREATER", "NOTINUSE17", "NOTINUSE18", "NOTINUSE19", "NOTINUSE20",
        "NOTINUSE21", "BITNOT", "NEGATE", "NOTINUSE24", "NOTINUSE25", "NOTINUSE26", "NOTINUSE27", "PUSHINT",
        "PUSHVAR", "POPASSIGN", "ALLOCATE", "ADJUSTPC", "ADJUSTATZERO", "ADJUSTSP", "HEAPASSIGN", "HEAPFETCH"
    ) );
    
    private VMInstructionMemory instructionMemory;
    
    private Console console;
    
    private VMHeap heap;
    private VMStack stack;
    
    private boolean shouldExit = false;
    
    public static boolean printDebugInfo = false;
    
    public Runtime ( InstructionList instructions , int requiredStack , int requiredHeap , Console console ) {
        
        this.console = console;
        this.instructionMemory = new VMInstructionMemory(instructions);
        
        heap = new VMHeap(requiredHeap);
        stack = new VMStack(requiredStack);
        
    }
    
    public void clock() throws VMException {
        
        Instruction instruction = instructionMemory.getNextInstruction();
        int opcode = instruction.getOpcode_or_operand();
        
        if ( printDebugInfo ) {
            StringBuilder s = new StringBuilder("Next instruction: " + opcode);
            while (s.length() < 30) s.append(" ");
            if ( instruction.getAssociatedStatement() != null ) s.append(instruction.getAssociatedStatement().description());
            System.out.println(s.toString());
        }
        
        switch ( opcode ) {
            
            case 0: {                           //      END
                System.out.println("didSetShouldExit");
                shouldExit = true;
                break;
            } case 1: {                         //      PRINT
                instruction_PRINT();
                break;
            } case 2: {                         //      NEWVAR
                stack.push(0);
                break;
            } case 3: {                         //      PUSHFRAME
                stack.newFrame();
                break;
            } case 4: {                         //      POPFRAME
                stack.removeFrame();
                break;
            } case 5:                           //      ADD
              case 6:                           //      SUB
              case 7:                           //      MUL
              case 8:                           //      DIV
              case 9:                           //      BITWISE &
              case 10:                          //      BITWISE |
              case 11:                          //      BITWISE ^
              case 12:                          //      !=
              case 13:                          //      ==
              case 14:                          //      %
              case 15:                          //      <
              case 16:                          //      >
              case 17:                          //  [ UNUSED ]
              case 18:                          //  [ UNUSED ]
              case 19:                          //  [ UNUSED ]
              case 20:                          //  [ UNUSED ]
              case 21: {                        //  [ UNUSED ]
                instruction_BINARY_ARITHMETIC(opcode);
                break;
            } case 22:                          //      BITWISE !       (Flip bits)
              case 23:                          //      NEGATION
              case 24:                          //  [ UNUSED ]
              case 25:                          //  [ UNUSED ]
              case 26:                          //  [ UNUSED ]
              case 27: {                        //  [ UNUSED ]
                instruction_UNARY_ARITHMETIC(opcode);
                break;
            } case 28: {
                instruction_PUSHINT();
                break;
            } case 29: {
                instruction_PUSHVAR();
                break;
            } case 30: {
                instruction_POPASSIGN();
                break;
            } case 31: {
                instruction_ALLOCATE();
                break;
            } case 32: {
                instruction_ADJUSTPC();
                break;
            } case 33: {
                instruction_ADJUSTATZERO();
                break;
            } case 34: {
                instruction_ADJUSTSP();
                break;
            } case 35: {
                instruction_HEAPASSIGN();
                break;
            } case 36: {
                instruction_HEAPFETCH();
                break;
            } default: {
                throw new VMException("Unrecognized instruction " + opcode, "instruction decoder");
            }
            
        }
        
        if ( printDebugInfo ) {
            System.out.println("  Stack: " + stack.getFirst(16) + " ... ]");
        }
        
    }
    
    public boolean shouldExit() {
        return shouldExit;
    }
    
    public void run() throws VMException {
        while ( !shouldExit ) {
            clock();
        }
    }
    
    public void printStack() {
        System.out.println(stack);
    }
    
    public int getStackElement ( int index ) throws VMException {
        return stack.getElementAtIndex(index);
    }
    
    public void printHeap() {
        System.out.println(heap);
    }
    
    public int getHeapElement ( int index ) throws VMException {
        return heap.getData(index);
    }
    
    private void instruction_PRINT() throws VMException {
        
        int stringPointer = instructionMemory.getNextInstruction().getOpcode_or_operand();
        int initialPointer = stringPointer;
        
        int data = heap.getData(stringPointer);
        
        StringBuilder output = new StringBuilder();
        
        while ( data != 0 ) {
            
            output.append(data);
            
            stringPointer = (stringPointer + 1) % (heap.getSize());
            
            if ( stringPointer == initialPointer ) {
                throw new VMException("Never-ending string", "print handler");
            }
            
            data = heap.getData(stringPointer);
            
        }
        
        console.print(output.toString());
        
    }
    
    private void instruction_BINARY_ARITHMETIC ( int instruction ) throws VMException {
        
        int operatorIndex = instruction - 5;
        
        int arg1 = stack.pop();
        int arg2 = stack.pop();
        
        List<BinaryOperator<Integer>> operators = new ArrayList<BinaryOperator<Integer>>( Arrays.asList(
            (y , x) -> x + y,
            (y , x) -> x - y,
            (y , x) -> x * y,
            (y , x) -> x / y,
            (y , x) -> x & y,
            (y , x) -> x | y,
            (y , x) -> x ^ y,
            (y , x) -> ( x != y ) ? 1 : 0, 
            (y , x) -> ( x == y ) ? 1 : 0,
            (y , x) -> x % y,
            (y , x) -> ( x < y ) ? 1 : 0,
            (y , x) -> ( x > y ) ? 1 : 0
        ) );
        
        // TODO: Her må kanskje noe ekstra sjekking til for å unngå arithmetic
        // TODO: overflows i selve Java-runtime.
        int result = operators.get(operatorIndex).apply(arg1, arg2);
        
        stack.push(result);
        
    }
    
    private void instruction_UNARY_ARITHMETIC(int instruction) throws VMException {
        
        int operatorIndex = instruction - 22;
        
        int arg1 = stack.pop();
        
        List<UnaryOperator<Integer>> operators = new ArrayList<UnaryOperator<Integer>>( Arrays.asList(
            (x) -> ~x,
            (x) -> -x
        ) );
        
        // TODO: Her må kanskje noe ekstra sjekking til for å unngå arithmetic overflows
        // TODO: i selve Java-runtime.
        int result = operators.get(operatorIndex).apply(arg1);
        
        stack.push(result);
        
    }
    
    private void instruction_PUSHINT() throws VMException {
        int intToPush = instructionMemory.getNextInstruction().getOpcode_or_operand();
        stack.push(intToPush);
    }
    
    private void instruction_PUSHVAR() throws VMException {
        
        int offset = instructionMemory.getNextInstruction().getOpcode_or_operand();
        
        if ( printDebugInfo ) {
            System.out.println("  (DebugInfo) PushVar with offset " + offset);
        }
        
        int value = stack.peekAtFramePointerOffset(offset);
        stack.push(value);
        
    }
    
    private void instruction_POPASSIGN() throws VMException {
        int value = stack.pop();
        int offset = instructionMemory.getNextInstruction().getOpcode_or_operand();
        stack.writeToFramePointerOffset(value, offset);
    }
    
    private void instruction_ALLOCATE() throws VMException {
        int size = stack.pop();
        int pointer = heap.alloc(size);
        stack.push(pointer);
    }
    
    private void instruction_ADJUSTPC() throws VMException {
        
        int adjustment = instructionMemory.getNextInstruction().getOpcode_or_operand();
        instructionMemory.adjust_program_counter(adjustment);
        
    }
    
    private void instruction_ADJUSTATZERO() throws VMException {
        
        int adjustment = instructionMemory.getNextInstruction().getOpcode_or_operand();
        int condition = stack.pop();
        
        if ( condition == 0 ) {
            instructionMemory.adjust_program_counter(adjustment);
        }
        
    }
    
    private void instruction_ADJUSTSP() throws VMException {
        int adjustment = instructionMemory.getNextInstruction().getOpcode_or_operand();
        stack.adjust_stack_pointer(adjustment);
    }
    
    private void instruction_HEAPASSIGN() throws VMException {
        int address = stack.pop();
        int value = stack.pop();
        heap.setData(address, value);
    }
    
    private void instruction_HEAPFETCH() throws VMException {
        int address = stack.pop();
        int value = heap.getData(address);
        stack.push(value);
    }
    
    // TODO: Her kan flere instruksjoner legges til (merker med TODO slik at blålinja dukker opp)
    
    static public String instructionWithOpCode ( int opcode ) {
        
        return opcodeNames.get(opcode);
        
    }
    
    public int getStackPointer() {
        return stack.getStackPointer();
    }
    
    @Override
    public String toString() {
        
        return "Stack: " + stack + "\nHeap: " + heap;
        
    }
    
}
