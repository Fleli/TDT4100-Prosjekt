package Project;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import Project.Compiler.Compiler.Compiler;
import Project.Compiler.InstructionGeneration.InstructionList;
import Project.VirtualMachine.Runtime;
import Project.VirtualMachine.VMException;

public class HeapTest {
    
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
    public void test_writeToHeap() throws VMException {
        
        assertDoesNotThrow( () -> runWithInput(
            "int* a = alloc(5); heap (a) = 7; heap (a + 1) = 9; int* b = alloc(7); heap(b+2) = 13;"
        ) );
        
        assertEquals( 7 , runtime.getHeapElement(0) );      // a[0] == 7
        assertEquals( 9 , runtime.getHeapElement(1) );      // a[1] == 9
        assertEquals( 13 , runtime.getHeapElement(7) );      // b[2] == 13
        
        runtime.printHeap();
        
    }
    
    @Test
    public void test_fetchFromHeap() throws VMException {
        
        assertDoesNotThrow( () -> runWithInput(
            "int* a = alloc(5); heap(a + 1) = 15; heap(a + 3) = 20; int* b = alloc(20); heap(b + 2) = heap(a + 1) * heap(a + 3);"
        ) );
        
        assertEquals( 15 , runtime.getHeapElement(1) );      // a[1] == 15
        assertEquals( 20 , runtime.getHeapElement(3) );      // a[3] == 20
        assertEquals( 300 , runtime.getHeapElement(7) );      // b[2] == a[1] * a[3] == 300
        
        runtime.printStack();
        runtime.printHeap();
        
    }
    
}
