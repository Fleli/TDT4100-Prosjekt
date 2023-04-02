package Project.Compiler.Parser.StatementTypes;

import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.NameBinding.Environment;
import Project.Compiler.Parser.Statement;
import Project.Compiler.Parser.Expressions.Expression;

/**
 * Deklarerer en ny variabel innenfor gitt scope.
 * Merk at programmereren kan gi initial value. Dette
 * spaltes til en hoveddel ({@code Declaration}) og
 * en {@code Assignment}-del som eies av {@code Declaration}-
 * objektet, og ikke er synlig for omverdenen.
 */
public class Declaration implements Statement {
    
    /**
     * Alle variabler oppfører seg som {@code int}s ifbm. TAC, men de kan også være pointers. Variabelen {@code pointerDepth}
     * svarer til antall nivå med indirection, slik at {@code int} har {@code pointerDepth = 0}, {@code int*} har
     * {@code pointerDepth = 1} osv.
     * Derfor vil {@code pointerDepth} alene avgjøre typen til variabelen som deklareres.
    */
    private int pointerDepth;
    
    /**
     * Variabelens navn, som den kan refereres til både i andre scopes (dersom navnet opptrer som mest lokalt)
     * og i samme scope. Merk at navnekollisjoner ikke er lovlig innenfor samme scope, selv om variablene har
     * forskjellige typer.
    */
    private String name;
    
    private Expression initialRhs;
    private Assignment assignment;
    
    /**
     * Create a new {@code Declaration} object without an initial value (that is, a specified
     * value other than the default 0 value).
     * @param pointerDepth The level of indirection to an actual {@code int} this variable has
     * @param name The name of this variable in the source code
     */
    public Declaration ( int pointerDepth , String name ) {
        
        if ( pointerDepth < 0 ) throw new IllegalArgumentException("pointerDepth < 0 er ugyldig");
        
        this.pointerDepth = pointerDepth;
        this.name = name;
        
    }
    
    /**
     * Create a new {@code Declaration} object with initial value. When binding names and
     * generating instructions, this initial value (and the variable's name) will be treated
     * as if they were an {@code Assignment}, but this {@code Assignment} object is not visible
     * to the outside.
     * @param pointerDepth The level of indirection to an actual {@code int} this variable has
     * @param name The name of this variable in the source code
     * @param initialRhs The initial {@code Expression} of this variable. Will be treated as an
     * {@code Assignment} object upon runtime, but not during compilation.
     */
    public Declaration ( int pointerDepth , String name , Expression initialRhs ) {
        
        if ( pointerDepth < 0 ) throw new IllegalArgumentException("pointerDepth < 0 er ugyldig");
        
        if ( initialRhs == null ) throw new IllegalArgumentException("Initial expression cannot be null.");
        
        this.pointerDepth = pointerDepth;
        this.name = name;
        
        this.initialRhs = initialRhs;
        this.assignment = new Assignment(name, initialRhs);
        
        if ( name == "remainder" ) {
            System.out.println ( "\n\n\n" + assignment.description() + "\n\n\n" );
        }
        
    }
    
    public String getName() {
        return name;
    }
    
    public int getPointerDepth() {
        return pointerDepth;
    }
    
    @Override
    public InstructionList generateInstructions ( Environment environment ) {
        
        InstructionList list = new InstructionList();
        list.add(2, this);                                        // Declaration betyr ny variabel (NEWVAR)
        
        if ( initialRhs != null ) {
            list.add(assignment.generateInstructions(environment)); 
        }
        
        return list;
        
    }
    
    @Override
    public void bind_names(Environment environment) {
        
        environment.addVariable(this);
        
        if ( initialRhs != null ) {
            assignment.bind_names(environment);
        }
        
    }
    
    @Override
    public String toString() {
        StringBuilder stars = new StringBuilder();
        for ( int i = 0 ; i < pointerDepth ; i++ ) stars.append("*");
        return "Declaration " + name + ": int" + stars.toString();
    }
    
    @Override
    public String description() {
        StringBuilder stars = new StringBuilder();
        for ( int i = 0 ; i < pointerDepth ; i++ ) stars.append("*");
        stars = new StringBuilder("int" + stars.toString() + " " + name);
        return stars.toString();
    }

}
