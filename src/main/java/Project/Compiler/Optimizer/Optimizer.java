package Project.Compiler.Optimizer;

import java.util.ArrayList;
import java.util.List;

import Project.Compiler.Compiler.Error;
import Project.Compiler.InstructionGeneration.DebugRegion;
import Project.Compiler.Parser.Statement;

public class Optimizer {
    
    public static final int mask_constantFold       =       1 << 0;
    // Legg til flere optimizations her, og oppdater n_optimizations til største left-shift + 1
    
    private List<Optimization> optimizations;
    private List<Statement> program;
    private List<Error> errors;
    
    private int optimizerConfiguration = 1;
    
    public void setOptimizerConfiguration(int optimizerConfiguration) {
        this.optimizerConfiguration = optimizerConfiguration;
    }
    
    public void setProgram(List<Statement> program) {
        this.program = program;
    }
    
    public void optimize() {
        
        optimizations = new ArrayList<Optimization>();
        errors = new ArrayList<Error>();
        
        boolean doConstantFold = (optimizerConfiguration & mask_constantFold) == mask_constantFold;
        
        if (doConstantFold) for (Statement statement : program) {
            statement.constantFold(this);
        }
        
        // for (Optimization optimization : optimizations) {
        //     System.out.println(optimization.getType());
        // }
        
    }
    
    public void submitOptimization(int line, String type) {
        Optimization optimization = new Optimization(line, type);
        optimizations.add(optimization);
    }
    
    public void submitError(String message, DebugRegion debugRegion) {
        Error error = new Error(message, debugRegion, "issue");
        errors.add(error);
    }
    
    public List<Statement> getProgram() {
        return program;
    }
    
    public List<Error> getErrors() {
        return errors;
    }
    
    public List<Optimization> getOptimizations() {
        return optimizations;
    }
    
}
