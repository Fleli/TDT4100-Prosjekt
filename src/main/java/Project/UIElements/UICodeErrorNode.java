package Project.UIElements;

import java.util.ArrayList;
import java.util.List;

import Project.Compiler.Compiler.Error;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class UICodeErrorNode extends UIButton {
    
    List<Error> errors = new ArrayList<Error>();

    public UICodeErrorNode(double lineWidth, double fontSize, double spacing) {
        
        super(
            new Point2D(lineWidth - (fontSize + spacing), 0),
            new UISize(fontSize, fontSize),
            ""
        );
        
        setImage( new Image(
            "file:TDT4100-prosjekt-frederee/src/main/java/Project/Images/error.png",
            fontSize, fontSize, true, true
        ) );
        
    }
    
    public void clearErrors() {
        errors = new ArrayList<Error>();
        setVisible(false);
    }
    
    public void addError(Error error) {
        setVisible(true);
        errors.add(error);
        // TODO: Jobb mer med denne slik at det vises antall errors på linja
    }
    
}
