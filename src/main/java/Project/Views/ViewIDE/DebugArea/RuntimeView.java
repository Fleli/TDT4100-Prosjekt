package Project.Views.ViewIDE.DebugArea;

import Project.Views.ViewIDE.LanguageDelegates.Delegate_f.VMDB.VMDebugger;
import Project.VirtualMachine.VMException;
import javafx.scene.paint.Color;

public class RuntimeView extends DebugAreaView {
    
    private VMDebugger debugger;
    
    public RuntimeView(double width, double height, double fontSize, double spacing) {
        
        super(width, height);
        
    }
    
    public void setDebugger(VMDebugger debugger) {
        this.debugger = debugger;
    }
    
    @Override
    public void refresh() {
        
        super.refresh();
        
        if (debugger == null) {
            return;
        }
        
        String runtimeViewDescription = debugger.getRuntimeViewDescription();
        
        print(runtimeViewDescription);
        
    }
    
    public void notifyException(VMException exception) {
        clear();
        Color red = Color.rgb(240, 110, 110);
        print("RUNTIME EXCEPTION\n", red, "-fx-font-weight: bold;");
        print(exception.getMessage() + "\n", red, "");
    }
    
}
