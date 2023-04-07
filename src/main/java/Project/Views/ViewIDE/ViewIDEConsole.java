package Project.Views.ViewIDE;

import Project.Console;
import Project.UIElements.UIConsoleLine;
import Project.UIElements.UINode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ViewIDEConsole extends UINode implements Console {
    
    private static final double fontSize = 16;
    private static final double linePadding = 5;
    
    private UIConsoleLine topLine;
    private UIConsoleLine currentLine;
    
    private Rectangle background;
    
    private int lineCount = 1;
    
    public ViewIDEConsole(double width, double height) {
        
        super();
        
        background = new Rectangle(width, height);
        background.setFill(Color.rgb(50, 75, 75));
        background.setViewOrder(-1);
        getChildren().add(background);
        
        clear();
        
    }
    
    public void delegatedScroll(double dx, double dy) {
        
        if (topLine != null) {
            
            double oldTY = topLine.getTranslateY();
            double newTY = oldTY + dy * 0.6;
            
            topLine.setTranslateY(newTY);
            fitInBounds();
            
        }
        
    }
    
    public void incrementLineCount() {
        lineCount++;
    }
    
    @Override
    public void print(String text) {
        currentLine.print(text);
    }
    
    @Override
    public void clear() {
        
        if ( topLine != null ) {
            getChildren().remove(topLine);
        }
        
        topLine = new UIConsoleLine(fontSize, 20, this);
        topLine.setTranslateX(linePadding);
        
        lineCount = 1;
        
        getChildren().add(topLine);
        currentLine = topLine;
        
    }
    
    @Override
    public void newLine() {
        print("\n");
    }
    
    public void setCurrentLine(UIConsoleLine newLine) {
        currentLine = newLine;
    }
    
    private void fitInBounds() {
        
        double y = topLine.getTranslateY();
        
        y = Math.min(y, 0);
        y = Math.max(y, (2 - lineCount) * (fontSize + UIConsoleLine.spacing));
        
        topLine.setTranslateY(y);
        
    }
    
}
