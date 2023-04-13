package Project.Compiler.Parser.Expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Operator {
    
    /**
     * A list of all the unary operators with native VM support. These are listed in the same
     * order as the VM instructions, so that conversion from String to Integer happens swiftly
     * and becomes less error-prone.
     */
    private final static List<String> unaryOperators = new ArrayList<String>( Arrays.asList(
        "~", "-"
    ) );
    
    /**
     * A list of all the binary operators with native VM support. These are listed in the same
     * order as the VM instructions, so that conversion from String to Integer happens swiftly
     * and becomes less error-prone.
     */
    private final static List<String> binaryOperators = new ArrayList<String>( Arrays.asList(
        "+", "-", "*", "/", "&", "|", "^", "!=", "==", "%", "<", ">"
    ) );
    
    private String syntax;
    
    private String position;
    
    private int precedence;
    
    public Operator(String position, String syntax, int precedence) {
        
        this.position = position;
        this.syntax = syntax;
        this.precedence = precedence;
        
    }
    
    public String getPosition() {
        return position;
    }
    
    public String getSyntax() {
        return syntax;
    }
    
    public int getPrecedence() {
        return precedence;
    }
    
    public int getInstruction() {
        
        if ( position.equals("infix") ) {
            return binaryOperators.indexOf(syntax) + 5;
        } else if ( position.equals("prefix")  ||  position.equals("postfix") ) {
            return unaryOperators.indexOf(syntax) + 22;
        } else {
            throw new IllegalStateException("No such operation " + syntax + " exists in VM 'hardware' specification.");
        }
        
    }

    public Integer apply(Integer t, Integer u) {
        
        switch (syntax) {
            case "+":
                return t + u;
            case "-":
                return t - u;
            case "*":
                return t * u;
            case "/":
                return t / u;
            case "&":
                return t & u;
            case "|":
                return t | u;
            case "^":
                return t ^ u;
            case "!=":
                return (t != u) ? 1 : 0;
            case "==":
                return (t == u) ? 1 : 0;
            case "%":
                return t % u;
            case "<":
                return (t < u) ? 1 : 0;
            case ">":
                return (t > u) ? 1 : 0;
            default:
                System.exit(1);
                throw new IllegalStateException("Unknown syntax " + syntax);
        }
        
    }
    
    public Integer apply(int a) {
        
        switch (syntax) {
            case "~":
                return ~a;
            case "-":
                return -a;
            default:
                System.exit(1);
                throw new IllegalStateException("Unknown syntax " + syntax);
                
        }
        
    }
    
}
