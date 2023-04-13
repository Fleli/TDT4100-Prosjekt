package Project.Compiler.Lexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Project.Compiler.Compiler.Error;

public class Lexer {
    
    private final static List<String> keywords = new ArrayList<String>( Arrays.asList( 
        "int", "alloc", "if", "else", "heap", "while", "println", "print", "string", "dealloc"
    ) );
    
    private final static String initId = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String initOp = "+-*/%&|<>!~";
    
    // Note that '=' can both lead to '=' (control) and '==' (operator), but starts out as '=' (control)
    // and is later changed *if* it is followed by another '='
    
    private final static String initCtrl = "={}[]()@;";
    
    private final static String identifierChars = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final static String intLiteralChars = "0123456789";
    
    private int index = 0;
    
    private int line = 1; // line, starts at 1
    private int col = 0; // column (index) starts at 0
    
    private String text;
    private List<Token> tokens;
    private List<Error> errors;
    
    /**
     * Updates the {@code Lexer} object's {@code text} property so that {@code lex()} can be called later on, using the passed-in {@code String} object.
     * @param input The {@code String} object containing the source code of the program to be lexed.
     * @throws IllegalArgumentException Throws if the passed-in text contains illegal characters, see {@code Lexer.allowedInput} for legal characters.
     */
    public void setInput(String input) {
        text = input;
    }
    
    private void submitError(String message, Token errorToken) {
        Error newError = new Error(message, errorToken, "issue");
        errors.add(newError);
    }
    
    public List<Error> getErrors() {
        return errors;
    }
    
    public void lex() {
        
        if ( text == null ) throw new IllegalStateException("Cannot lex when specified input is null. Use the setInput(String) method to specify an input.");
        
        index = 0;
        line = 1;
        col = 0;
        
        tokens = new ArrayList<Token>();
        errors = new ArrayList<Error>();
        
        while ( index < text.length() ) {
            
            char c = text.charAt(index);
            
            if ( initId.indexOf(c) != -1 ) {
                lex_identifier();
            } else if ( intLiteralChars.indexOf(c) != -1 ) {
                lex_intLiteral();
            } else if ( initOp.indexOf(c) != -1 ) {
                lex_operator();
            } else if ( initCtrl.indexOf(c) != -1 ) {
                lex_control();
            } else if ( c == ' ' ) {
                incrementIndex();
            } else if ( c == '\n' ) {
                incrementIndex();
                newLine();
            } else if ( c == '#' ) {
                lex_comment();
            } else if ( c == '"' ) {
                lex_stringLiteral();
            } else {
                recover_from_illegal_char();
            }
            
        }
        
    }
    
    public List<Token> getTokens() {
        
        if ( tokens == null ) throw new IllegalStateException("No tokens have been created. Use the lex() method to make sure they are.");
        
        return new ArrayList<>(tokens);
    }
    
    private void newLine() {
        line++;
        col = 0;
    }
    
    private void incrementIndex() {
        index++;
        col++;
    }
    
    private void recover_from_illegal_char() {
        Token token = new Token("error", line, col);
        appendAndIncrement(token);
        tokens.add(token);
    }
    
    /**
     * Appends the character currently being pointed to by {@code index} to the passed-in {@code Token} object. Then increments {@code index}, so that the current character is consumed.
     * @param token The {@code Token} object which should receive the currently pointed-to character.
     */
    private void appendAndIncrement(Token token) {
        char c = text.charAt(index);
        token.append(c);
        incrementIndex();
    }
    
    private void checkIfKeyword(Token token) {
        
        if ( keywords.contains(token.content()) ) {
            token.setType("keyword");
        } else {
            token.setType("identifier");
        }
        
    }
    
    /**
     * Checks whether the current character pointed to by {@code index} is allowed in some specified structure, for instance identifiers.
     * @param chars A string containing the allowed characters
     * @return {@code boolean} indicating whether the current character is in {@code chars}
     */
    private boolean currentIsIn(String chars) {
        char current = text.charAt(index);
        int stringIndex = chars.indexOf(current);
        return stringIndex != -1;
    }
    
    private void lex_identifier() {
        
        Token token = new Token("identifier", line, col);
        
        while ( index < text.length()  &&  currentIsIn(identifierChars) ) {
            appendAndIncrement(token);
            checkIfKeyword(token);
        }
        
        tokens.add(token);
        
    }
    
    private void lex_intLiteral() {
        Token token = new Token("intLiteral", line, col);
        while ( index < text.length()  &&  currentIsIn(intLiteralChars) ) appendAndIncrement(token);
        tokens.add(token);
    }
    
    private void lex_operator() {
        
        Token token = new Token("operator", line, col);
        
        char a = text.charAt(index);
        
        appendAndIncrement(token);
        
        if ( index >= text.length() ) {
            tokens.add(token);
            return;
        }
        
        char b = text.charAt(index);
        
        if (a == '!' &&  b == '=') {
            appendAndIncrement(token);
        }
        
        tokens.add(token);
        
    }
    
    private void lex_control() {
        
        Token token = new Token("control", line, col);
        
        appendAndIncrement(token);
        
        token.setType(token.content());
        
        if ( index < text.length()  &&  currentIsIn("=") ) {
            appendAndIncrement(token);
            token.setType("operator");
        }
        
        tokens.add(token);
        
    }
    
    private void lex_comment() {
        Token token = new Token("comment", line, col);
        while ( index < text.length()  &&  !currentIsIn("\n") ) appendAndIncrement(token);
        tokens.add(token);
    }
    
    private void lex_stringLiteral() {
        
        Token token = new Token("stringLiteral", line, col);
        appendAndIncrement(token);
        
        while ( index < text.length()  &&  !currentIsIn("\"\n") ) {
            appendAndIncrement(token);
        }
        
        if ( index >= text.length() ) {
            submitError("Expected '\"' before end of line to terminate string literal.", token);
        } else {
            appendAndIncrement(token);
        }
        
        tokens.add(token);
        
    }
    
}
