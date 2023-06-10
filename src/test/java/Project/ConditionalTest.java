package Project;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Project.Compiler.Compiler.Compiler;
import Project.Compiler.InstructionGeneration.InstructionList;
import Project.VirtualMachine.Runtime;
import Project.VirtualMachine.VMException;

public class ConditionalTest {
    
    Compiler compiler;
    Runtime runtime;
    
    @BeforeEach
    public void setup() {
        compiler = new Compiler();
    }
    
    public void runWithInput ( String sourceCode ) throws VMException {
        
        compiler.compile(sourceCode, true);
        
        InstructionList executable = compiler.getExecutable();
        
        runtime = new Runtime( executable , 1024 , 1024 , null );
        
        compiler.printExecutable();
        
        runtime.run();
        
    }
    
    @Test
    public void testVariableShadowing() throws VMException {
        
        assertDoesNotThrow( () -> runWithInput("int a = 1; int b = 15; int c = 6; if a { int b = c + 10; a = b + 1; }") );
        
        assertEquals(17, runtime.getStackElement(1));        // Sjekk a = 17
        assertEquals(4, runtime.getStackPointer());
        
        runtime.printStack();
        
    }
    
    @Test
    public void testConditionalNesting() throws VMException {
        
        assertDoesNotThrow( () -> runWithInput("int a = 1; if (a) { int b = 16; if (b) { int c = 25; } int d = 40; }") );
        
        assertEquals(1, runtime.getStackElement(1));        // Sjekk a = 1
        assertEquals(16, runtime.getStackElement(2));       // Sjekk b = 16 (men b egentlig utenfor scope nå)
        assertEquals(40, runtime.getStackElement(3));       // Sjekk d = 40 (d overskriver c, men d egentlig utenfor scope)
        assertEquals(2, runtime.getStackPointer());
        
        runtime.printStack();
        
    }
    
    @Test
    public void test_elseIf_isNotExecuted() throws VMException {
        
        assertDoesNotThrow( () -> runWithInput( "int a = 1; if (a) { a = 5; } else if 1 { a = 8; } int b = 7;" ) );
        
        assertEquals(5, runtime.getStackElement(1));        // Sjekk a = 1
        assertEquals(7, runtime.getStackElement(2));        // Sjekk b = 7
        
        runtime.printStack();
        
    }
    
    @Test
    public void test_elseIf_isExecuted() throws VMException {
        
        assertDoesNotThrow( () -> runWithInput( "int a = 0; if (a) { a = 5; } else if 1 { a = 8; } int b = 7;" ) );
        
        assertEquals(8, runtime.getStackElement(1));        // Sjekk a = 1
        assertEquals(7, runtime.getStackElement(2));        // Sjekk b = 7
        
        runtime.printStack();
        
    }
    
    @Test
    public void test_elseIfChain_isNotExecuted() throws VMException {
        
        assertDoesNotThrow( () -> runWithInput( "int a = 1; if (a) { a = 5; } else if a + 1 { a = 8; } else if a + 2 { a = 10; } int b = 7;" ) );
        
        assertEquals(5, runtime.getStackElement(1));        // Sjekk a = 5
        assertEquals(7, runtime.getStackElement(2));        // Sjekk b = 7
        
        runtime.printStack();
        
    }
    
    @Test
    public void test_elseIfChain_executed_elseIf_1() throws VMException {
        
        assertDoesNotThrow( () -> runWithInput( "int a = 0; if (a) { a = 5; } else if a + 1 { a = 8; } else if a + 2 { a = 10; } int b = 7;" ) );
        
        assertEquals(8, runtime.getStackElement(1));        // Sjekk a = 8
        assertEquals(7, runtime.getStackElement(2));        // Sjekk b = 7
        
        runtime.printStack();
        
    }
    
    @Test
    public void test_elseIfChain_executed_elseIf_2() throws VMException {
        
        assertDoesNotThrow( () -> runWithInput( "int a = 5; if (a - 5) { a = 5; } else if ( a * 0 ) { a = 8; } else if a + 2 { a = 10; int p = 90; int q = 80; int r = 70; } int b = 7;" ) );
        
        assertEquals(10, runtime.getStackElement(1));       // Sjekk a = 10
        assertEquals(7, runtime.getStackElement(2));        // Sjekk b = 7
        
        assertEquals(3, runtime.getStackPointer());         // Sjekk at SP = 3, dvs. kun a og b definert i globalt scope.
        
        runtime.printStack();
        
    }
    
    @Test
    public void test_elseIfChain_onlyFirstMatchIsEvaluated() throws VMException {
        
        assertDoesNotThrow( () -> runWithInput( 
            "int a = 10; int b = 2; int c = 3; if ( a - 10 ) { a = 5000; } else if ( a + 1 ) { b = 400; } else if ( a + 2 ) { c = 600; } int d = 20;"
        ) );
        
        assertEquals(10, runtime.getStackElement(1));       // Sjekk a = 10
        assertEquals(400, runtime.getStackElement(2));      // Sjekk b = 400
        assertEquals(3, runtime.getStackElement(3));        // Sjekk c = 3
        assertEquals(20, runtime.getStackElement(4));       // Sjekk d = 20
        
        assertEquals(5, runtime.getStackPointer());         // Sjekk at SP = 3, dvs. kun a og b definert i globalt scope.
        
        runtime.printStack();
        
    }
    
}
