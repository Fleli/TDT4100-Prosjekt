package Project;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Project.Compiler.Compiler.Compiler;
import Project.VirtualMachine.Runtime;
import Project.VirtualMachine.VMException;

public class AllocationTest {
    
    Compiler compiler;
    Runtime runtime;
    
    @BeforeEach
    public void setup() {
        compiler = new Compiler();
    }
    
    @Test
    public void checkBasicFunctionality() throws VMException {
        
        assertDoesNotThrow( () -> compiler.compile( "int a = alloc(5); int b = alloc(6); int c = alloc(7);", true ) );
        assertNotNull( compiler.getExecutable() );
        
        runtime = new Runtime( compiler.getExecutable() , 1024 , 1024 , null );
        runtime.run();
        
        assertEquals(0, runtime.getStackElement(1));
        assertEquals(5, runtime.getStackElement(2));
        assertEquals(11, runtime.getStackElement(3));
        
        runtime.printStack();
        
    }
    
    @Test
    public void testOutOfMemory() throws VMException {
        
        
        assertDoesNotThrow( () -> compiler.compile( "int a = alloc(5); int b = alloc(8); int c = alloc(1000000);", true ) );
        assertNotNull( compiler.getExecutable() );
        
        runtime = new Runtime( compiler.getExecutable() , 1024 , 1024 , null );
        
        assertThrows( VMException.class , () -> runtime.run() );
        assertEquals( 5, runtime.getStackElement(2) );
        
        runtime.printStack();
        
    }
    
}
