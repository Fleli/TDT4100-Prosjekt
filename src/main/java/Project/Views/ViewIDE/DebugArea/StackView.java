package Project.Views.ViewIDE.DebugArea;

import java.util.List;

import Project.Views.ViewIDE.LanguageDelegates.Delegate_f.VMDB.VMDBSymbol;
import Project.Views.ViewIDE.LanguageDelegates.Delegate_f.VMDB.VMDebugger;

/**
 * Area for displaying active variables stored on the stack.
 */
public class StackView extends DebugAreaView {
    
    private VMDebugger debugger;
    
    public StackView(double width, double height, double fontSize, double spacing) {
        
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
        
        List<VMDBSymbol> symbols = debugger.getSymbolList();
        
        for (VMDBSymbol symbol : symbols) {
            String line = symbol.getName() + " = " + symbol.getValue() + "\n";
            print(line);
        }
        
    }
    
}
