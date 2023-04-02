package Project.Compiler.Lexer;

public class Token {
    
    private String type;
    
    private int line;
    private int startColumn;
    
    private StringBuilder content = new StringBuilder();
    
    public Token(String type, int line, int startcolumn) {
        this.type = type;
        this.line = line;
        this.startColumn = startcolumn;
    }
    
    public String content() {
        return content.toString();
    }
    
    public String type() {
        return type;
    }
    
    public int startColumn() {
        return startColumn;
    }
    
    public int getLine() {
        return line;
    }
    
    /**
     * Updates the token's type.
     * @param newType The token's new type, which will override its initial type.
     */
    public void setType(String newType) {
        this.type = newType;
    }
    
    /**
     * Append a character to the token's content, thus increasing its length by 1 and extending its content String.
     * @param c The {@code char} to be added.
     */
    public void append(char c) {
        content.append(c);
    }
    
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("" + line);
        while ( text.length() < 4 ) text.append(' ');
        text.append("" + startColumn);
        while ( text.length() < 8 ) text.append(' ');
        text.append(type);
        while ( text.length() < 22 ) text.append(' ');
        text.append(content.toString());
        return text.toString();
    }
    
    /**
     * Checks whether this {@code Token} object has a specific type.
     * @param type The type to compare to this {@code Token}'s {@code type} property.
     * @return Equality between the passed-in {@code String} object and this object's {@code type}.
     */
    public boolean typeIs(String type) {
        return this.type.equals(type);
    }
    
    /**
     * Checks whether this {@code Token} object has specific content.
     * @param content The content to compare to this {@code Token}'s {@code content} property.
     * @return Equality between the passed-in {@code String} object and this object's {@code content}.
     */
    public boolean contentIs(String content) {
        return this.content().equals(content);
    }
    
}
