package Project.Compiler.Statements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Project.Compiler.InstructionGeneration.DebugRegion;
import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.Lexer.Token;
import Project.Compiler.NameBinding.Environment;
import Project.Compiler.Optimizer.Optimizer;
import Project.Compiler.Parser.Operator;
import Project.Compiler.Parser.Statement;

/**
 * Arithmetic expression. Can be either unary/binary operation, literal value or symbol reference. Implements
 * the {@code Statement} interface, so that code can be generated directly by the {@code Expression} nodes
 * themselves. Note also that name binding is done by the {@code Expression} objects, for the same reason.
 */
public class Expression implements Statement {
    
    private String type;
    
    private int literalValue;
    private String stringLiteral;
    
    private Token token;
    
    private String reference;
    private Token referenceToken;
    private int localIndex;
    
    private Expression arg1;
    private Expression arg2;
    private Operator operator;
    
    private DebugRegion debugRegion;
    
    public Expression(int literalValue, Token token) {
        
        type = "literal";
        
        this.literalValue = literalValue;
        
        debugRegion = new DebugRegion(token);
        
    }
    
    public Expression(int literalValue, DebugRegion debugRegion) {
        
        type = "literal";
        
        this.literalValue = literalValue;
        this.debugRegion = debugRegion;
        
    }
    
    /**
     * Wrap a reference or string literal in an {@code Expression} object. Note that name binding should happen
     * later. Thus, this constructor does not access any {@code Environment}s.
     * @param string The variable reference or string literal to wrap.
     * @param token The {@code Token} object which the reference or string literal is part of.
     * @param isReference If {@code true}, will create a {@code reference Expression} object. Otherwise,
     * create a {@code stringLiteral Expression} object
     */
    public Expression(String string, Token token, boolean isReference) {
        
        if (isReference) {
                
            type = "reference";
            this.reference = string;
            this.referenceToken = token;
            
        } else {
            
            type = "stringLiteral";
            this.stringLiteral = string;
            this.token = token;
            
        }
        
        debugRegion = new DebugRegion(token);
        
    }
    
    public Expression(Expression arg1, Operator operator, Token operatorToken) {
        
        type = "unary";
        
        this.arg1 = arg1;
        this.operator = operator;
        
        DebugRegion operatorRegion = new DebugRegion(operatorToken);
        debugRegion = new DebugRegion(operatorRegion, arg1.debugRegion);
        
    }
    
    public Expression ( Expression arg1 , Expression arg2 , Operator operator ) {
        
        type = "binary";
        
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.operator = operator;
        
        debugRegion = new DebugRegion(arg1.debugRegion, arg2.debugRegion);
        
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
    public Expression ( Expression expression , String type , Token debugToken ) {
        
        if ( !singleExpressionTypes.contains(type) ) {
            throw new IllegalArgumentException("Cannot create single expression object with unknown type " + type);
        }
        
        this.type = type;
        this.arg1 = expression;
        
        this.debugRegion = new DebugRegion(debugToken);
        
    }
    
    
    @Override
    public void bind_names(Environment environment) {
        
        // TODO: Sjekk at error faktisk er submitted dersom noe er null, og ikke burde være det
        
        if ( type.equals("reference") ) {
            
            localIndex = environment.bind_and_get_local_index(reference, referenceToken, false);
            
        } else if ( type.equals("unary") || type.equals("alloc")  ||  type.equals("heapAccess") ) {
            
            if ( arg1 == null ) {
                return;
            }
            
            arg1.bind_names(environment);
            
        } else if ( type.equals("binary") ) {
            
            if ( arg1 == null  ||  arg2 == null ) {
                return;
            }
            
            arg1.bind_names(environment);
            arg2.bind_names(environment);
            
        }
        
    }
    
    public void constantFold(Optimizer optimizer) {
        
        if (type.equals("binary")) {
            
            arg1.constantFold(optimizer);
            arg2.constantFold(optimizer);
            
            if (arg1.type.equals("literal") && arg2.type.equals("literal")) {
                
                int a = arg1.literalValue;
                int b = arg2.literalValue;
                
                boolean isDivision = (operator.getSyntax() == "/" || operator.getSyntax() == "%");
                
                System.out.println(arg1.debugRegion);
                System.out.println(arg2.debugRegion);
                System.out.println(debugRegion);
                
                if (isDivision && b == 0) {
                    optimizer.submitError(
                        "Division by zero will yield runtime error.", 
                        arg2.debugRegion
                    );
                    return;
                }
                
                debugRegion = new DebugRegion(arg1.debugRegion, arg2.debugRegion);
                
                type = "literal";
                literalValue = operator.apply(a, b);
                
                optimizer.submitOptimization(
                    debugRegion.start_line, 
                    "Constant fold: " + a + " " + operator.getSyntax() + " " + b + " -> " + literalValue
                );
                
                arg1 = null;
                arg2 = null;
                operator = null;
                
            }
            
        } else if (type.equals("unary")) {
            
            arg1.constantFold(optimizer);
            
            if (arg1.type.equals("literal")) {
                
                int a = arg1.literalValue;
                
                type = "literal";
                literalValue = operator.apply(a);
                
                debugRegion = new DebugRegion(arg1.debugRegion, debugRegion);
                
                optimizer.submitOptimization(
                    debugRegion.start_line, 
                    "Constant fold (unary)"
                );
                
            }
            
        } else if (type == "heapAccess"  ||  type == "alloc") {
            
            arg1.constantFold(optimizer);
            
        }
        
    }
    
    @Override
    public InstructionList generateInstructions(Environment environment) {
        
        InstructionList instructions = new InstructionList();
        
        if ( type.equals("binary") ) {
            
            instructions.add(arg1.generateInstructions(environment));
            instructions.add(arg2.generateInstructions(environment));
            instructions.add(operator.getInstruction(), this, debugRegion);
            
        } else if ( type.equals("unary") ) {
            
            instructions.add(arg1.generateInstructions(environment));
            instructions.add(operator.getInstruction(), this, debugRegion);
            
        } else if ( type.equals("literal") ) {
            
            instructions.add(28, this, debugRegion);                 // PUSHINT instruction
            instructions.add(literalValue, this, debugRegion);       // Operand: The value to push
            
        } else if ( type.equals("reference") ) {
            
            instructions.add(29, this, debugRegion);                 // PUSHVAR
            instructions.add(localIndex, this, debugRegion);         // Operand: The variable's offset from the stack's frame pointer in local scope
            
        } else if ( type.equals("alloc") ) {
            
            instructions.add(arg1.generateInstructions(environment));       // Start by finding the actual size to allocate
            instructions.add(31, this, debugRegion);                        // ALLOCATE
            
        } else if ( type.equals("heapAccess") ) {
            
            instructions.add(arg1.generateInstructions(environment));       // Start by finding the address to fetch from
            instructions.add(36, this, debugRegion);                        // HEAPFETCH
            
        } else if ( type.equals("stringLiteral") ) {
            
            // Trenger plass for alle chars i stringen, pluss én for string-terminatoren (0)
            int length_including_terminator = stringLiteral.length() + 1;
            
            // Lager en allocation siden string literals plasseres på heap
            Expression size_of_alloc = new Expression(length_including_terminator, debugRegion);
            Expression alloc = new Expression(size_of_alloc, "alloc", token);
            
            // Legger til instruksjonene for allokering av området. Nå vil pointer til
            // heap-området hvor stringen skal ligge, være på topp av stack.
            InstructionList instructions_alloc = alloc.generateInstructions(environment);
            instructions.add(instructions_alloc);
            
            // Går gjennom hver char og skriver til heap. Bruker (39) HEAPOFFSET som
            // instruksjon, og offset (stadig økende) som operand
            for ( int index = 0 ; index < stringLiteral.length() ; index++ ) {
                
                char c = stringLiteral.charAt(index);
                
                instructions.add(28, this, debugRegion);     // (28)     PUSHINT
                instructions.add(c, this, debugRegion);      // Operand  Verdi av char som skal skrives
                
                instructions.add(39, this, debugRegion);     // (39)     HEAPOFFSET (skriv til heap på offset)
                instructions.add(index, this, debugRegion);  // Operand  Offset fra heap base
                
            }
            
            // TODO: Skriv 0 eksplisitt til (stringLiteral.length())-offset på heap 
            // for å null-terminere strings (har fungert så langt fordi heapen er stor nok)
            
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
        } else if ( type == "stringLiteral" ) {
            return "string literal: " + stringLiteral;
        } else {
            return "ERROR_TYPE: " + type;
        }
        
    }
    
    public Token string_literal_in_expression() {
        
        if (type == "unary") {
            
            return arg1.string_literal_in_expression();
            
        } else if (type == "binary") {
            
            Token token_arg1 = arg1.string_literal_in_expression();
            
            if (token_arg1 != null) {
                return token_arg1;
            }
            
            return arg2.string_literal_in_expression();
            
        } else if (type == "stringLiteral") {
            
            return token;
            
        } else {
            
            return null;
            
        }
        
    }
    
    @Override
    public int getLine() {
        return debugRegion.start_line;
    }
    
}
