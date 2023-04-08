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
    
    private Image image_issue;
    private Image image_warning;
    
    public UICodeErrorNode(UICodeLine line) {
        
        super(
            new Point2D(line.getCodeLineWidth() - (line.getFontSize() + line.getSpacing()), 0),
            new UISize(line.getFontSize() + line.getSpacing(), line.getFontSize() + line.getSpacing()),
            ""
        );
        
        this.lineWidth = line.getCodeLineWidth();
        this.fontSize = line.getFontSize();
        this.spacing = line.getSpacing();
        
        image_issue = new Image(
            "file:TDT4100-prosjekt-frederee/src/main/java/Project/Images/Editor/xcodeError.png",
            fontSize + spacing, fontSize + spacing, true, true
        );
        
        image_warning = new Image(
            "file:TDT4100-prosjekt-frederee/src/main/java/Project/Images/Editor/warning.png",
            fontSize + spacing, fontSize + spacing, true, true
        );
        
        setImage(image_warning);
        
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
        errors.add(error);
        
    }
    
    public boolean isEmpty() {
        return (errors.size() == 0);
    }
    
    public List<Error> getErrors() {
        return errors;
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
    
    public void finished() {
        
        int index = 1;
        
        for ( Error error : errors ) {
                
            if (error.getSeverity().equals("issue")) {
                setImage(image_issue);
            }
            
            double ty = index * (fontSize + spacing);
            
            UICodeErrorMessageLine errorLine = new UICodeErrorMessageLine(
                error.getMessage(), 
                lineWidth - 200, 
                fontSize + spacing, 
                fontSize,
                error.getSeverity()
            );
            errorLine.setTranslateY(ty);
            getChildren().add(errorLine);
            
            errorMessageLines.add(errorLine);
            
            index += 1;
            
        }
        
    }
    
}
