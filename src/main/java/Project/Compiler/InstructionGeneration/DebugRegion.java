package Project.Compiler.InstructionGeneration;

import Project.Compiler.Lexer.Token;

public class DebugRegion {
    
    public int start_line;
    public int start_col;
    
    public int end_line;
    public int end_col;
    
    /**
     * Creates a {@code DebugRegion} object with specified starting line and column, and ending line and column.
     * @param start_line
     * @param start_col
     * @param end_line
     * @param end_col
     */
    public DebugRegion(int start_line, int start_col, int end_line, int end_col) {
        this.start_line = start_line;
        this.start_col = start_col;
        this.end_line = end_line;
        this.end_col = end_col;
    }
    
    /**
     * Creates a {@code DebugRegion} object stretching from one to another, and everywhere in-between.
     * @param beginning The start {@code DebugRegion}
     * @param end The end {@code DebugRegion}
     */
    public DebugRegion(DebugRegion beginning, DebugRegion end) {
        this.start_line = beginning.start_line;
        this.start_col = beginning.start_col;
        this.end_line = end.end_line;
        this.end_col = end.end_col;
    }
    
    /**
     * Creates a {@code DebugRegion} object around a specific token
     * @param token The {@code Token} object to create a debug region around.
     */
    public DebugRegion(Token token) {
        start_line = token.getLine();
        start_col = token.startColumn();
        end_line = token.getLine();
        end_col = token.startColumn() + token.content().length() - 1;
    }
    
    /**
     * Creates a {@code DebugRegion} object stretching from the beginning of one
     * token to the end of another.
     * @param start The beginning token, inclusive
     * @param end The ending token, inclusive
     */
    public DebugRegion(Token start, Token end) {
        DebugRegion region_start = new DebugRegion(start);
        DebugRegion region_end = new DebugRegion(end);
        this.start_line = region_start.start_line;
        this.start_col = region_start.start_col;
        this.end_line = region_end.end_line;
        this.end_col = region_end.end_col;
    }
    
    /**
     * Checks whether a token lies inside this {@code DebugRegion} object,
     * edges inclusive.
     * @param token The token to check
     * @return Whether the token lies within this {@code DebugRegion} object's
     * horizontal coordinates.
     */
    public boolean columnContains(Token token) {
        
        boolean afterStart = start_col <= token.startColumn();
        boolean beforeEnd = end_col >= token.startColumn();
        
        return (afterStart && beforeEnd);
        
    }
    
    @Override
    public String toString() {
        return "DebugRegion(" + start_line + " " + start_col + " -> " + end_line + " " + end_col + ")";
    }
    
}
