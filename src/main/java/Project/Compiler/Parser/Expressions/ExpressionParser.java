package Project.Compiler.Parser.Expressions;

import java.util.ArrayList;
import java.util.List;

import Project.Compiler.Lexer.Token;
import Project.Compiler.Parser.Parser;

public class ExpressionParser {
    
    private Parser parser;
    
    private List<Operator> operators = new ArrayList<Operator>();
    
    private int min;
    private int max;
    
    public ExpressionParser ( Parser parser ) {
        
        this.parser = parser;
        
        operators.add( new Operator("infix", "|", 1) );
        
        operators.add( new Operator("infix", "^", 2) );
        
        operators.add( new Operator("infix", "&", 3) );
        
        operators.add( new Operator("infix", "==", 4) );
        operators.add( new Operator("infix", "!=", 4) );
        
        operators.add( new Operator("infix", "<", 5) );
        operators.add( new Operator("infix", ">", 5) );
        
        operators.add( new Operator("infix", "+", 6) );
        operators.add( new Operator("infix", "-", 6) );
        
        operators.add( new Operator("infix", "*", 7) );
        operators.add( new Operator("infix", "/", 7) );
        operators.add( new Operator("infix", "%", 7) );
        
        // legg til ! her senere
        
        for ( Operator operator : operators ) {
            
            int precedence = operator.getPrecedence();
            
            min = Math.min(min, precedence);
            max = Math.max(max, precedence);
            
        }
        
    }
    
    public Expression parse(int level) {
        
        if ( parser.inputIsExhausted() ) {
            parser.submitErrorOnCurrentLine("Expected value but found end of file.");
            return null;
        }
        
        if ( level > max ) {
            
            Token token = parser.token();
            Expression node = null;
            
            if ( token.typeIs("(") ) {
                node = parseSubExpression();
            } else if ( token.typeIs("identifier") ) {
                node = parseSymbolReference();
            } else if ( token.typeIs("intLiteral") ) {
                node = parseIntLiteral();
            } else if ( token.typeIs("keyword")  &&  token.contentIs("alloc") ) {
                node = parseAlloc();
            } else if ( token.typeIs("keyword")  &&  token.contentIs("heap") ) {
                node = parseHeapAccess();
            } else if ( token.typeIs("stringLiteral") ) {
                node = parseStringLiteral();
            } else {
                parser.submitErrorOnToken("expression");
                incrementIndex();
            }
            
            return node;
            
        } else {
            
            Expression arg1 = parse(level + 1);
            
            Operator operator = find_operator(level);
            
            while ( operator != null ) {
                
                String position = operator.getPosition();
                
                if ( position.equals("infix") ) {
                    
                    Expression arg2 = parse(level + 1);
                    
                    arg1 = new Expression(arg1, arg2, operator);
                    
                } else if ( position.equals("postfix") ) {
                    
                    arg1 = new Expression(arg1, operator);
                    
                } else {
                    
                    // TODO: Verifiser unreachable og fjern denne.
                    throw new IllegalStateException("Fatal error, reached unreachable.");
                    
                }
                
                operator = find_operator(level);
                
            }
            
            return arg1;
            
        }
        
        
        
    }
    
    private Operator find_operator ( int level ) {
        
        if ( parser.inputIsExhausted() ) return null;
        
        Token token = parser.token();
        
        if ( !token.typeIs("operator") ) return null;
        
        String content = token.content();
        
        List<Operator> operator = operators
            .stream()
            .filter( op -> op.getPrecedence() == level )
            .filter( op -> op.getSyntax().equals(content) )
            .toList();
        
        if ( operator.size() == 0 ) return null;
        
        parser.incrementIndex();
        
        return operator.get(0);
        
    }
    
    private Expression parseSubExpression() {
        
        parser.incrementIndex();
        
        Expression node = parse(min);
        
        if ( parser.isExhaustedOrNotSpecificType(")", "Expected ')' to complete sub-expression.") ) {
            return null;
        }
        
        parser.incrementIndex();
        
        return node;
        
    }
    
    /**
     * Parses a symbol reference, for example if {@code index} or {@code x} appears
     * in an expression. Only parses variable references for now, but support
     * for function calls might come later.
     * @return The {@code Expression} object wrapping the symbol reference.
     */
    private Expression parseSymbolReference() {
        
        String reference = parser.token().content();
        Token referenceToken = parser.token();
        
        parser.incrementIndex();
        
        return new Expression(reference, referenceToken, true);
        
    }
    
    private Expression parseIntLiteral() {
        
        String content = parser.token().content();
        
        parser.incrementIndex();
        
        if ( content.equals("true") ) {
            return new Expression(1);
        } else if ( content.equals("false") ) {
            return new Expression(0);
        } else {
            // TODO: Sjekk at exceptions her faktisk er unreachable, 
            // TODO: eller wrap i try-catch
            return new Expression ( Integer.parseInt(content) );
        }
        
    }
    
    private Expression parseAlloc() {
        
        // Current token must be 'alloc'
        incrementIndex();
        
        // Assert that next is '('
        if ( parser.isExhaustedOrNotSpecificType("(", "Expected number of words to be allocated") ) {
            return null;
        }
        
        incrementIndex();
        
        Expression allocatedWords = parse(1);
        
        // TODO: Sjekk om det ikke en trengs en null-check her
        
        if ( parser.isExhaustedOrNotSpecificType(")", "Expected ')' to complete specifiation of requested allocation size.") ) {
            return null;
        }
        
        incrementIndex();
        
        return new Expression ( allocatedWords , "alloc" );
        
    }
    
    private Expression parseHeapAccess() {
        
        // Current token must be 'heap'
        incrementIndex();
        
        // Assert that next is '('
        if ( parser.isExhaustedOrNotSpecificType("(", "Expected address for heap access encloses in ()") ) {
            return null;
        }
        
        // If past the check, current must be '(', so we just skip past it
        incrementIndex();
        
        // Next, we generate the Expression node for the heap access address
        Expression address = parse(1);
        
        if ( address == null ) {
            return null;
        }
        
        // Assert that next is ')', since we just finished the heap access address Expression
        if ( parser.isExhaustedOrNotSpecificType(")", "Expected ')' to complete heap access") ) {
            return null;
        }
        
        // Since the current token is ')', we skip past it
        incrementIndex();
        
        return new Expression(address, "heapAccess");
        
    }
    
    private Expression parseStringLiteral() {
        
        Token token = parser.token();
        
        StringBuilder including_quotes = new StringBuilder(token.content());
        including_quotes.deleteCharAt(0);
        including_quotes.deleteCharAt(including_quotes.length() - 1);
        
        incrementIndex();
        
        String string = including_quotes.toString();
        
        return new Expression(string, token, false);
        
    }
    
    private void incrementIndex() {
        parser.incrementIndex();
    }
    
}
