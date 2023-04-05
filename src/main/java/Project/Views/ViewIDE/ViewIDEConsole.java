package Project.Views.ViewIDE;

import Project.Console;
import Project.UIElements.UIConsoleLine;
import Project.UIElements.UINode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ViewIDEConsole extends UINode implements Console {
    
    private static final double fontSize = 13;
    
    private UIConsoleLine topLine;
    private UIConsoleLine currentLine;
    
    private Rectangle background;
    
    public ViewIDEConsole(double width) {
        
        super();
        
        double height = ViewIDEMenuElements.consoleHeight;
        
        background = new Rectangle(width, height);
        background.setFill(Color.rgb(50, 75, 75));
        background.setViewOrder(-1);
        getChildren().add(background);
        
        clear();
        
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
        
        getChildren().add(topLine);
        currentLine = topLine;
        
    }
    
    public void setCurrentLine(UIConsoleLine newLine) {
        currentLine = newLine;
    }
    
}
