package Project.Views.ViewIDE.LanguageDelegates;

import Project.UIElements.UICodeLine;
import Project.UIElements.UINode;
import javafx.scene.paint.Color;

public interface LanguageDelegate {
    
    public UINode getIDEUpperBand(Color color, double height, UICodeLine topLine);
    public UINode getIDELowerBand(Color color, double height);
    
    public UINode getDebugArea();
    
    public void run();
    public void run(String sourceCode, int stack_size, int heap_size);
    
    public void debug();
    public void debugger_nextClock(UICodeLine topLine);
    
    public void syntaxHighlight(UICodeLine line);
    
    public void scrolledInDebugArea(double dx, double dy);
    
    /**
     * Have the language delegate react anytime text is written, for example
     * auto-compiling and error message display.
     * @param topLine NOT necessarily the line that text was written to, but the
     * line at the top (first in the {@code UICodeLine} linked list)
     */
    public void reactOnTextWritten(UICodeLine topLine);
    
}
