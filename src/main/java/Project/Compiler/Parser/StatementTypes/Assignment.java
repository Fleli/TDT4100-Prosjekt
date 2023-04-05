package Project.Compiler.Parser.StatementTypes;

import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.Lexer.Token;
import Project.Compiler.NameBinding.Environment;
import Project.Compiler.Parser.Statement;
import Project.Compiler.Parser.Expressions.Expression;

public class Assignment implements Statement {
    
    private Token lhsToken;
    
    /**
     * The {@code String} symbol used to reference this variable.
     */
    private String lhs;
    
    /**
     * The value to assign to this variable.
     */
    private Expression rhs;
    
    /**
     * The local index of {@code lhs} is set during name binding, and is used by the code
     * generator to get the correct stack frame offsets when assigning the new value
     * to the {@code lhs} variable.
     */
    private int localIndexOfLhs;
    
    /**
     * Creates a new {@code Assignment} object with the specified name ({@code String lhs})
     * and new value ({@code Expression rhs}) to assign to the specified name. Note that
     * all variables should be bound to the {@code Environment} of the {@code Parser} object
     * responsible, but this is fixed during {@code bind_names}, which should be called later, 
     * for both {@code lhs} and {@code rhs}. 
     * @param lhs A {@code String} object representing the source code name of 
     * the variable to be assigned.
     * @param rhs An {@code Expression} object representing the value to assign to the
     * specified variable.
     */
    public Assignment(String lhs, Expression rhs, Token lhsToken) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.lhsToken = lhsToken;
    }
    
    
    @Override
    public void bind_names(Environment environment) {
        
        localIndexOfLhs = environment.bind_and_get_local_index(lhs, lhsToken);
        
        rhs.bind_names(environment);
        
    }

    @Override
    public InstructionList generateInstructions(Environment environment) {
        
        InstructionList instructions = new InstructionList();
        
        instructions.add(rhs.generateInstructions(environment));           // Utregning av RHS
        
        // Resultat ligger nå øverst på stack, så vi popper og skriver til framePointer + localIndex
        
        instructions.add ( 30 , this );                 // Pop from stack (result of expression)
        instructions.add ( localIndexOfLhs , this );    // Write to local index, the offset from local frame pointer
        
        return instructions;
        
    }
    
    @Override
    public String description() {
        return lhs + " = " + rhs.description();
    }
    
    @Override
    public String toString() {
        return "assign " + description();
    }
    
}
