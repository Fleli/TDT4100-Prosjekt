package Project.UIElements;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class UICodeErrorMessageLine extends UINode {
    
    public static final double increase = 20;
    
    private Rectangle background;
    private Label label;
    
    public UICodeErrorMessageLine(String message, double width, double height, double fontSize) {
        
        super();
        
        background = new Rectangle(width, height);
        background.setTranslateX(-width);
        background.setFill(Color.rgb(150, 40, 40, 0.8));
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
