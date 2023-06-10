package Project;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;


import Project.Compiler.Compiler.Compiler;
import Project.Compiler.InstructionGeneration.InstructionList;
import Project.VirtualMachine.Runtime;
import Project.VirtualMachine.VMException;

public class FullProgramTests {
    
    Compiler compiler;
    Runtime runtime;
    
    public void runWithInput ( String sourceCode ) throws VMException {
        
        compiler = new Compiler();
        
        compiler.compile(sourceCode, true);
        
        InstructionList executable = compiler.getExecutable();
        
        assertNotNull(executable);
        
        runtime = new Runtime ( executable , 1024 , 1024 , null );
        
        compiler.printExecutable();
        
        runtime.run();
        
    }
    
    @Test
    public void test_primesProgram() throws VMException {
        
        assertDoesNotThrow( () -> runWithInput(
                "  int limit = 100;"
            +   "\nint number = 2;"
            +   "\nint* primes = alloc(50);"
            +   "\nint count = 0;"
            +   "\nwhile ( number < limit ) {"
            +   "\n    int isPrime = 1;"
            +   "\n    int factor = 2;"
            +   "\n    while ( factor < number ) {"
            +   "\n        int remainder = number % factor;"
            +   "\n        factor = factor + 1;"
            +   "\n        if ( remainder == 0 ) {"
            +   "\n            isPrime = 0;"
            +   "\n        }"
            +   "\n     }"
            +   "\n     if ( isPrime ) {"
            +   "\n         heap(primes + count) = number;"
            +   "\n         count = count + 1;"
            +   "\n     }"
            +   "\n     number = number + 1;"
            +   "\n}"
        ) );
        
        runtime.printHeap();
        runtime.printStack();
        
    }
    
    
}
