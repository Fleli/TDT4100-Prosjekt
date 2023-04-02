package Project.Compiler.Compiler;

import java.util.List;

import Project.Compiler.InstructionGeneration.InstructionList;
import Project.Compiler.Lexer.Lexer;
import Project.Compiler.Lexer.Token;
import Project.Compiler.NameBinding.Environment;
import Project.Compiler.Parser.Parser;
import Project.Compiler.Parser.Statement;
import Project.VirtualMachine.Runtime;
import Project.VirtualMachine.VMException;

public class Compiler {
    
    private Lexer lexer = new Lexer();
    private Parser parser = new Parser();
    
    private Environment environment = new Environment();
    
    private InstructionList executable;
    
    public void compile ( String sourceCode ) {
        
        if ( sourceCode == null ) throw new IllegalArgumentException("Source code cannot be null");
        
        lexer.setInput(sourceCode);
        
        lexer.lex();
        
        List<Token> tokens = lexer.getTokens();
        
        parser.setTokens(tokens);
        
        try {
            
            parser.parse();
        
        } catch (Exception e) {
            
            System.out.println("Parser threw exception. Errors:");
            
            for ( Error error : parser.getErrors() ) {
                System.out.println("    " + error);
            }
            
            System.out.println("Will now throw from compiler as well.");
            
            throw e;
            
        }
        
        if ( parser.wasErroneous() ) {
            
            for ( Error error : parser.getErrors() ) {
                System.out.println(error);
            }
            
            return;
            
        } else {
            
            for ( Statement statement : parser.getStatements() ) {
                System.out.println(statement);
            }
            
        }
        
        List<Statement> program = parser.getStatements();
        
        for ( Statement statement : program ) {
            statement.bind_names(environment);
        }
        
        InstructionList executable = new InstructionList();
        
        for ( Statement statement : program ) {
            
            InstructionList newInstructions = statement.generateInstructions(environment);
            executable.add(newInstructions);
            
        }
        
        executable.add(0, null); // End program;
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
            
            if (   instruction == 1
                || instruction == 28
                || instruction == 29
                || instruction == 30
                || instruction == 32
                || instruction == 33
                || instruction == 34 )
            {
                instruction = executable.getExeData(index);
                System.out.println("   ->operand: " + instruction );
                index++;
            }
            
        }
        
        System.out.println("}");
        
    }
    
    public static void main(String[] args) {
        
        Compiler compiler = new Compiler();
        
        compiler.compile(
            
            "  int limit = 500;"
            +   "\nint number = 3;"
            +   "\nint* primes = alloc(100);"
            +   "\nheap(primes + 0) = 2;"
            +   "\nint count = 1;"
            +   "\nwhile ( number < limit ) {"
            +   "\n    int isPrime = 1;"
            +   "\n    int fIndex = 0;"
            +   "\n    while ( fIndex < count ) {"
            +   "\n        int remainder = number % heap ( primes + fIndex );"
            +   "\n        fIndex = fIndex + 1;"
            +   "\n        if ( remainder == 0 ) {"
            +   "\n            isPrime = 0;"
            +   "\n        }"
            +   "\n     }"
            +   "\n     if ( isPrime ) {"
            +   "\n         heap(primes + count) = number;"
            +   "\n         count = count + 1;"
            +   "\n     }"
            +   "\n     number = number + 2;"
            +   "\n}"
        );
        
        InstructionList executable = compiler.getExecutable();
        
        if ( executable != null ) {
            
            compiler.printExecutable();
            Runtime runtime = new Runtime(executable, 512, 512);
            
            try {
                Runtime.printDebugInfo = false;
                runtime.run();
            } catch (VMException e) {
                System.out.println("VM Runtime exception (" + e.getLocalizedMessage() + "). Stack trace:");
                e.printStackTrace();
                return;
            } catch (Exception e) {
                System.out.println("Other exception. Stack trace:");
                e.printStackTrace();
                return;
            }
            
            System.out.println(runtime);
            
        }
        
        System.out.println("Ended main()\n");
        
    }
    
}
