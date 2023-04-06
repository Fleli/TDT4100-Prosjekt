package Project.Views;

import Project.Program;
import Project.Documents.Document;
import Project.FileInterface.FileInterface;
import Project.UIElements.UIButton;
import Project.UIElements.UISize;
import Project.UIElements.UITextField;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class ViewNewFile extends UIView {
    
    private static final UISize textFieldSize       = new UISize(375, 40);
    
    private UITextField textField_programName = new UITextField(
        new Point2D(100, 100), 
        textFieldSize,
        "write here ..."
    );
    
    private UITextField textField_author = new UITextField(
        new Point2D(100, 300), 
        textFieldSize,
        "here too ..."
    );
    
    private UIButton button_returnToMenu = new UIButton(
        new Point2D(150, 50), 
        new UISize(40, 40), 
        ""
    );
    
    private UIButton button_moveToEditor = new UIButton(
        new Point2D(500, 600), 
        new UISize(225, 50), 
        "Begynn å skrive!"
    );
    
    public ViewNewFile(UISize size, Program mainProgram) {
        
        super(size);
        
        setMainProgram(mainProgram);
        setBackgroundColor(30, 100, 30);
        
        button_returnToMenu.setImage( new Image(
            "file:TDT4100-prosjekt-frederee/src/main/java/Project/Images/leftArrow.png", 
            40, 40, true, true
        ));
        
        button_returnToMenu.setActionInside( () -> {
            mainProgram().setView( new ViewMenu(size, mainProgram()) );
        });
        
        button_moveToEditor.setActionInside( () -> {
            
            try {
                
                String fileName = textField_programName.getText();
                
                FileInterface.createFileNamed(
                    fileName, "f",
                    textField_author.getText()
                );
                
                Document newDocument = FileInterface.getDocument(fileName, "f");
                mainProgram.beginEditing(newDocument);
                
            } catch (Exception e) {
                
                System.out.println("Caught error " + e.getMessage());
                
            }
            
        });
        
        getChildren().addAll ( textField_programName , textField_author , button_returnToMenu , button_moveToEditor );
        
    }
    
}
