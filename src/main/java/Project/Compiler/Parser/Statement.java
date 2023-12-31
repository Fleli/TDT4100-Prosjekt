package Project.Compiler.Parser;

import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.NameBinding.Environment;
import Project.Compiler.Optimizer.Optimizer;

public interface Statement {
    
    public void bind_names ( Environment environment );
    
    public InstructionList generateInstructions( Environment environment );
    
    public String description();
    
    public int getLine();
    
    public void constantFold(Optimizer optimizer);
    
}
