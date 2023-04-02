package Project.VirtualMachine.Heap;

import java.util.Arrays;

import Project.VirtualMachine.VMException;

public class VMHeap {
    
    private int[] data;
    
    private int size;
    
    private VMHeapAllocator allocator;
    
    public VMHeap ( int size ) {
        
        this.size = size;
        
        data = new int[size];
        
        allocator = new VMHeapAllocator(this);
        
    }
    
    public int getData ( int location ) throws VMException {
        
        if ( location < 0  ||  location >= size ) {
            throw new VMException("Segmentation fault on heap. Location " + location + " is outside memory bounds.", "heap");
        }
        
        return data[location];
        
    }
    
    public void setData ( int location , int word ) throws VMException {
        
        if ( location < 0  ||  location >= size ) {
            throw new VMException("Segmentation fault on heap. Location " + location + " is outside memory bounds.", "heap");
        }
        
        data[location] = word;
        
    }
    
    public int alloc ( int size ) throws VMException {
        
        return allocator.allocate ( size );
        
    }
    
    public void dealloc ( int pointer ) throws VMException {
        
        allocator.deallocate ( pointer );
        
    }
    
    public int getSize() {
        return size;
    }
    
    public void printUsed() {
        allocator.printUsed();
    }
    
    @Override
    public String toString() {
        return "Heap sized " + size + " with data: \n" + Arrays.toString(data) + " and allocator status:\n" + allocator.toString();
    }
    
}
