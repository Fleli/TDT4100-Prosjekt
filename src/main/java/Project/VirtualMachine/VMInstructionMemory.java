package Project.VirtualMachine;

import Project.Compiler.InstructionGeneration.Instruction;
import Project.Compiler.InstructionGeneration.InstructionList;

public class VMInstructionMemory {
    
    private InstructionList instructions;
    
    private int size;
    
    private int program_counter = 0;
    
    public VMInstructionMemory ( InstructionList instructions ) {
        
        this.instructions = instructions;
        
        size = instructions.size();
        
    }
    
    public Instruction getNextInstruction() throws VMException {
        
        Instruction instruction = instructions.get(program_counter);
        
        program_counter += 1;
        
        if ( program_counter > size ) {
            System.out.println("Program counter is " + program_counter);
            throw new VMException("Program counter rolled over", "instruction memory");
        }
        
        return instruction;
        
    }
    
    public void adjust_program_counter ( int adjustment ) {
        program_counter += adjustment;
    }
    
    public int getProgramCounter() {
        return program_counter;
    }
    
    public int peekInstruction() {
        return instructions.getExeData(program_counter);
    }
    
}
