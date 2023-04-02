package Project.Compiler.Compiler;

import Project.Compiler.Lexer.Token;

public class Error {
    
    /**
     * All errors must give a message to the user.
    */
    private String message;
    
    /**
     * If one token is erroneous, it can be specified, and will yield more precise error information in the IDE.
     * However, {@code token} is allowed to be {@code null} if no specific token was erroneous.
     * Note: If a token is passed in, its line will be used for the {@code line} property of the error.
    */
    private Token token;
    
    /**
     * All errors happen on a specific line.
    */
    private int line;
    
    
    public Error(String message, int line) {
        this.message = message;
        this.line = line;
    }
    
    public Error(String message, Token token) {
        this.message = message;
        this.token = token;
        this.line = token.getLine();
    }
    
    public int getLine() {
        return line;
    }
    
    public String getMessage() {
        return message;
    }
    
    /**
     * This method will be removed, but is only here to temporarily get rid of any errors while it is under design.
     * @deprecated
     * @return Token
     */
    public Token getToken() {
        return token;
    }
    
    @Override
    public String toString() {
        if ( token == null ) {
            return "@Line" + line + "\t\t" + getMessage();
        } else {
            return "@Line" + line + "\t\t" + getMessage() + " (at token " + token + ")";
        }
    }
    
}
