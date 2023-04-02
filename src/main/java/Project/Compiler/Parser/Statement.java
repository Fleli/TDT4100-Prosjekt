package Project.Compiler.Parser;

import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.NameBinding.Environment;

public interface Statement {
    
    public void bind_names ( Environment environment );
    
    public InstructionList generateInstructions( Environment environment );
    
    public String description();
    
}
