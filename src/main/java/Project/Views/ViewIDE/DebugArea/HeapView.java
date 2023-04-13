package Project.Views.ViewIDE.DebugArea;

import java.util.List;

import Project.Views.ViewIDE.LanguageDelegates.Delegate_f.VMDebugger;
import Project.VirtualMachine.Heap.VMHeapArea;
import javafx.scene.paint.Color;

public class HeapView extends DebugAreaView {
    
    

    public HeapView(double width, double height) {
        
        super(width, height);
        
    }
    
    @Override
    public void refresh() {
        
        super.refresh();
        
        VMDebugger debugger = getDebugger();
        
        if (debugger == null) {
            return;
        }
        
        List<VMHeapArea> allocations = debugger.getAllocations();
        
        if (allocations.size() == 0) {
            
            print("No allocations yet", Color.WHITE, "-fx-font-weight: bold;");
            
        } else for (VMHeapArea allocation : allocations) {
            
            print("\nAllocation at line " + allocation.getAllocLine() + ":\n", Color.WHITE, "-fx-font-weight: bold;");
            
            String sizeDescription = "Size: " + allocation.getSize() + "\n";
            String pointerDescription = "Pointer: " + allocation.getAddress() + "\n";
            
            StringBuilder dataString = new StringBuilder("[");
            
            int[] heapData = debugger.getDataAtAllocation(allocation);
            
            if (heapData.length == 0) {
                
                dataString.append("]");
                
            } else {
                
                for (int i = 0 ; i < heapData.length ; i++) {
                    int data = heapData[i];
                    dataString.append(data + ", ");
                }
                
                dataString.delete(dataString.length() - 2, dataString.length());
                
            }
            
            String text_to_print = sizeDescription + pointerDescription + dataString + "]\n";
            print(text_to_print);
            
        }
        
        finishedViewUpdate_moveToScroll();
        
    }
    
}
