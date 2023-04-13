package Project.Views.ViewIDE.DebugArea;

import java.util.List;

import Project.Views.ViewIDE.LanguageDelegate.VMDBSymbol;
import Project.Views.ViewIDE.LanguageDelegate.VMDebugger;

/**
 * Area for displaying active variables stored on the stack.
 */
public class StackView extends DebugAreaView {
    
    public StackView(double width, double height, double fontSize, double spacing) {
        
        super(width, height);
        
        
        
    }
    
    @Override
    public void refresh() {
        
        super.refresh();
        
        VMDebugger debugger = getDebugger();
        
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
