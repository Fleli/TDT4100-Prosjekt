package Project.Compiler.NameBinding;

import Project.Compiler.Lexer.Token;
import Project.Compiler.Statements.Declaration;

public class ScopeEntry {
    
    private Declaration declaration;
    private int localIndex;
    
    public ScopeEntry ( Declaration declaration , int localIndex ) {
        this.declaration = declaration;
        this.localIndex = localIndex;
    }
    
    public Token getNameToken() {
        return declaration.getNameToken();
    }
    
    public boolean wasReadFrom() {
        return declaration.wasReadFrom();
    }
    
    public int getLocalIndex(boolean doNotMarkAsRead) {
        
        if (!doNotMarkAsRead) declaration.didReadFrom();
        
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
