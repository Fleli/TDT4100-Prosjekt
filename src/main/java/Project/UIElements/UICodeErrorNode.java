package Project.UIElements;

import java.util.ArrayList;
import java.util.List;

import Project.Compiler.Compiler.Error;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class UICodeErrorNode extends UIButton {
    
    private List<Error> errors = new ArrayList<Error>();
    
    private double lineWidth;
    
    private double fontSize;
    private double spacing;
    
    private List<UICodeErrorMessageLine> errorMessageLines = new ArrayList<UICodeErrorMessageLine>();

    public UICodeErrorNode(double lineWidth, double fontSize, double spacing) {
        
        super(
            new Point2D(lineWidth - (fontSize + spacing), 0),
            new UISize(fontSize + spacing, fontSize + spacing),
            ""
        );
        
        this.lineWidth = lineWidth;
        
        this.fontSize = fontSize;
        this.spacing = spacing;
        
        Image image = new Image(
            "file:TDT4100-prosjekt-frederee/src/main/java/Project/Images/xcodeError.png",
            fontSize + spacing, fontSize + spacing, true, true
        );
        
        setImage( image );
        
        setActionInside( () -> {
            show();
        } );
        
        setActionOutside( () -> {
            hide();
        } );
        
        hide();
        
        setVisible(false);
        
        setTransparent();
        
    }
    
    public void addError(Error error) {
        
        setVisible(true);
        
        /*if ( errors.size() == 0 ) {
            
            TranslateTransition animation = new TranslateTransition( new Duration(200) , this );
            animation.setCycleCount(1);
            animation.setByX(-30);
            animation.play();
            
        }*/
        
        errors.add(error);
        
        double ty = errors.size() * (fontSize + spacing);
        
        UICodeErrorMessageLine errorLine = new UICodeErrorMessageLine(error.getMessage(), lineWidth - 200, fontSize + spacing, fontSize);
        errorLine.setTranslateY(ty);
        getChildren().add(errorLine);
        
        errorMessageLines.add(errorLine);
        
    }
    
    public boolean isEmpty() {
        return (errors.size() == 0);
    }
    
    private void hide() {
        
        for ( UICodeErrorMessageLine messageLine : errorMessageLines ) {
            messageLine.setVisible(false);
        }
        
    }
    
    private void show() {
        
        for ( UICodeErrorMessageLine messageLine : errorMessageLines ) {
            messageLine.setVisible(true);
        }
        
    }
    
}
