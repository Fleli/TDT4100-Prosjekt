package Project.UIElements;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Attributed label using the Courier New font.
 */
public class UILabel extends UINode {
    
    private double fontSize;
    
    public UILabel(double fontSize) {
        
        super();
        
        this.fontSize = fontSize;
        
    }
    
    public void addAttributedText(String text, Color color, int col, String special) {
        
        Label textNode = new Label(text);
        
        textNode.setFont( new Font("Courier New", fontSize) );
        textNode.setTextFill(color);
        
        if ( special != null ) {
            textNode.setStyle(special);
        }
        
        double translateX = col * fontSize * 0.6;
        textNode.setTranslateX(translateX);
        
        getChildren().add(textNode);
        
    }
    
}
