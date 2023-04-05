package Project.Compiler.Parser.Expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.Lexer.Token;
import Project.Compiler.NameBinding.Environment;
import Project.Compiler.Parser.Statement;

/**
 * Arithmetic expression. Can be either unary/binary operation, literal value or symbol reference. Implements
 * the {@code Statement} interface, so that code can be generated directly by the {@code Expression} nodes
 * themselves. Note also that name binding is done by the {@code Expression} objects, for the same reason.
 */
public class Expression implements Statement {
    
    private String type;
    
    private int literalValue;
    
    private String reference;
    private Token referenceToken;
    private int localIndex;
    
    private Expression arg1;
    private Expression arg2;
    private Operator operator;
    
    public Expression ( int literalValue ) {
        type = "literal";
        this.literalValue = literalValue;
    }
    
    /**
     * Wrap a reference in an {@code Expression} object. Note that name binding should happen
     * later. Thus, this constructor does not access any {@code Environment}s.
     * @param reference The symbol that is being referenced.
     */
    public Expression ( String reference , Token referenceToken ) {
        type = "reference";
        this.reference = reference;
        this.referenceToken = referenceToken;
    }
    
    public Expression ( Expression arg1 , Operator operator ) {
        type = "unary";
        this.arg1 = arg1;
        this.operator = operator;
    }
    
    public Expression ( Expression arg1 , Expression arg2 , Operator operator ) {
        type = "binary";
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.operator = operator;
    }
    
    private static final List<String> singleExpressionTypes = new ArrayList<String>( Arrays.asList(
        "alloc", "heapAccess"
    ) );
    
    /**
     * Creates an {@code Expression} object of the specified type, which must be among
     * those listed in {@code singleExpressionTypes} declared in {@code Expression.java}.
     * @param expression The {@code Expression} object to assign to {@code arg1}.
     * @param type The {@code type} object (a {@code String}) of this {@code Expression}.
     */
    public Expression ( Expression expression , String type ) {
        
        if ( !singleExpressionTypes.contains(type) ) {
            throw new IllegalArgumentException("Cannot create single expression object with unknown type " + type);
        }
        
        this.type = type;
        this.arg1 = expression;
        
    }
    
    
    @Override
    public void bind_names(Environment environment) {
        
        if ( type.equals("reference") ) {
            
            localIndex = environment.bind_and_get_local_index(reference, referenceToken);
            
        } else if ( type.equals("unary") || type.equals("alloc")  ||  type.equals("heapAccess") ) {
            
            arg1.bind_names(environment);
            
        } else if ( type.equals("binary") ) {
            
            arg1.bind_names(environment);
            arg2.bind_names(environment);
            
        }
        
    }

    @Override
    public InstructionList generateInstructions(Environment environment) {
        
        InstructionList instructions = new InstructionList();
        
        if ( type.equals("binary") ) {
            
            instructions.add(arg1.generateInstructions(environment));
            instructions.add(arg2.generateInstructions(environment));
            instructions.add(operator.getInstruction(), this);
            
        } else if ( type.equals("unary") ) {
            
            instructions.add(arg1.generateInstructions(environment));
            instructions.add(operator.getInstruction(), this);
            
        } else if ( type.equals("literal") ) {
            
            instructions.add(28, this);                 // PUSHINT instruction
            instructions.add(literalValue, this);       // Operand: The value to push
            
        } else if ( type.equals("reference") ) {
            
            instructions.add(29, this);                 // PUSHVAR
            instructions.add(localIndex, this);         // Operand: The variable's offset from the stack's frame pointer in local scope
            
        } else if ( type.equals("alloc") ) {
            
            instructions.add(arg1.generateInstructions(environment));               // Start by finding the actual size to allocate
            instructions.add(31, this);                                             // ALLOCATE
            
        } else if ( type.equals("heapAccess") ) {
            
            instructions.add(arg1.generateInstructions(environment));               // Start by finding the address to fetch from
            instructions.add(36, this);                                             // HEAPFETCH
            
        } else {
            
            throw new IllegalStateException("Unknown type " + type);
            
        }
        
        return instructions;
        
    }

    @Override
    public String description() {
        
        if ( type == "literal" ) {
            return "" + literalValue;
        } else if ( type == "reference" ) {
            return reference;
        } else if ( type == "unary" ) {
            return operator.getSyntax() + "(" + arg1.description() + ")";
        } else if ( type == "binary" ) {
            return "(" + arg1.description() + " " + operator.getSyntax() + " " + arg2.description() + ")";
        } else if ( type == "alloc" ) {
            return "alloc(" + arg1.description() + ")";
        } else if ( type == "heapAccess" ) {
            return "heap(" + arg1.description() + ")";
        } else {
            return "ERROR_TYPE: " + type;
        }
        
    }
    
}
