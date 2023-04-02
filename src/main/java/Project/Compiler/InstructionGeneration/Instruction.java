package Project.Compiler.InstructionGeneration;

import Project.Compiler.Parser.Statement;

public class Instruction {
    
    private int opcode_or_operand;
    
    private Statement associatedStatement;
    
    public Instruction ( int opcode_or_operand , Statement associatedStatement ) {
        this.opcode_or_operand = opcode_or_operand;
        this.associatedStatement = associatedStatement;
    }
    
    public int getOpcode_or_operand() {
        return opcode_or_operand;
    }
    
    public Statement getAssociatedStatement() {
        return associatedStatement;
    }
    
}
