package Project.Views.ViewIDE.DebugArea;

import Project.UIElements.UIConsoleLine;
import Project.UIElements.UINode;
import Project.Views.ViewIDE.LanguageDelegate.VMDebugger;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DebugAreaView extends UINode {
    
    private static final double fontSize = 16;
    private static final double spacing = 6;
    private static final double linePadding = 5;
    
    private static final double topPadding = 6;
    
    private UIConsoleLine topLine;
    private UIConsoleLine currentLine;
    
    private double scroll = 0;
    
    private Rectangle background;
    
    private int lineCount = 1;
    
    private int max_chars;
    
    private VMDebugger debugger;
    
    public DebugAreaView(double width, double height) {
        
        background = new Rectangle(width, height);
        background.setFill(Color.AQUAMARINE);
        getChildren().add(background);
        
        max_chars = (int) Math.floor(width / (fontSize * 0.6));
        
        clear();
        fitInBounds();
        
    }
    
    public void setDebugger(VMDebugger debugger) {
        this.debugger = debugger;
    }
    
    public VMDebugger getDebugger() {
        return debugger;
    }
    
    public double getFontSize() {
        return fontSize;
    }
    
    public double getSpacing() {
        return spacing;
    }
    
    /**
     * Should be overridden by subclasses to refresh the DebugAreaView's UI
     * NOTE: super.refresh() must still be called, since this class handles
     * clearing (preparation) of the console.
     */
    public void refresh() {
        clear();
    }
    
    public void delegatedScroll(double dx, double dy) {
        
        if (topLine != null) {
            
            double oldTY = topLine.getTranslateY();
            double newTY = oldTY + dy * 0.6;
            
            topLine.setTranslateY(newTY);
            fitInBounds();
            
        } else {
            
            System.out.println("Topline was null");
            
        }
        
    }
    
    public void incrementLineCount() {
        lineCount++;
    }
    
    public void print(String text) {
        currentLine.print(text);
    }
    
    public void print(String text, Color color, String style) {
        currentLine.print(text, color, style);
    }
    
    public void clear() {
        
        if ( topLine != null ) {
            getChildren().remove(topLine);
        }
        
        topLine = new UIConsoleLine(fontSize, max_chars, this);
        topLine.setTranslateX(linePadding);
        topLine.setTranslateY(topPadding);
        
        lineCount = 1;
        
        getChildren().add(topLine);
        currentLine = topLine;
        
    }
    
    public void finishedViewUpdate_moveToScroll() {
        
        topLine.setTranslateY(scroll);
        fitInBounds();
        
    }
    
    public void newLine() {
        print("\n");
    }
    
    public void setCurrentLine(UIConsoleLine newLine) {
        currentLine = newLine;
    }
    
    private void fitInBounds() {
        
        double y = topLine.getTranslateY();
        
        y = Math.min(y, topPadding);
        y = Math.max(y, (2 - lineCount) * (fontSize + UIConsoleLine.spacing) + topPadding);
        
        topLine.setTranslateY(y);
        
        scroll = y;
        
    }
    
    public void setFill(int r, int g, int b) {
        background.setFill(Color.rgb(r, g, b));
    }
    
}
