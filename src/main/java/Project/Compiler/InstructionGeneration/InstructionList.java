package Project.Compiler.InstructionGeneration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Project.Compiler.Parser.Statement;

public class InstructionList implements Iterable<Instruction> {
    
    
    private List<Instruction> instructions = new ArrayList<Instruction>();
    
    
    public void add(int opcode_or_operand, Statement associatedStatement, DebugRegion debugRegion) {
        
        Instruction instruction = new Instruction(opcode_or_operand, associatedStatement, debugRegion);
        instructions.add(instruction);
        
    }
    
    public void add (List<Integer> exeList, Statement associatedStatement, DebugRegion debugRegion) {
        
        for ( Integer integer : exeList ) {
            add(integer, associatedStatement, debugRegion);
        }
        
    }
    
    public void add ( List<Instruction> instructionsToAdd ) {
        
        instructions.addAll(instructionsToAdd);
        
    }
    
    public void add ( InstructionList otherList ) {
        
        for ( Instruction instruction : otherList ) {
            instructions.add(instruction);
        }
        
    }
    
    public int size() {
        return instructions.size();
    }
    
    
    public int getExeData( int index ) {
        return instructions.get(index).getOpcode_or_operand();
    }
    
    public String getAssociatedStatement( int index ) {
        Instruction instruction = instructions.get(index);
        Statement statement = instruction.getAssociatedStatement();
        if ( statement == null ) return "";
        return statement.description();
    }
    
    public Instruction get(int index) {
        return instructions.get(index);
    }

    @Override
    public Iterator<Instruction> iterator() {
        return instructions.iterator();
    }
    
}
