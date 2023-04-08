package Project.Compiler.NameBinding;

import java.util.ArrayList;
import java.util.List;

import Project.Compiler.Compiler.Error;
import Project.Compiler.Lexer.Token;
import Project.Compiler.Parser.StatementTypes.Declaration;

/**
 * Inneholder en liste med {@code entries} (variabler og offsets) 
 * definert innenfor dette scopet.
 * Starter med spesifisert {@code offset}, ikke nødvendigvis lik
 * {@code 1}, fordi mindre lokale scope kan ha definert variabler, 
 * og da skal dette fortsette {@code offset}-rekken fordi scopes i
 * runtime bruker samme {@code framePointer}.
 */
public class Scope {
    
    public static boolean foundBug = false;
    
    private int offset;
    
    private List<ScopeEntry> entries = new ArrayList<ScopeEntry>();
    
    private Environment environment;
    
    public Scope ( int offset , Environment environment ) {
        
        if ( offset < 1 ) {
            throw new IllegalArgumentException("Scope entry offset cannot be less than 1.");
        }
        
        this.offset = offset;
        this.environment = environment;
        
    }
    
    public void verifyUsedDeclarations() {
        for (ScopeEntry entry : entries) {
            if (!entry.wasReadFrom()) {
                environment.submitNeverReadWarning(entry);
            }
        }
    }
    
    public int getOffset() {
        return offset;
    }
    
    public int getLocalIndex(String name, Token referenceToken, boolean doNotMarkAsRead) {
        
        for ( ScopeEntry entry : entries ) if ( entry.getName().equals(name) ) {
            return entry.getLocalIndex(doNotMarkAsRead);
        }
        
        environment.submitError( new Error(
            "No variable " + name + " exists in this scope.", 
            referenceToken,
            "issue"
        ) );
        
        return 0;
        
    }
    
    public int getPointerDepth ( String name ) {
        
        for ( ScopeEntry entry : entries ) if ( entry.getName().equals(name) ) {
            return entry.getPointerDepth();
        }
        
        // TODO: Endre dette til normal error-reporting
        throw new IllegalStateException("No variable " + name + " exists in this scope.");
        
    }
    
    public boolean variableExists ( String name ) {
        
        for ( ScopeEntry entry : entries ) if ( entry.getName().equals(name) ) {
            return true;
        }
        
        return false;
        
    }
    
    public List<ScopeEntry> getEntries() {
        return new ArrayList<ScopeEntry>(entries);
    }
    
    public void addVariable ( Declaration declaration ) {
        
        if ( variableExists(declaration.getName()) ) {
            Error newError = new Error(
                "The variable " + declaration.getName() + " has already been defined in this scope", 
                declaration.getNameToken(),
                "issue"
            );
            environment.submitError(newError);
            return;
        }
        
        ScopeEntry newEntry = new ScopeEntry(declaration, offset);
        
        offset++;
        
        entries.add(newEntry);
        
    }
    
    @Override
    public String toString() {
        
        StringBuilder description = new StringBuilder();
        
        for ( ScopeEntry entry : entries ) {
            description.append("    ->" + entry + "\n");
        }
        
        return description.toString();
        
    }
    
}
