package Project;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Project.Compiler.Compiler.Compiler;
import Project.VirtualMachine.Runtime;
import Project.VirtualMachine.VMException;

public class ExpressionTest {
    
    Compiler compiler;
    Runtime runtime;
    
    @BeforeEach
    public void setup() {
        compiler = new Compiler();
    }
    
    @Test
    public void testLiterals() throws VMException {
        
        assertDoesNotThrow( () -> compiler.compile( "int a; int b; a = 1; b = 2; int c; int d; c = 5; d = 7; c = 2 + 9; int e;", true ) );
        assertNotNull( compiler.getExecutable() );
        
        runtime = new Runtime( compiler.getExecutable() , 1024 , 1024 , null  );
        runtime.run();
        
        assertEquals(1, runtime.getStackElement(1));        // Sjekk a = 1
        assertEquals(2, runtime.getStackElement(2));        // Sjekk b = 2
        assertEquals(11, runtime.getStackElement(3));       // Sjekk c = 11
        assertEquals(7, runtime.getStackElement(4));        // Sjekk d = 7
        assertEquals(0, runtime.getStackElement(5));        // Sjekk e = 0
        
        runtime.printStack();
        
    }
    
    @Test
    public void testReferences() throws VMException {
        
        assertDoesNotThrow( () -> compiler.compile( "int a; a = 6; int b; b = a * a; int c; c = a | b; int d; d = a + b + c;", true ) );
        assertNotNull ( compiler.getExecutable() );
        
        runtime = new Runtime ( compiler.getExecutable() , 1024 , 1024 , null );
        runtime.run();
        
        assertEquals(6, runtime.getStackElement(1));
        assertEquals(36, runtime.getStackElement(2));
        assertEquals(38, runtime.getStackElement(3));
        assertEquals(80, runtime.getStackElement(4));
        
        runtime.printStack();
        
    }
    
    @Test
    public void testPrecedence() throws VMException {
        
        assertDoesNotThrow( () -> compiler.compile( "int a; a = 6 + 4 * 5 | 9; int b; b = 127 & 63 + 4;", true ) );
        assertNotNull ( compiler.getExecutable() );
        
        runtime = new Runtime ( compiler.getExecutable() , 1024 , 1024 , null );
        runtime.run();
        
        assertEquals(27, runtime.getStackElement(1));
        assertEquals(67, runtime.getStackElement(2));
        
        runtime.printStack();
        
    }
    
    @Test
    public void testParentheses() throws VMException {
        
        assertDoesNotThrow( () -> compiler.compile( "int a; a = 4 + 5 * ( 4 + 5 ); int b; b = ( 127 & 63 ) + 4;", true ) );
        assertNotNull ( compiler.getExecutable() );
        
        runtime = new Runtime ( compiler.getExecutable() , 1024 , 1024 , null );
        runtime.run();
        
        assertEquals(49, runtime.getStackElement(1));
        assertEquals(67, runtime.getStackElement(2));
        
        runtime.printStack();
        
    }
    
}
