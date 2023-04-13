package Project.Views;

import java.io.IOException;

import Project.Program;
import Project.Documents.Document;
import Project.FileInterface.FileInterface;
import Project.UIElements.UIButton;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import Project.UIElements.UITextField;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class NewFileWindow extends UINode {
    
    private static final String allowedChars = 
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    ;
    
    private Label label_title;
    private Label label_fileName;
    private Label label_author;
    
    private UIButton window;
    private Rectangle background;
    
    private UITextField textField_fileName;
    private UITextField textField_author;
    
    private UIButton button_beginEditing;
    
    public NewFileWindow(double width, double height, double fontSize, Program mainProgran) {
        
        background = new Rectangle(Program.viewSize.width, Program.viewSize.height);
        getChildren().add(background);
        
        window = new UIButton(
            new Point2D(Program.viewSize.width / 2 - width / 2, Program.viewSize.height / 2 - height / 2), 
            new UISize(width, height), 
            ""
        );
        addChild(window);
        
        window.setActionOutside( () -> {
            this.setVisible(false);
        } );
        
        double textFieldWidth = 0.6 * width;
        double textFieldHeight = 0.1 * height;
        
        textField_fileName = new UITextField(
            new Point2D(width * 0.3, height * 0.2), 
            new UISize(textFieldWidth, textFieldHeight), 
            "filnavn ..."
        );
        
        textField_author = new UITextField(
            new Point2D(width * 0.3, height * 0.45), 
            new UISize(textFieldWidth, textFieldHeight),
            "forfatter ..."
        );
        
        style(textField_author, fontSize);
        style(textField_fileName, fontSize);
        
        int charLimit = 20;
        textField_fileName.setCharLimit(charLimit);
        textField_author.setCharLimit(charLimit);
        
        textField_fileName.setAllowedInput(allowedChars + "_");
        textField_author.setAllowedInput(allowedChars + " ");
        
        button_beginEditing = new UIButton(
            new Point2D(0.6 * width, height - textFieldHeight * 1.1 - 40), 
            new UISize(0.3 * width, textFieldHeight * 1.1), 
            "Opprett"
        );
        button_beginEditing.setActionInside( () -> {
            
            String fileName = textField_fileName.getText();
            String author = textField_author.getText();
            
            try {
                
                FileInterface.createFileNamed(fileName, "f", author);
                Document document = FileInterface.getDocument(fileName, "f");
                mainProgran.beginEditing(document);
                
            } catch (IOException exception) {
                
                System.out.println("File error: " + exception.getLocalizedMessage());
                
            }
            
        } );
        button_beginEditing.setFill(90, 180, 170);
        button_beginEditing.setMainLabelFont( new Font("Courier New", fontSize + 10) );
        button_beginEditing.setMainLabelStyle("-fx-font-weight: bold;");
        button_beginEditing.setMainLabelFontColor(Color.LIGHTGRAY);
        button_beginEditing.setMainLabelTranslationX(30);
        window.addChild(button_beginEditing);
        
        label_title = new Label("Opprett ny .f-fil");
        label_fileName = new Label("Filnavn");
        label_author = new Label("Forfatter");
        
        style(label_title, fontSize + 6, 20, 20);
        style(label_fileName, fontSize, width * 0.1, height * 0.2 + 5);
        style(label_author, fontSize, width * 0.1, height * 0.45 + 5);
        
        label_title.setStyle("-fx-font-weight: bold;");
        
    }
    
    private void style(UITextField textField, double fontSize) {
        textField.setFill(120, 160, 150);
        textField.setMainLabelFont( new Font("Courier New", fontSize) );
        textField.setMainLabelFontColor(Color.WHITE);
        textField.setPlaceholderTextColor(Color.gray(0.8));
        window.addChild(textField);
    }
    
    private void style(Label label, double fontSize, double x, double y) {
        label.setTextFill(Color.BLACK);
        label.setFont( new Font("Courier New", fontSize) );
        label.setTranslateX(x);
        label.setTranslateY(y);
        window.getChildren().add(label);
    }
    
    public void setBackgroundColor(int r, int g, int b, double o) {
        background.setFill(Color.rgb(r, g, b, o));
    }
    
    public void setWindowColor(int r, int g, int b) {
        window.setFill(r, g, b);
    }
    
}
