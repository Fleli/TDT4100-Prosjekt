package Project.Compiler.NameBinding;

import Project.Compiler.Parser.StatementTypes.Declaration;

public class ScopeEntry {
    
    private Declaration declaration;
    private int localIndex;
    
    public ScopeEntry ( Declaration declaration , int localIndex ) {
        this.declaration = declaration;
        this.localIndex = localIndex;
    }
    
    public int getLocalIndex() {
        return localIndex;
    }
    
    public String getName() {
        return declaration.getName();
    }
    
    public int getPointerDepth() {
        return declaration.getPointerDepth();
    }
    
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("(" + localIndex + ")");
        while (ret.length() < 6) ret.append(" ");
        ret.append(declaration);
        return ret.toString();
    }
    
}
