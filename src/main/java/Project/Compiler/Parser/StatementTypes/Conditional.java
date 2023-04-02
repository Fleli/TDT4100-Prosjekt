package Project.Compiler.Parser.StatementTypes;

import java.util.List;

import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.NameBinding.Environment;
import Project.Compiler.Parser.Statement;
import Project.Compiler.Parser.Expressions.Expression;

public class Conditional implements Statement {
    
    /**
     * The condition, which when evaluated decides
     * whether this {@code Conditional}'s {@code body}
     * or, if non-{@code null}, the {@code otherwise}
     * condition should be executed.
     */
    private Expression condition;
    
    /**
     * The {@code Conditional} object's {@code body} is
     * a {@code List} of {@code Statement} objects to be
     * executed if {@code condition} evalues to anything
     * else than {@code 0}.
     */
    private List<Statement> body;
    
    /**
     * If this object's {@code condition} fails and 
     * {@code otherwise} is non-{@code null}, it will
     * be executed.
     */
    private Conditional otherwise;
    
    
    public Conditional ( Expression condition , List<Statement> body , Conditional otherwise ) {
        
        if ( condition == null  ||  body == null  ||  otherwise == null ) {
            throw new IllegalArgumentException("All arguments must be non-null.");
        }
        
        this.condition = condition;
        this.body = body;
        this.otherwise = otherwise;
        
    }
    
    /**
     * Create new {@code Conditional} object without {@code else} statement
     * @param condition The {}
     * @param body
     */
    public Conditional ( Expression condition , List<Statement> body ) {
        
        if ( condition == null  ||  body == null ) {
            throw new IllegalArgumentException("All arguments must be non-null.");
        }
        
        this.condition = condition;
        this.body = body;
        
    }
    
    
    @Override
    public void bind_names(Environment environment) {
        
        condition.bind_names(environment);
        
        // Since the body is enclosed in { }, it requires a scope push
        environment.pushScope();
        
        for ( Statement statement : body ) {
            statement.bind_names(environment);
        }
        
        // The scope pushed for the body is popped once its names are bound
        environment.popScope();
        
        if ( otherwise != null ) {
            otherwise.bind_names(environment);
        }
        
    }

    @Override
    public InstructionList generateInstructions(Environment environment) {
        
        InstructionList instructions = new InstructionList();
        
        // Generate instructions for calculating the expression
        InstructionList conditionCalculationInstructions = condition.generateInstructions(environment);
        instructions.add(conditionCalculationInstructions);
        
        // Must find number of declarations in body to adjust stack pointer later on
        int numberOfDeclarations = 0;
        
        // Generate instructions for the if's body
        InstructionList bodyInstructions = new InstructionList();
        for ( Statement statement : body ) {
            bodyInstructions.add(statement.generateInstructions(environment));
            if ( statement instanceof Declaration ) numberOfDeclarations++;
        }
        
        // Juster stack pointer tilbake basert på antall declarations.
        bodyInstructions.add(34, this);                         // ADJUSTSP instruction
        bodyInstructions.add(-numberOfDeclarations, this);      // For each declaration, adjust by -1
        
        // Two paths, depending on whether an 'otherwise' path is added
        if ( otherwise == null ) {
            
            // Programmet skal hoppe forbi hele body dersom condition evalueres til 0
            int adjustment_at_condition_fail = bodyInstructions.size();
            
            // Legg til ADJUSTATZERO (33) og operand (adjustment) i instruksjonslisten
            instructions.add(33, this);
            instructions.add(adjustment_at_condition_fail, this);
            
            // Legg til statement-bodyen nå, altså etter conditional adjust-kommandoen
            instructions.add(bodyInstructions);
            
        } else {
            
            // Først må vi generere instruksjoner for 'else if'-pathen. Denne vil rekursivt
            // ta seg av nøstede 'else ifs', så her kan vi "late som om" det bare er én
            // 'else if'.
            InstructionList otherwiseInstructions = otherwise.generateInstructions(environment);
            
            // En del av bodyInstructions må være å justere program counter slik at programmet
            // ikke faller gjennom til 'else if' dersom condition != 0. Vi må altså ha en
            // AJDUSTPC-instruksjon. Antall instruksjoner den skal justeres med, er nøyaktig lik
            // antall otherwiseInstructions (fordi det er disse som skal hoppes over).
            int adjustment = otherwiseInstructions.size();
            bodyInstructions.add(32, this);             // ADJUSTPC
            bodyInstructions.add(adjustment, this);     // Operand: adjustment
            
            // Nå er det klart for å evaluere condition. Dersom den evalueres til 0, skal
            // programmet hoppe videre til 'else if'-clausen. Det betyr at PC-adjustment
            // for feilet condition, er lik antall instruksjoner i if-statementen sin body
            int adjustment_at_condition_fail = bodyInstructions.size();
            
            // Dersom condition (som er på toppen av stacken, fordi det forrige som ble
            // lagt til i instructions var evalueringen av condition) evalueres til 0,
            // skal vi justere program counter slik at vi i stedet evaluerer otherwise
            // sin kode. Ellers skal vi fallthrough til body, som selv sørger for å
            // unngå fallthrough til otherwise.
            instructions.add(33, this);                             // ADJUSTATZERO (33) Juster PC dersom pop() == 0
            instructions.add(adjustment_at_condition_fail, this);   // Operand: justering dersom pop() == 0
            
            // Vi har nå utført utregning og sjekk av condition. Dersom den evalueres til
            // et tall ulik 0 (ADJUSTATZERO ovenfor *ikke* justerer) skal vi fallthrough
            // til body, for da er condition evaluert til sann, så body skal utføres.
            instructions.add(bodyInstructions);
            
            // Merk at bodyInstructions selv sørger for å unngå fallthrough til otherwise,
            // så vi kan nå trygt legge inn otherwiseInstructions (som selv sørger for
            // evaluering av condition, riktig fallthrough, osv.) som de neste instruksjonene
            instructions.add(otherwiseInstructions);
            
        }
        
        return instructions;
        
    }
    
    @Override
    public String description() {
        
        return "if" + condition.description() + " { ... }";
        
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder("if " + condition.description() + "{");
        
        for ( Statement s : body ) {
            sb.append("\n" + s);
        }
        
        return sb + "\n}";
        
    }
    
}
