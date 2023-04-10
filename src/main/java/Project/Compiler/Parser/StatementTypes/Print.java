package Project.Compiler.Parser.StatementTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Project.Compiler.InstructionGeneration.DebugRegion;
import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.Lexer.Token;
import Project.Compiler.NameBinding.Environment;
import Project.Compiler.Parser.Statement;
import Project.Compiler.Parser.Expressions.Expression;

public class Print implements Statement {
    
    public static final List<String> allowed_types = new ArrayList<String>( Arrays.asList(
        "int", "string"
    ) );
    
    private boolean is_println;
    private String print_type;
    
    private Expression argument;
    
    private DebugRegion debugRegion;
    
    public Print(boolean is_println, String print_type, Expression argument, Token start, Token end) {
        
        if (argument == null) {
            throw new IllegalArgumentException("Expression must be non-null");
        }
        
        if ( !allowed_types.contains(print_type) ) {
            throw new IllegalArgumentException("Cannot print " + print_type);
        }
        
        this.is_println = is_println;
        this.print_type = print_type;
        this.argument = argument;
        
        debugRegion = new DebugRegion(start, end);
        
    }
    
    @Override
    public void bind_names(Environment environment) {
        argument.bind_names(environment);
    }

    @Override
    public InstructionList generateInstructions(Environment environment) {
        
        InstructionList instructions = new InstructionList();
        
        // Generer først expression, slik at vi kan bruke resultatet
        instructions.add(argument.generateInstructions(environment));
        
        // Videre er det print-typen som avgjør hva som skjer.
        if (print_type.equals("int")) {
            
            // Logikken for int-printing ligger i VM, så vi generer bare selve print-instruksjonen.
            instructions.add(37, this, debugRegion);
            
        } else if (print_type.equals("string")) {
            
            // Logikken for string-printing ligger i VM, så vi generer bare selve print-instruksjonen.
            instructions.add(1, this, debugRegion);
            
        } else {
            
            throw new IllegalStateException("Type is " + print_type + ", which is undefined.");
            
        }
        
        // Legg også (optionally) til \n-printing.
        if (is_println) {
            instructions.add(38, this, debugRegion);
        }
        
        return instructions;
        
    }
    
    @Override
    public String description() {
        return "print (line: " + is_println + "): " + argument.description();
    }
    
    @Override
    public int getLine() {
        return debugRegion.start_line;
    }
    
}
