package Project.Compiler.Compiler;

import java.util.ArrayList;
import java.util.List;

import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.Lexer.Lexer;
import Project.Compiler.Lexer.Token;
import Project.Compiler.NameBinding.Environment;
import Project.Compiler.Parser.Parser;
import Project.Compiler.Parser.Statement;
import Project.VirtualMachine.Runtime;

public class Compiler {
    
    private Lexer lexer = new Lexer();
    private Parser parser = new Parser();
    
    private Environment environment = new Environment();
    
    private InstructionList executable;
    
    private List<Error> errors = new ArrayList<Error>();
    
    public void compile(String sourceCode, boolean generateExecutable) {
        
        if ( sourceCode == null ) throw new IllegalArgumentException("Source code cannot be null");
        
        // Setup token and statement lists
        List<Token> tokens;
        List<Statement> program;
        
        // Lex the input
        lexer.setInput(sourceCode);
        lexer.lex();
        tokens = lexer.getTokens();
        
        // Parse the input
        parser.setTokens(tokens);
        parser.parse();
        program = parser.getStatements();
        
        // Bind names
        bindNames(program);
        
        // Finner errors i source-programmet
        errors.addAll(parser.getErrors());
        errors.addAll(environment.getErrors());
        
        // Ferdig dersom executable ikke skal genereres
        if ( !generateExecutable ) {
            return;
        }
        
        // Sett opp liste for executable
        InstructionList executable = new InstructionList();
        generateExecutable(program, executable);
        
        // Assign to this object's executable property
        this.executable = executable;
        
    }
    
    public InstructionList getExecutable() {
        return executable;
    }
    
    public void printExecutable() {
        
        System.out.println("Executable {");
        
        int index = 0;
        
        while ( index < executable.size() ) {
            
            int instruction = executable.getExeData(index);
            
            StringBuilder s = new StringBuilder("  " + index);
            while ( s.length() < 8 ) s.append(" ");
            s.append("" + instruction);
            while ( s.length() < 14 ) s.append(" ");
            s.append ( Runtime.instructionWithOpCode(instruction) );
            while ( s.length() < 30 ) s.append(" ");
            
            String associatedStatement = executable.getAssociatedStatement(index);
            
            if ( associatedStatement != null ) {
                s.append(associatedStatement);
            }
            
            System.out.println(s.toString());
            
            index++;
            
            if (   instruction == 28
                || instruction == 29
                || instruction == 30
                || instruction == 32
                || instruction == 33
                || instruction == 34
                || instruction == 39 )
            {
                instruction = executable.getExeData(index);
                System.out.println("   ->operand: " + instruction );
                index++;
            }
            
        }
        
        System.out.println("}");
        
    }
    
    public List<Token> getSyntaxHighlightableTokens(String source) {
        
        List<Token> tokens;
        
        lexer = new Lexer();
        lexer.setInput(source);
        lexer.lex();
        tokens = lexer.getTokens();
        
        return tokens;
        
    }
    
    public List<Error> getErrors() {
        return new ArrayList<Error>(errors);
    }
    
    private void bindNames(List<Statement> program) {
        for ( Statement statement : program ) {
            statement.bind_names(environment);
        }
        environment.verifyUsedDeclarationsInGlobal();
    }
    
    private void generateExecutable(List<Statement> program, InstructionList executable) {
        
        for ( Statement statement : program ) {
            InstructionList newInstructions = statement.generateInstructions(environment);
            executable.add(newInstructions);
        }
        
        executable.add(0, null); // End program;
        
    }
    
    public static boolean isDataTypes(String keyword) {
        return 
            keyword.equals("int")
            ||  keyword.equals("string");
    }
    
}
