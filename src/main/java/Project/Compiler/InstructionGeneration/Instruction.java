package Project.Compiler.InstructionGeneration;

import Project.Compiler.Parser.Statement;

public class Instruction {
    
    private int opcode_or_operand;
    
    private Statement associatedStatement;
    
    private DebugRegion debugRegion;
    
    public Instruction(int opcode_or_operand, Statement associatedStatement, DebugRegion debugRegion) {
        this.opcode_or_operand = opcode_or_operand;
        this.associatedStatement = associatedStatement;
        this.debugRegion = debugRegion;
    }
    
    public int getOpcode_or_operand() {
        return opcode_or_operand;
    }
    
    public Statement getAssociatedStatement() {
        return associatedStatement;
    }
    
    public DebugRegion getDebugRegion() {
        return debugRegion;
    }
    
}
