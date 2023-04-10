package Project.Compiler.Parser.StatementTypes;

import Project.Compiler.InstructionGeneration.DebugRegion;
import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.Lexer.Token;
import Project.Compiler.NameBinding.Environment;
import Project.Compiler.Parser.Statement;
import Project.Compiler.Parser.Expressions.Expression;

public class Dealloc implements Statement {
    
    private Expression pointer;
    
    private DebugRegion debugRegion;
        
    public Dealloc(Expression pointer, Token start, Token end) {
        
        if (pointer == null) {
            throw new IllegalArgumentException("Pointer must be non-null");
        }
        
        this.pointer = pointer;
        
        debugRegion = new DebugRegion(start, end);
        
    }
    
    @Override
    public void bind_names(Environment environment) {
        pointer.bind_names(environment);
    }
    
    @Override
    public InstructionList generateInstructions(Environment environment) {
        
        InstructionList instructions = pointer.generateInstructions(environment);
        
        instructions.add(40, this, debugRegion);        // (40) DEALLOC
        
        return instructions;
        
    }
    
    @Override
    public String description() {
        return "dealloc (" + pointer.description() + ")";
    }
    
    @Override
    public int getLine() {
        return debugRegion.start_line;
    }
    
}
