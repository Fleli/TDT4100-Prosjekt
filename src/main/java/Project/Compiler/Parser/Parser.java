package Project.Compiler.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Project.Compiler.Compiler.Error;
import Project.Compiler.Lexer.Token;
import Project.Compiler.Parser.Expressions.Expression;
import Project.Compiler.Parser.Expressions.ExpressionParser;
import Project.Compiler.Parser.StatementTypes.Assignment;
import Project.Compiler.Parser.StatementTypes.Conditional;
import Project.Compiler.Parser.StatementTypes.Declaration;
import Project.Compiler.Parser.StatementTypes.HeapAssignment;
import Project.Compiler.Parser.StatementTypes.While;

public class Parser {
    
    
    public static final int mask_declaration        =       1 << 0; 
    public static final int mask_assignment         =       1 << 1;
    public static final int mask_conditionals       =       1 << 2;
    public static final int mask_heapAssign         =       1 << 3;
    public static final int mask_while              =       1 << 4;
    // flere masks, for functionDef, functionCall, osv.
    // bruker 1 << n som maskeverdier
    
    private ExpressionParser expressionParser = new ExpressionParser(this);
    
    /**
     * The index in the {@code tokens} input of {@code Token} objects passed by the Compiler's {@code Lexer}.
     */
    private int index = 0;
    
    /**
     * The {@code Parser} object's main input. This list of {@code Token}s is what will be parsed according to the
     * lanugage's grammar. The currently pointed-to {@code Token} object is found using the {@code index} property
     * of this {@code Parser} object.
     */
    private List<Token> tokens;
    
    /**
     * The {@code Parser} object's main output. This list of {@code Statement}s will be produced by the parse
     * function if no errors are detected. The {@code Statement}s may themselves contain {@code Statement}s, and
     * this {@code List} contains the top-level (global) {@code Statement}s that together make up the program.
     */
    private List<Statement> statements;
    
    // Secondary output: errors
    private List<Error> errors;
    
    /**
     * Updates the Parser's internal token list, and deletes previously generated statements and errors (starts fresh with the new input);
     * @param tokens The lexer-generated tokens to pass in for parsing.
     */
    public void setTokens( List<Token> tokens ) {
        
        if ( tokens == null ) throw new IllegalStateException("Expected non-null list of tokens.");
        
        this.tokens = new ArrayList<Token>();
        
        for ( Token token : tokens ) {
            
            if ( token.type() != "comment" ) {
                this.tokens.add(token);
            } else {
                System.out.println("Parser threw out comment-token " + token);
            }
            
        }
        
        errors = null;
        statements = null;
        
    }
    
    public void submitErrorOnToken(String expected) {
        Error newError = new Error("Expected " + expected + " but found '" + tokens.get(index).content() + "'", tokens.get(index));
        errors.add(newError);
    }
    
    public void submitErrorOnCurrentLine(String message) {
        
        int line = 0;
        
        if ( index == tokens.size()  &&  tokens.size() == 0 ) {
            line = 1;
        } else if ( index == tokens.size() ) {
            line = tokens.get(tokens.size() - 1).getLine();
        } else {
            line = tokens.get(index).getLine();
        }
        
        Error newError = new Error(message, line);
        errors.add(newError);
    }
    
    public void parse() {
        
        statements = new ArrayList<Statement>();
        errors = new ArrayList<Error>();
        
        index = 0;
        
        statements = parse ( 127 , new ArrayList<String>() );
        
    }
    
    /**
     * Executes parsing of the previously passed-in tokens.
     * @param masks An {@code int} specifying which statement types are allowed in the current parse. 
     * Use 127 to allow all kinds of existing statements.
     */
    private List<Statement> parse ( int masks , List<String> returnTokenTypes ) {
        
        if ( tokens == null ) throw new IllegalStateException("Cannot parse when no tokens are passed in. Use setTokens() first.");
        
        List<Statement> statements = new ArrayList<Statement>();
        
        boolean allow_declarations  = ( masks & mask_declaration )  == mask_declaration;
        boolean allow_assignments   = ( masks & mask_assignment )   == mask_assignment;
        boolean allow_conditionals  = ( masks & mask_conditionals ) == mask_conditionals;
        boolean allow_heapAssign    = ( masks & mask_heapAssign )   == mask_heapAssign;
        boolean allow_while         = ( masks & mask_while )        == mask_while;
    
        while ( index < tokens.size() ) {
            
            Token token = tokens.get(index);
            
            if ( returnTokenTypes.contains ( token.type() ) ) {
                
                return statements;
                
            } else if ( allow_declarations  &&  token.typeIs("keyword")  &&  token.contentIs("int") ) {
                
                Declaration declaration = parse_declaration();
                
                if ( declaration != null ) {
                    statements.add(declaration);
                }
                
            } else if ( allow_assignments  &&  token.typeIs("identifier") ) {
                
                Assignment assignment = parse_assignment();
                
                if ( assignment != null ) {
                    statements.add(assignment);
                }
                
            } else if ( allow_conditionals  &&  token.typeIs("keyword")  &&  token.contentIs("if") ) {
                
                Conditional conditional = parse_conditional();
                
                if ( conditional != null ) {
                    statements.add(conditional);
                }
                
            } else if ( allow_heapAssign  &&  token.typeIs("keyword")  &&  token.contentIs("heap") ) {
                
                HeapAssignment heapAssignment = parse_heapAssignment();
                
                if ( heapAssignment != null ) {
                    statements.add(heapAssignment);
                }
                
            } else if ( allow_while  &&  token.typeIs("keyword")  &&  token.contentIs("while") ) {
                
                While loop = parse_while();
                
                if ( loop != null ) {
                    statements.add(loop);
                }
                
            } else if ( token.typeIs(";") ) {
                
                incrementIndex();
                
            }
            
            else {
                submitErrorOnToken("statement (at " + index + ")");
                // lag en form for error recovery her, for eksempel panikk frem til semikolon e.l.
                // merk også at denne delen ikke er ferdig. I stedet for bare if-else vil det komme
                // if-else if-...-else for å støtte flere statements.
                return statements;
            }
            
        }
        
        return statements;
        
    }
    
    public List<Statement> getStatements() {
        
        if ( statements == null ) throw new IllegalStateException("The parse() method must be called for statements to be generated.");
        
        return new ArrayList<Statement>(statements);
        
    }
    
    public List<Error> getErrors() {
        
        if ( errors == null ) throw new IllegalStateException("The parse() method must be called for errors to be generated.");
        
        return new ArrayList<Error>(errors);
        
    }
    
    public boolean wasErroneous() {
        return ( errors.size() > 0 );   
    }
    
    public void incrementIndex() {
        index++;
    }
    
    public boolean inputIsExhausted() {
        return ( index == tokens.size() );
    }
    
    private Declaration parse_declaration() {
        
        // vi vet at første er int, så vi går videre til neste
        incrementIndex();
        
        int pointerDepth = 0;
        
        // mens neste er *, øker pointer-dybden.
        while ( index < tokens.size()  &&  tokens.get(index).typeIs("operator")  &&  tokens.get(index).contentIs("*") ) {
            pointerDepth++;
            incrementIndex();
        }
        
        if ( inputIsExhausted() ) {
            submitErrorOnCurrentLine("Expected name of variable.");
            return null;
        }
        
        if ( !tokens.get(index).typeIs("identifier") ) {
            submitErrorOnToken("name of variable");
            return null;
        }
        
        String name = tokens.get(index).content();
        Token nameToken = tokens.get(index);
        
        incrementIndex();
        
        // Nå forbi navnet, neste SKAL være ;
        // merk at senere vil dette endres slik at neste også kan være =, men foreløpig kun ; godkjent.
        
        if ( inputIsExhausted() ) {
            submitErrorOnCurrentLine("Expected ; to end declaration.");
            return null;
        }
        
        if ( token().typeIs(";") ) {
            incrementIndex();
            return new Declaration(pointerDepth, name, nameToken);
        }
        
        // Hvis det *ikke* kom et ; etter navnet, skal det være =, for å gi initial value.
        
        if ( !token().typeIs("=") ) {
            submitErrorOnToken("=");
            return null;
        }
        
        incrementIndex();       
        // Har nå bekreftet at det var =, så neste er expression
        
        if ( inputIsExhausted() ) {
            submitErrorOnCurrentLine("Expected expression to assign initial value to " + name);
            return null;
        }
        
        Expression rhs = expressionParser.parse(1);
        
        if ( rhs == null ) {
            return null;
        }
        
        if ( isExhaustedOrNotSpecificType(";", "Expected ; to finish declaration.") ) {
            return null;
        }
        
        incrementIndex();
        
        return new Declaration(pointerDepth, name, rhs, nameToken);
        
    }
    
    private Assignment parse_assignment() {
        
        // Vi vet at første token må være av typen identifier siden vi i det hele tatt kom inn i funksjonen.
        String lhs = token().content();
        Token lhsToken = token();
        
        incrementIndex();
        
        if ( isExhaustedOrNotSpecificType("=", "Unused identifier " + lhs) ) {
            return null;
        }
        
        incrementIndex();
        
        if ( inputIsExhausted() ) {
            submitErrorOnCurrentLine("Expected expression on right-hand side of '=' in assignment.");
            return null;
        }
        
        Expression rhs = expressionParser.parse(1);
        
        if ( rhs == null ) {
            return null;
        }
        
        if ( isExhaustedOrNotSpecificType(";", "Expected ; to finish assignment") ) {
            return null;
        }
        
        incrementIndex();
        
        return new Assignment(lhs, rhs, lhsToken);
        
    }
    
    private Conditional parse_conditional() {
        
        // Første er 'if', som vi allerede har bekreftet
        incrementIndex();
        
        Expression condition = expressionParser.parse(1);
        
        if ( condition == null ) {
            return null;
        }
        
        if ( isExhaustedOrNotSpecificType("{", "Expected { to begin conditional body") ) {
            return null;
        }
        
        incrementIndex();
        
        List<Statement> body = parse(127, new ArrayList<String>( Arrays.asList ( "}" ) ) );
        
        if ( isExhaustedOrNotSpecificType("}", "Expected } to end conditional body") ) {
            return null;
        }
        
        incrementIndex();
        
        if ( inputIsExhausted()  ||  ! ( token().typeIs("keyword") && token().contentIs("else") ) ) {        // Ingen else etterpå
            return new Conditional(condition, body);
        }
        
        // vi *vet* at else kommer nå, fordi dette er det eneste tilfellet hvor if ovenfor feiler
        incrementIndex();
        
        if ( inputIsExhausted() ) {
            submitErrorOnCurrentLine("Expected 'if' after 'else' to complete non-primary instruction path.");
            return null;
        }
        
        if ( ! ( token().typeIs("keyword") && token().contentIs("if") ) ) {
            submitErrorOnToken("if");
            return null;
        }
        
        // if we find an 'if', the pattern 'else if' is recognized, and so we parse the next
        // conditional and add it as the 'otherwise' path in our original conditional.
        Conditional otherwise = parse_conditional();
        
        return new Conditional(condition, body, otherwise);
        
    }
    
    private HeapAssignment parse_heapAssignment() {
        
        // We know that we've already encountered the "heap" keyword, so we skip past it.
        incrementIndex();
        
        // Next, we expect an Expression that describes the heap address to write to
        Expression address = expressionParser.parse(1);
        
        if ( address == null ) {
            return null;
        }
        
        // Now we expect an '=' token
        if ( isExhaustedOrNotSpecificType("=", "Expected '=' to assign to heap address") ) {
            return null;
        }
        
        // We found an '=' sign, so we skip past it
        incrementIndex();
        
        // Next, we expect a new Expression that describes the new value to assign
        // to the specified heap address
        Expression value = expressionParser.parse(1);
        
        if ( value == null ) {
            return null;
        }
        
        // Finally, we must confirm that the statement ends with ';'
        if ( isExhaustedOrNotSpecificType(";", "Expected ';' to complete heap assignment") ) {
            return null;
        }
        
        incrementIndex();
        
        return new HeapAssignment(address, value);
        
    }
    
    private While parse_while() {
        
        // We know that we must have encountered the keyword "while", so we skip past it
        incrementIndex();
        
        // Next, we generate an Expression for the while's condition
        Expression condition = expressionParser.parse(1);
        
        if ( condition == null ) {
            return null;
        }
        
        // Assert that next is {
        if ( isExhaustedOrNotSpecificType("{", "Expected '{' to begin body of loop") ) {
            return null;
        }
        
        // Current is '{', so we skip past it
        incrementIndex();
        
        // Next, we generate the loop's body. The body may contain any statement (for now)
        // so we use 127 for its mask. Also, the body stops when we hit '}', which indicates
        // the end of the "while" statement
        int statement_masks = 127;                  // TODO: Adjust this mask later on
        List<String> terminator = new ArrayList<String>( Arrays.asList( "}" ) );
        List<Statement> body = parse(statement_masks, terminator);
        
        // Assert that body is not null (in which case something went wrong during parsing)
        if ( body == null ) {
            return null;
        }
        
        // Assert that the next token is '}'
        if ( isExhaustedOrNotSpecificType("}", "Expected '}' to complete body of loop") ) {
            return null;
        }
        
        // Current token is confirmed to be '}', so we skip past it
        incrementIndex();
        
        return new While(condition, body);
        
    }
    
    // TODO: Add more statements here (marked with todo so blue line shows up)
    
    public Token token() {
        return tokens.get(index);
    }
    
    /**
    * Will verify that this {@code Parser} object has at least one {@code Token} object left 
    * in its input, and that the current token is of a specific type. If either of these 
    * conditions fail, an error message is submitted.
    * @param expectedType The token type that is expected if the input stream is not exhausted.
    * @param exhaustedMessage An error message to submit if the input stream is exhausted.
    * @return Will return {@code true} if either of the two conditions fail (and an error message
    * is submitted), and return {@code false} otherwise (if "everything is fine").
    */
   public boolean isExhaustedOrNotSpecificType ( String expectedType , String exhaustedMessage ) {
       
       if ( inputIsExhausted() ) {
           submitErrorOnCurrentLine(exhaustedMessage);
           return true;
       }
       
       if ( !token().typeIs(expectedType) ) {
           submitErrorOnToken(expectedType);
           return true;
       }
       
       return false;
       
   }
    
}
