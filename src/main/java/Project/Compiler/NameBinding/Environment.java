package Project.Compiler.NameBinding;

import java.util.ArrayList;
import java.util.List;

import Project.Compiler.Parser.StatementTypes.Declaration;

import Project.Compiler.Compiler.Error;
import Project.Compiler.Lexer.Token;

/**
 * Temporarily just works as wrapper class for Scope. However, once scopes
 * actually become relevant (when functions are supported), the Environment
 * class is important to make a clear distinction between global and local
 * scopes.
 */
public class Environment {
    
    
    private List<Scope> scopes = new ArrayList<Scope>();
    
    private List<Error> errors = new ArrayList<Error>();
    
    
    public Environment() {
        scopes.add ( new Scope(1, this) );
    }
    
    
    public void submitError(Error error) {
        this.errors.add(error);
    }
    
    public List<Error> getErrors() {
        return errors;
    }
    
    private Scope local() {
        return scopes.get ( scopes.size() - 1 );
    }
    
    
    public void addVariable ( Declaration declaration ) {
        local().addVariable(declaration);
    }
    
    public void pushScope() {
        
        int currentOffset = local().getOffset();
        
        scopes.add( new Scope(currentOffset, this) );
        
    }
    
    public void submitNeverReadWarning(ScopeEntry entry) {
        
        Error error = new Error(
            "The variable " + entry.getName() + " was never read from.",
            entry.getNameToken(),
            "warning"
        );
        
        errors.add(error);
        
    }
    
    public void verifyUsedDeclarationsInGlobal() {
        scopes.get(0).verifyUsedDeclarations();
    }
    
    public void popScope() {
        
        if ( scopes.size() <= 1 ) {
            throw new IllegalStateException("Cannot remove last scope from Environment (ScopeStackUnderflow)");
        }
        
        Scope scope = scopes.get(scopes.size() - 1);
        
        scope.verifyUsedDeclarations();
        
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
    
    public int getLocalIndex(String name, Token referenceToken, boolean doNotMarkAsRead) {
        
        for ( int index = scopes.size() - 1 ; index >= 0 ; index-- ) {
            
            Scope scope = scopes.get(index);
            
            if ( scope.variableExists(name) ) {
                return scope.getLocalIndex(name, referenceToken, doNotMarkAsRead);
            }
            
        }
        
        submitError( new Error(
            "This context does not define the variable " + name,
            referenceToken,
            "issue"
        ));
        return 0;
        
    }
    
    public int bind_and_get_local_index(String name, Token referenceToken, boolean doNotMarkedAsRead) {
        
        if ( !variableExists(name) ) {
            submitError( new Error(
                "The variable " + name + " is not defined in this context.", 
                referenceToken,
                "issue"
            ) );
            return 0;
        }
        
        int localIndex = getLocalIndex(name, referenceToken, doNotMarkedAsRead);
        
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
