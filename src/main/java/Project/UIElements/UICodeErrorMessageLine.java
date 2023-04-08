package Project.UIElements;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class UICodeErrorMessageLine extends UINode {
    
    public static final double increase = 20;
    
    private Rectangle background;
    private Label label;
    
    public UICodeErrorMessageLine(String message, double width, double height, double fontSize, String severity) {
        
        super();
        
        background = new Rectangle(width, height);
        background.setTranslateX(-width);
        
        switch (severity) {
            case "issue":
                background.setFill(Color.rgb(150, 40, 40, 0.8));
                break;
            case "warning":
                background.setFill(Color.rgb(180, 150, 30));
                break;
            default:
                throw new IllegalStateException("Error severity is " + severity + ", which is not supported.");
        }
        
        getChildren().add(background);
        
        label = new Label(message);
        label.setFont( new Font("Courier New", fontSize) );
        label.setStyle("-fx-font-weight: bold;");
        label.setTextFill(Color.rgb(235, 235, 235));
        label.setTranslateX(-width + 20);
        getChildren().add(label);
        
        setVisible(false);
        
    }
    
}
