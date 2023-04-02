package Project.IDE;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class Editor extends Pane {
    
    
    private Container container = new Container();
    
    
    
    public Editor() {
        
        super();
        
        getChildren().add(container);
        
    }
    
    
    public void keyAction(KeyEvent key) {
        container.keyAction(key);
    }
    
    public void mouseAction(MouseEvent mouse) {
        
        Point2D p = new Point2D(mouse.getX(), mouse.getY());
        
        if ( container.contains(p) ) {
            
            Point2D point = new Point2D(
                p.getX() - container.getTranslateX() - getTranslateX(), 
                p.getY() - container.getTranslateY() - getTranslateY()
            );
            
            container.didClick(point);
            
        } else {
            
            
            
        }
        
    }
    
    
    
}
