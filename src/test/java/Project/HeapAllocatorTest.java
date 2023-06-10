package Project;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Project.VirtualMachine.VMException;
import Project.VirtualMachine.Heap.VMHeap;

public class HeapAllocatorTest {
    
    VMHeap heap;
    
    @BeforeEach
    public void setup() {
        heap = new VMHeap(12);
    }
    
    @Test
    public void testAlloc() throws VMException {
        assertEquals(0, heap.alloc(4, 0));
        assertEquals(4, heap.alloc(5, 0));
        assertEquals(9, heap.alloc(3, 0));
        assertThrows(VMException.class, () -> heap.alloc(1, 0));
    }
    
    @Test
    public void testDealloc() throws VMException {
        int a = heap.alloc(7, 0);
        assertDoesNotThrow( () -> heap.dealloc(a) );
        assertDoesNotThrow( () -> heap.alloc(1, 0) );
        assertEquals(8, heap.alloc(2, 0));
        assertEquals(0, heap.alloc(5, 0));
    }
    
    @Test
    public void testNotEnoughSpace() throws VMException {
        assertDoesNotThrow( () -> { 
            for ( int i = 0 ; i < 6 ; i++ ) {
                heap.alloc(2, 0);
            }
        } );
        assertDoesNotThrow( () -> { heap.dealloc(4); } );
        assertThrows( VMException.class , () -> { heap.dealloc(9); } );
        assertThrows( VMException.class , () -> { heap.alloc(3, 0); } );
    }
    
}
