package Project.Compiler.Compiler;

public class CompilerProfiler {
    
    public static final int n_stages = 5;
    
    private long start;
    
    // lex, parse, bind, optimize, generate
    private Long[] profileTimes;
    
    public CompilerProfiler() {
        
        start = System.currentTimeMillis();
        
        profileTimes = new Long[n_stages];
        
    }
    
    public void finishedStage(int stage) {
        profileTimes[stage] = System.currentTimeMillis() - start;
    }
    
    public Long time(int i) {
        return profileTimes[i];
    }
    
}
