package Project.UIElements;

import Project.Views.ViewIDE.ViewIDEConsole;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class UIConsoleLine extends UINode {
    
    public static final double spacing = 12;
    
    private Label label;
    private StringBuilder text = new StringBuilder("");
    
    private double maxChars;
    private double fontSize;
    
    private UIConsoleLine line_below;
    
    private ViewIDEConsole console;
    
    public UIConsoleLine(double fontSize, double maxChars, ViewIDEConsole console) {
        
        this.console = console;
        
        this.maxChars = maxChars;
        this.fontSize = fontSize;
        
        label = new Label();
        label.setFont( new Font("Courier New", fontSize) );
        label.setTextFill(Color.grayRgb(235));
        
        getChildren().add(label);
        
        setViewOrder(-2);
        
        console.setCurrentLine(this);
        
    }
    
    public void print(String text) {
        
        int index = 0;
        
        while ( index < text.length() ) {
            
            char next = text.charAt(index);
            
            if ( next == '\n' ) {
                
                index++;
                
                newLine();
                String remaining = text.substring(index);
                line_below.print(remaining);
                
                break;
                
            } else if ( this.text.length() >= maxChars ) {
                
                newLine();
                String remaining = text.substring(index);
                line_below.print(remaining);
                
                break;
                
            } else {
                
                this.text.append(next);
                index++;
                
            }
            
        }
        
        label.setText(this.text.toString());
        
    }
    
    public void newLine() {
        
        UIConsoleLine newBelow = new UIConsoleLine(fontSize, maxChars, console);
        newBelow.setTranslateY(fontSize + spacing);
        
        console.incrementLineCount();
        addChild(newBelow);
        
        line_below = newBelow;
        
    }
    
}
