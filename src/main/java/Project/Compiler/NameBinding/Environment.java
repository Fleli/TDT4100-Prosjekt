package Project.Compiler.NameBinding;

import java.util.ArrayList;
import java.util.List;

import Project.Compiler.Parser.StatementTypes.Declaration;

/**
 * Temporarily just works as wrapper class for Scope. However, once scopes
 * actually become relevant (when functions are supported), the Environment
 * class is important to make a clear distinction between global and local
 * scopes.
 */
public class Environment {
    
    
    private List<Scope> scopes = new ArrayList<Scope>();
    
    
    public Environment() {
        scopes.add ( new Scope(1) );
    }
    
    
    private Scope local() {
        return scopes.get ( scopes.size() - 1 );
    }
    
    
    public void addVariable ( Declaration declaration ) {
        local().addVariable(declaration);
    }
    
    public void pushScope() {
        
        int currentOffset = local().getOffset();
        
        scopes.add( new Scope ( currentOffset ) );
        
    }
    
    public void popScope() {
        
        if ( scopes.size() <= 1 ) {
            throw new IllegalStateException("Cannot remove last scope from Environment (ScopeStackUnderflow)");
        }
        
        scopes.remove ( scopes.size() - 1 );
        
    }
    
    public boolean variableExists ( String name ) {
        
        for ( int index = scopes.size() - 1 ; index >= 0 ; index-- ) {
            
            Scope scope = scopes.get(index);
            
            if ( scope.variableExists(name) ) {
                return true;
            }
            
        }
        
        return false;
        
    }
    
    public int getLocalIndex ( String name ) {
        
        for ( int index = scopes.size() - 1 ; index >= 0 ; index-- ) {
            
            Scope scope = scopes.get(index);
            
            if ( scope.variableExists(name) ) {
                
                if ( scope.getLocalIndex(name) == 0 ) {
                    System.out.println("(in getLocalIndex) Local index of " + name + " is 0.");
                    System.exit(1);
                }
                
                return scope.getLocalIndex(name);
                
            }
            
        }
        
        throw new IllegalArgumentException("Variable " + name + " is not defined.");
        
    }
    
    public int bind_and_get_local_index ( String name ) {
        
        if ( !variableExists(name) ) {
            // TODO: Bedre error handling enn exceptions.
            throw new IllegalStateException("The variable " + name + " is not defined");
        }
        
        int localIndex = getLocalIndex(name);
        
        if ( localIndex == 0 ) {
            System.out.println("Local index of " + name + " is 0");
            System.exit(1);
        }
        
        return localIndex;
        
    }
    
    @Override
    public String toString() {
        
        StringBuilder text = new StringBuilder("Environment (least to most local scopes):");
        
        for ( Scope scope : scopes ) {
            text.append ( "  Scope:\n" + scope );
        }
        
        return text.toString();
        
    }
    
}
