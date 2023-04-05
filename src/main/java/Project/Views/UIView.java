package Project.Views;

import Project.Program;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class UIView extends UINode {
    
    
    private Program mainProgram;
    
    private Rectangle background;
    
    
    public UIView(UISize size) {
        
        super();
        
        background = new Rectangle(0, 0, size.width, size.height);
        getChildren().add(background);
        
    }
    
    public void setBackgroundColor( int r, int g, int b ) {
        background.setFill( Color.rgb(r, g, b) );
    }
    
    public void setMainProgram(Program mainProgram) {
        this.mainProgram = mainProgram;
    }
    
    public Program mainProgram() {
        return mainProgram;
    }
    
}
