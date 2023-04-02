package Project.Compiler.Parser.StatementTypes;

import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.NameBinding.Environment;
import Project.Compiler.Parser.Statement;
import Project.Compiler.Parser.Expressions.Expression;

public class HeapAssignment implements Statement {
    
    
    private Expression address;
    private Expression value;
    
    
    public HeapAssignment ( Expression address , Expression value ) {
        
        if ( address == null  ||  value == null ) {
            throw new IllegalArgumentException("Both arguments of heap assignment must be non-null");
        }
        
        this.address = address;
        this.value = value;
        
    }
    

    @Override
    public void bind_names(Environment environment) {
        
        address.bind_names(environment);
        value.bind_names(environment);
        
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
        instructions.add(35 , this);
        
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
    
}
