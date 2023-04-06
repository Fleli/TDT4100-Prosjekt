package Project.Compiler.Parser.StatementTypes;

import java.util.List;

import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.NameBinding.Environment;
import Project.Compiler.Parser.Statement;
import Project.Compiler.Parser.Expressions.Expression;

public class While implements Statement {
    
    
    private Expression condition;
    private List<Statement> body;
    
    
    public While ( Expression condition , List<Statement> body ) {
        
        if ( condition == null  ||  body == null ) {
            throw new IllegalArgumentException("Arguments of While constructor must be non-null");
        }
        
        this.condition = condition;
        this.body = body;
        
    }
    
    
    
    @Override
    public void bind_names(Environment environment) {
        
        // First, bind names for the condition to be evaluated
        condition.bind_names(environment);
        
        // Then, since the While statement's body is enclosed in { }, we
        // push a new scope to the current environment
        environment.pushScope();
        
        // Now, we bind names for each statement in the While's body
        for ( Statement statement : body ) {
            statement.bind_names(environment);
        }
        
        // Since we're now done with the statements enclosed in { },
        // we pop the most local scope off of our Environment's Scope stack.
        environment.popScope();
        
    }

    @Override
    public InstructionList generateInstructions(Environment environment) {
        
        InstructionList instructions = new InstructionList();
        
        // First of all, the condition is evaluated
        InstructionList conditionEvaluationInstructions = condition.generateInstructions(environment);
        
        // Now, the top of the stack is the evaluation of the condition.
        // But before we add ADJUSTPC to the instructions, we need to find
        // the number of addresses to adjust if the condition fails
        // Since we are then supposed to skip past the body of this loop,
        // that number corresponds to the number of statements that the
        // body contains
        InstructionList bodyInstructions = new InstructionList();
        
        // We need to count the number of declarations inside the while
        // statement's body so that we can adjust the stack pointer when
        // exiting it (declarations increase SP, but it is not automatically
        // reduced later on)
        int numberOfDeclarations = 0;
        
        // Generate instructions for the while statement's body
        for ( Statement statement : body ) {
            bodyInstructions.add(statement.generateInstructions(environment));
            if ( statement instanceof Declaration ) numberOfDeclarations++;
        }
        
        // Adjust stack pointer back when exiting the while statement's body
        bodyInstructions.add(34, this);                         // ADJUSTSP instruction
        bodyInstructions.add(-numberOfDeclarations, this);      // For each declaration, adjust by -1
        
        // Now we know how big the body of the while statement is. However, in 
        // order to exit the body and reevaluate the while statement's condition,
        // we need to adjust the program counter back to *before* condition evaluation
        int bodySize = bodyInstructions.size();
        int evaluationSize = conditionEvaluationInstructions.size();
        
        // Adjustment to exit the body is the sum of the bodySize and
        // evaluation size, plus 4 memory addresses – 2 of which will come
        // from the ADJUSTATZERO instruction (which will be inserted) and
        // 2 of which are necessary due to the adjustment statement itself
        int exit_body_adjustment = -(bodySize + evaluationSize + 4);
        
        // The adjustment to exit the body should actually be a part of the body itself.
        bodyInstructions.add(32, this);                         // Adjust program counter
        bodyInstructions.add(exit_body_adjustment, this);       // Operand: the program counter adjustment
        
        // Now the while statement's body is complete. If the condition
        // evaluation fails, we should skip past the while statement's
        // body. Thus, we must adjust PC the number of memory addresses
        // that the body's instructions take up. We add this adjustment
        // to the evaluation instruction list
        int condition_is_zero_adjustment = bodyInstructions.size();
        conditionEvaluationInstructions.add(33, this);                              // Adjust PC if pop() (condition evaluation) yields 0
        conditionEvaluationInstructions.add(condition_is_zero_adjustment, this);    // If zero, adjust by the body's size
        
        // The first part of the while loop is condition evaluation and
        // conditional program counter adjustment.
        instructions.add(conditionEvaluationInstructions);
        
        // Then, we add the body's instructions.
        instructions.add(bodyInstructions);
        
        // Condition evaluation and body makes up the whole while statement
        // so we are now ready to return the generated instructions
        return instructions;
        
    }
    
    @Override
    public String description() {
        return "while " + condition.description() + " { ... }";
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder("while " + condition.description() + "{");
        
        for ( Statement s : body ) {
            sb.append("\n" + s);
        }
        
        return sb + "\n}";
        
    }
    
}
