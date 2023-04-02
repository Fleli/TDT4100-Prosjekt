package Project.VirtualMachine.Heap;

public class VMHeapArea {
    
    private int address;
    private int size;
    
    
    public VMHeapArea ( int address , int size ) {
        this.address = address;
        this.size = size;
    }
    
    
    public int getAddress() {
        return address;
    }
    
    public int getSize() {
        return size;
    }
    
}
