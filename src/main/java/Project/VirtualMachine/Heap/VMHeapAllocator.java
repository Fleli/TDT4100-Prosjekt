package Project.VirtualMachine.Heap;

import java.util.ArrayList;
import java.util.List;

import Project.VirtualMachine.VMException;

public class VMHeapAllocator {
    
    private VMHeap heap;
    private List<VMHeapArea> used = new ArrayList<VMHeapArea>();
    
    private int heapPointer = 0;
    
    public VMHeapAllocator ( VMHeap heap ) {
        this.heap = heap;
    }
    
    private int necessaryIncrement ( int allocMin , int allocMax ) {
        
        if ( allocMax >= heap.getSize() ) {
            return ( heap.getSize() - allocMin );
        }
        
        for ( VMHeapArea area : used ) {
            
            int usedMin = area.getAddress();
            int usedMax = usedMin + area.getSize() - 1;
            
            if ( usedMax < allocMin  ||  allocMax < usedMin ) {
                continue;
            } else {
                return usedMax - allocMin + 1;
            }
            
        }
        
        return 0;
        
    }
    
    public int allocate(int size, int allocLine) throws VMException {
        
        int passes = 0;
        
        int increment = necessaryIncrement(heapPointer, heapPointer + size - 1);
        
        while ( passes < 2  &&  increment > 0 ) {
            
            heapPointer += increment;
            
            if ( heapPointer >= heap.getSize() ) {
                heapPointer -= heap.getSize();
                passes++;
            }
            
            increment = necessaryIncrement(heapPointer, heapPointer + size - 1);
            
        }
        
        if ( passes < 2 ) {     // Success
            
            VMHeapArea newlyAllocated = new VMHeapArea(heapPointer, size, allocLine);
            used.add(newlyAllocated);
            
            int pointer = heapPointer;
            
            heapPointer = ( heapPointer + size ) % ( heap.getSize() );
            
            return pointer;
            
        } else {                // No success, not enough space
            
            throw new VMException("Not enough heap space for allocation of size " + size + " words.", "heap allocator");
            
        }
        
    }
    
    public void deallocate(int pointer) throws VMException {
        
        for ( VMHeapArea area : used ) if ( area.getAddress() == pointer ) {
            used.remove(area);
            return;
        }
        
        throw new VMException("Cannot deallocate unused memory at location " + pointer + ".", "heap allocator");
        
    }
    
    public List<VMHeapArea> getUsed() {
        return used;
    }
    
    public void printUsed() {
        
        System.out.println("Used area in heap:");
        
        for ( VMHeapArea area : used ) {
            System.out.println(area.getAddress() + " - " + (area.getAddress() + area.getSize() - 1));
        }
        
    }
    
    @Override
    public String toString() {
        
        StringBuilder s = new StringBuilder();
        
        for ( VMHeapArea area : used ) {
            s.append(area.getAddress() + " - " + (area.getAddress() + area.getSize() - 1) + "\n");
        }
        
        return s.toString();
        
    }
    
}
