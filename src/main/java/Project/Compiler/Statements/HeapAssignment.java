package Project.Compiler.Statements;

import Project.Compiler.InstructionGeneration.DebugRegion;
import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.Lexer.Token;
import Project.Compiler.NameBinding.Environment;
import Project.Compiler.Optimizer.Optimizer;
import Project.Compiler.Parser.Statement;

public class HeapAssignment implements Statement {
    
    private Expression address;
    private Expression value;
    
    private DebugRegion debugRegion;
    
    public HeapAssignment(Expression address, Expression value, Token start, Token end) {
        
        if ( address == null  ||  value == null ) {
            throw new IllegalArgumentException("Both arguments of heap assignment must be non-null");
        }
        
        this.address = address;
        this.value = value;
        
        debugRegion = new DebugRegion(start, end);
        
    }
    
    @Override
    public void bind_names(Environment environment) {
        address.bind_names(environment);
        value.bind_names(environment);
    }
    
    public void constantFold(Optimizer optimizer) {
        address.constantFold(optimizer);
        value.constantFold(optimizer);
    }
    
    @Override
    public InstructionList generateInstructions(Environment environment) {
        
        InstructionList instructions = new InstructionList();
        
        // Genererer først value sin instructions, deretter address sine. Da
        // sørger vi for at address havner på topp, som er viktig fordi address
        // poppes først når HEAPASSIGN (35) skal executes.
        instructions.add(value.generateInstructions(environment));
        instructions.add(address.generateInstructions(environment));
        
        // Etter at value og address er pushet (i denne rekkefølgen),
        // kan vi poppe dem og utføre HEAPASSIGN (35)
        instructions.add(35 , this, debugRegion);
        
        return instructions;
        
    }
    
    @Override
    public String description() {
        return "heap(" + address.description() + ") = " + value.description();
    }
    
    @Override
    public String toString() {
        return "heap " + address.description() + " = " + value.description();
    }
    
    @Override
    public int getLine() {
        return debugRegion.start_line;
    }
    
}
