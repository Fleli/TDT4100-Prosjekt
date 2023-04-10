package Project.VirtualMachine.Heap;

public class VMHeapArea {
    
    private int address;
    private int size;
    
    private int allocLine;
    
    public VMHeapArea(int address, int size, int allocLine) {
        this.address = address;
        this.size = size;
        this.allocLine = allocLine;
    }
    
    public int getAddress() {
        return address;
    }
    
    public int getSize() {
        return size;
    }
    
    public int getAllocLine() {
        return allocLine;
    }
    
}
