package Project.VirtualMachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import Project.Compiler.InstructionGeneration.DebugRegion;
import Project.Compiler.InstructionGeneration.Instruction;
import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.Parser.StatementTypes.Declaration;
import Project.Views.ViewIDE.DebugArea.ConsoleView;
import Project.Views.ViewIDE.LanguageDelegates.Delegate_f.VMDB.VMDebugger;
import Project.VirtualMachine.Heap.VMHeap;
import Project.VirtualMachine.Heap.VMHeapArea;

public class Runtime {
    
    private static final List<String> opcodeNames = new ArrayList<>( Arrays.asList(
        "END", "PRINT", "NEWVAR", "PUSHFRAME", "POPFRAME", "ADD", "SUB", "MUL", "DIV", "BITAND", "BITOR", "BITXOR",
        "NOTEQ", "EQ", "MOD", "SMALLER", "GREATER", "NOTINUSE17", "NOTINUSE18", "NOTINUSE19", "NOTINUSE20",
        "NOTINUSE21", "BITNOT", "NEGATE", "NOTINUSE24", "NOTINUSE25", "NOTINUSE26", "NOTINUSE27", "PUSHINT",
        "PUSHVAR", "POPASSIGN", "ALLOCATE", "ADJUSTPC", "ADJUSTATZERO", "ADJUSTSP", "HEAPASSIGN", "HEAPFETCH",
        "PRINTINT", "NEWLINE", "HEAPOFFSET", "DEALLOC"
    ) );
    
    private VMInstructionMemory instructionMemory;
    
    private ConsoleView console;
    
    private VMHeap heap;
    private VMStack stack;
    
    private boolean shouldExit = false;
    
    private boolean isDebug;
    private VMDebugger debugger;
    private DebugRegion activeDebugRegion;
    
    public static boolean printDebugInfo = false;
    
    private List<VMHeapArea> memoryLeaks;
    
    public Runtime(InstructionList instructions, int requiredStack, int requiredHeap, ConsoleView console) {
        
        this.console = console;
        this.instructionMemory = new VMInstructionMemory(instructions);
        
        heap = new VMHeap(requiredHeap);
        stack = new VMStack(requiredStack);
        
        isDebug = false;
        
    }
    
    public Runtime(InstructionList instructions, int requiredStack, int requiredHeap, ConsoleView console, VMDebugger debugger) {
        
        this.console = console;
        this.instructionMemory = new VMInstructionMemory(instructions);
        
        heap = new VMHeap(requiredHeap);
        stack = new VMStack(requiredStack);
        
        isDebug = true;
        this.debugger = debugger;
        
    }
    
    public List<VMHeapArea> getMemoryLeaks() {
        return memoryLeaks;
    }
    
    public DebugRegion getActiveDebugRegion() {
        return activeDebugRegion;
    }
    
    public void clock() throws VMException {
        
        Instruction instruction = instructionMemory.getNextInstruction();
        
        if (isDebug) {
            
            activeDebugRegion = instruction.getDebugRegion();
            
            int opcode = instruction.getOpcode_or_operand();
            debugger.setLastInstruction_opcode(opcode);
            
            String name = opcodeNames.get(opcode);
            debugger.setLastInstruction_name(name);
            
            if ( opcode == 28 || opcode == 29 || opcode == 30 || opcode == 32 || opcode == 33 || opcode == 34 || opcode == 39 ) {
                int operand = instructionMemory.peekInstruction();
                debugger.setLastInstruction_operand(operand);
            } else {
                debugger.setLastInstruction_operand(null);
            }
            
            int program_counter = instructionMemory.getProgramCounter();
            debugger.setProgram_counter(program_counter);
            
        }
        
        int opcode = instruction.getOpcode_or_operand();
        
        if ( printDebugInfo ) {
            StringBuilder s = new StringBuilder("Next instruction: " + opcode);
            while (s.length() < 30) s.append(" ");
            if ( instruction.getAssociatedStatement() != null ) s.append(instruction.getAssociatedStatement().description());
            System.out.println(s.toString());
        }
        
        switch ( opcode ) {
            
            case 0: {                           //      END
                shouldExit = true;
                memoryLeaks = heap.getUsed();
                break;
            } case 1: {                         //      PRINT
                instruction_PRINT();
                break;
            } case 2: {                         //      NEWVAR
                instruction_NEWVAR(instruction);
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
                instruction_ALLOCATE(instruction);
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
            } case 37: {
                instruction_PRINTINT();
                break;
            } case 38: {
                instruction_NEWLINE();
                break;
            } case 39: {
                instruction_HEAPOFFSET();
                break;
            } case 40: {
                instruction_DEALLOC();
                break;
            }
            default: {
                throw new VMException("Unrecognized instruction " + opcode, "instruction decoder");
            }
            
        }
        
        if (printDebugInfo) {
            System.out.println("  Stack: " + stack.getFirst(16) + " ... ]");
        }
        
        if (isDebug) {
            debugger.refresh(stack.getStack());
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
    
    private void instruction_NEWVAR(Instruction instruction) throws VMException {
        
        stack.push(0);
        
        if (isDebug) {
            
            try {
                Declaration declaration = (Declaration) instruction.getAssociatedStatement();
                debugger.push_new_var(declaration);
            } catch (Exception e) {
                System.out.println("Exception: " + e.getLocalizedMessage());
                throw new VMException("Could not convert " + instruction.getAssociatedStatement().description() + " to Declaration in NEWVAR", "execution");
            }
            
        }
        
    }
    
    private void instruction_PRINT() throws VMException {
        
        int stringPointer = stack.pop();
        int initialPointer = stringPointer;
        
        int data = heap.getData(stringPointer);
        
        StringBuilder output = new StringBuilder();
        
        while ( data != 0 ) {
            
            char[] nextChar = Character.toChars(data);
            output.append(nextChar);
            
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
    
    private void instruction_ALLOCATE(Instruction instruction) throws VMException {
        
        int line = instruction.getAssociatedStatement().getLine();
        
        int size = stack.pop();
        int pointer = heap.alloc(size, line);
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
        
        if (isDebug) {
            debugger.readjust(stack.getStackPointer());
        }
        
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
    
    private void instruction_PRINTINT() throws VMException {
        int argument = stack.pop();
        String text = Integer.toString(argument);
        console.print(text);
    }
    
    private void instruction_NEWLINE() throws VMException {
        console.newLine();
    }
    
    private void instruction_HEAPOFFSET() throws VMException {
        int value = stack.pop();
        int heap_base_address = stack.peek(1);
        int heap_base_offset = instructionMemory.getNextInstruction().getOpcode_or_operand();
        int heap_address = heap_base_address + heap_base_offset;
        heap.setData(heap_address, value);
    }
    
    private void instruction_DEALLOC() throws VMException {
        int pointer = stack.pop();
        heap.dealloc(pointer);
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
