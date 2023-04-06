package Project.UIElements;

import Project.Documents.Document;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class UIDocumentTableEntry extends UIButton {
    
    public static final double tableEntryWidth = 1100;
    
    private UIDocumentTableEntry above;
    private UIDocumentTableEntry below;
    
    private Document document;
    
    private boolean isSelected = false;
    
    private UIDocumentTable table;
    
    private double fontSize;
    private double spacing;
    
    private ImageView imgView;
    
    private Label label_fileName;
    private Label label_author;
    private Label label_created;
    private Label label_opened;
    
    // TODO: Legg til label med size
    
    public UIDocumentTableEntry(double fontSize, double spacing, Document document, UIDocumentTable table) {
        
        super( 
            new Point2D(0, 0), 
            new UISize(tableEntryWidth, fontSize + spacing), 
            ""
        );
        
        this.fontSize = fontSize;
        this.spacing = spacing;
        
        this.table = table;
        
        this.document = document;
        
        imgView = new ImageView( new Image( 
            "file:TDT4100-prosjekt-frederee/src/main/java/Project/Images/fileFormat_" + document.getExtension() + ".png",
            fontSize + spacing, fontSize + spacing, true, true
        ) );
        getChildren().add(imgView);
        
        label_fileName = new Label(document.getFileName() + "." + document.getExtension());
        label_fileName.setTranslateX(50);
        setFontAndAddChild(label_fileName);
        
        label_author = new Label(document.getAuthor());
        label_author.setTranslateX(350);
        setFontAndAddChild(label_author);
        
        label_created = new Label(document.getCreationDate_formattedString());
        label_created.setTranslateX(650);
        setFontAndAddChild(label_created);
        
        label_opened = new Label(document.getOpenDate_formattedString());
        label_opened.setTranslateX(850);
        setFontAndAddChild(label_opened);
        
        setActionInside( () -> {
            activate();
        } );
        
        setActionOutside( () -> {
            deactivate();
        } );
        
        refreshUI();
        
    }
    
    private void setFontAndAddChild(Label label) {
        
        Font tableFont = new Font("Courier New", fontSize);
        Color fontColor = Color.rgb(225, 225, 225);
        
        label.setFont(tableFont);
        label.setTextFill(fontColor);
        label.setStyle("-fx-font-weight: bold;");
        
        getChildren().add(label);
        
    }
    
    public void activate() {
        isSelected = true;
        refreshUI();
    }
    
    public void deactivate() {
        isSelected = false;
        refreshUI();
    }
    
    private void refreshUI() {
        if (isSelected) {
            setFill(20, 60, 20);
        } else {
            setFill(140, 140, 140);
        }
    }
    
    public void addBelow(Document documentBelow) {
        
        UIDocumentTableEntry entry = new UIDocumentTableEntry(fontSize, spacing, documentBelow, table);
        
        entry.setTranslateY(fontSize + spacing);
        
        entry.above = this;
        this.below = entry;
        
        addChild(entry);
        
        table.setLastRow(entry);
        
    }
    
    @Override
    public void mouseDown(Point2D location) {
        
        super.mouseDown(location);
        
        if (isSelected) {
            table.select(this);
        }
        
    }
    
    @Override
    public void keyDown(KeyEvent keyEvent) {
        
        if (!isSelected) {
            return;
        }
        
        ignoreKeyDown = true;
        
        if ( keyEvent.getCode() == KeyCode.UP  &&  above != null ) {
            table.select(above);
        } else if ( keyEvent.getCode() == KeyCode.DOWN  &&  below != null ) {
            table.select(below);
        }
        
        super.keyDown(keyEvent);
        
    }
    
    public Document getDocument() {
        return document;
    }
    
}
