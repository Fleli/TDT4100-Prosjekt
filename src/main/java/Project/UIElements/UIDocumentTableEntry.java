package Project.UIElements;

import Project.Program;
import Project.FileInterface.Document;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class UIDocumentTableEntry extends UIButton {
    
    public static final double tableEntryWidth = 1300;
    
    // fileImg, fileName, author, creation, open, fileSize, fileType
    private static final double[] colWidths = { 
        40, 260, 260, 120, 120, 140, 225
    };
    
    private UIDocumentTableEntry above;
    private UIDocumentTableEntry below;
    
    private Document document;
    
    private boolean isSelected = false;
    private boolean isOddRow;
    private boolean isTitleRow;
    
    private UIDocumentTable table;
    
    private double fontSize;
    private double spacing;
    
    private ImageView imgView;
    
    private Label label_fileName;
    private Label label_author;
    private Label label_created;
    private Label label_opened;
    private Label label_size;
    private Label label_type;
    
    public UIDocumentTableEntry(double fontSize, double spacing, Document document, UIDocumentTable table, boolean isOddRow) {
        
        super(
            new Point2D(0, 0), 
            new UISize(getTableEntryWidth(), fontSize + spacing), 
            ""
        );
        
        this.fontSize = fontSize;
        this.spacing = spacing;
        this.table = table;
        this.isOddRow = isOddRow;
        this.document = document;
        
        setViewOrder(1);
        
        imgView = new ImageView( new Image( 
            "file:TDT4100-prosjekt-frederee/src/main/java/Project/Images/FileFormats/" + document.getExtension() + ".png",
            fontSize + spacing, fontSize + spacing, true, true
        ) );
        getChildren().add(imgView);
        
        isTitleRow = false;
        
        init(
            document.getFileNameWithExtension(), 
            document.getAuthor(), 
            document.getCreationDate_formattedString(),
            document.getOpenDate_formattedString(),
            document.getFileSize() + " bytes",
            document.getTypeDescription()
        );
        
    }
    
    public UIDocumentTableEntry(String[] tableTitles, double fontSize, double spacing, UIDocumentTable table) {
        
        super(
            new Point2D(0, 0), 
            new UISize(getTableEntryWidth(), fontSize + spacing), 
            ""
        );
        
        this.fontSize = fontSize;
        this.spacing = spacing;
        this.table = table;
        
        if ( tableTitles.length != colWidths.length - 1 ) {
            throw new IllegalArgumentException("Mismatch between column widths (" + colWidths.length + ") and tableTitles (" + tableTitles.length + ")");
        }
        
        isTitleRow = true;
        
        double screenWidth = Program.viewSize.width;
        double tableWidth = getTableEntryWidth();
        double translateX = screenWidth - tableWidth;
        setTranslateX(translateX);
        
        init(
            tableTitles[0],
            tableTitles[1],
            tableTitles[2],
            tableTitles[3],
            tableTitles[4],
            tableTitles[5]
        );
        
    }
    
    private void init(String fileName, String author, String created, String opened, String size, String type) {
        
        label_fileName = new Label(fileName);
        label_fileName.setTranslateX(colWidths[0]);
        label_fileName.setMaxWidth(colWidths[1]);
        setFontAndAddChild(label_fileName);
        
        label_author = new Label(author);
        label_author.setTranslateX(colWidths[1] + colWidths[0]);
        label_author.setMaxWidth(colWidths[2]);
        setFontAndAddChild(label_author);
        
        label_created = new Label(created);
        label_created.setTranslateX(colWidths[2] + colWidths[1] + colWidths[0]);
        label_created.setMaxWidth(colWidths[3]);
        setFontAndAddChild(label_created);
        
        label_opened = new Label(opened);
        label_opened.setTranslateX(colWidths[3] + colWidths[2] + colWidths[1] + colWidths[0]);
        label_opened.setMaxWidth(colWidths[4]);
        setFontAndAddChild(label_opened);
        
        label_size = new Label(size);
        label_size.setTranslateX(colWidths[4] + colWidths[3] + colWidths[2] + colWidths[1] + colWidths[0]);
        label_size.setMaxWidth(colWidths[5]);
        setFontAndAddChild(label_size);
        
        label_type = new Label(type);
        label_type.setTranslateX(colWidths[5] + colWidths[4] + colWidths[3] + colWidths[2] + colWidths[1] + colWidths[0]);
        label_type.setMaxWidth(colWidths[6]);
        setFontAndAddChild(label_type);
        
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
        if (isTitleRow) {
            setFill(50, 80, 140);
        } else if (isSelected) {
            setFill(20, 60, 20);
        } else if (isOddRow) {
            setFill(140, 140, 140);
        } else {
            setFill(120, 120, 120);
        }
    }
    
    public void addBelow(Document documentBelow) {
        
        UIDocumentTableEntry entry = new UIDocumentTableEntry(fontSize, spacing, documentBelow, table, !isOddRow);
        
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
        
        if ( keyEvent.getCode() == KeyCode.UP  &&  above != null  &&  !above.isTitleRow ) {
            table.select(above);
        } else if ( keyEvent.getCode() == KeyCode.DOWN  &&  below != null ) {
            table.select(below);
        }
        
        super.keyDown(keyEvent);
        
    }
    
    public Document getDocument() {
        return document;
    }
    
    @Override
    public String toString() {
        return document.getFileNameWithExtension();
    }
    
    public boolean isSelected() {
        return isSelected;
    }
    
    public static double getTableEntryWidth() {
        return colWidths[0] + colWidths[1] + colWidths[2] + colWidths[3] + colWidths[4] + colWidths[5] + colWidths[6];
    }
    
}
