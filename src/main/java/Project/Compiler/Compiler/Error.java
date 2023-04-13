package Project.Compiler.Compiler;

import Project.Compiler.InstructionGeneration.DebugRegion;
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
     * Instead of a token, a {@code DebugRegion} object might be specified. This allows errors to span over multiple
     * tokens.
     */
    private DebugRegion debugRegion;
    
    /**
     * All errors happen on a specific line.
    */
    private int line;
    
    /**
     * An error might be an issue (so that compilation cannot be completed properly), or
     * it could be a warning if the programmer's code is syntactically correct but might
     * create unexpected behaviour or other problems.
     */
    private String severity;
    
    
    public Error(String message, int line, String severity) {
        this.message = message;
        this.line = line;
        this.severity = severity;
    }
    
    public Error(String message, Token token, String severity) {
        this.message = message;
        this.token = token;
        this.line = token.getLine();
        this.severity = severity;
    }
    
    public Error(String message, DebugRegion debugRegion, String severity) {
        this.message = message;
        this.debugRegion = debugRegion;
        this.line = debugRegion.start_line;
        this.severity = severity;
    }
    
    public int getLine() {
        return line;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public Integer getColumn() {
        
        if (debugRegion != null) {
            return debugRegion.start_col;
        } else if (token != null) {
            return token.startColumn();
        } else {
            return null;
        }
        
    }
    
    public DebugRegion getDebugRegion() {
        return debugRegion;
    }
    
    @Override
    public String toString() {
        
        String prefix = "@Line" + line + "\t\t" + getMessage() + "(" + severity + ")";
        
        if ( token == null ) {
            return prefix;
        } else {
            return prefix + " (at token " + token + ")";
        }
        
    }
    
}
