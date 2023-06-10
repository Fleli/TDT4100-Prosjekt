package Project;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import Project.Compiler.Compiler.Compiler;
import Project.Compiler.InstructionGeneration.InstructionList;
import Project.VirtualMachine.Runtime;
import Project.VirtualMachine.VMException;

public class WhileTest {
    
    Compiler compiler;
    Runtime runtime;
    
    public void runWithInput ( String sourceCode ) throws VMException {
        
        compiler = new Compiler();
        
        compiler.compile(sourceCode, true);
        
        InstructionList executable = compiler.getExecutable();
        
        assertNotNull ( executable );
        
        runtime = new Runtime ( executable , 1024 , 1024 , null );
        
        compiler.printExecutable();
        
        runtime.run();
        
    }
    
    @Test
    public void test_simpleLoop() throws VMException {
        
        assertDoesNotThrow( () -> runWithInput(
            "int a = 4; while ( a == 4 ) { a = a + 1; }"
        ) );
        
        assertEquals( 5 , runtime.getStackElement(1) );     // a == 5
        assertEquals( 2 , runtime.getStackPointer());       // SP == 2
        
        runtime.printStack();
        
    }
    
    @Test
    public void test_loopAndNextStatement() throws VMException {
        
        assertDoesNotThrow( () -> runWithInput(
            "int a = 0; while ( a != 4 ) { a = a + 1; } int b = a - 1;"
        ) );
        
        assertEquals( 4 , runtime.getStackElement(1) );     // a == 4
        assertEquals( 3 , runtime.getStackElement(2) );     // b == 3
        assertEquals( 3 , runtime.getStackPointer());       // SP == 2
        
        runtime.printStack();
        
    }
    
}
