package Project.Compiler.Optimizer;

public class Optimization {
    
    private int line;
    
    private String type;
    
    public Optimization(int line, String type) {
        this.line = line;
        this.type = type;
    }
    
    public int getLine() {
        return line;
    }
    
    public String getType() {
        return type;
    }
    
}
