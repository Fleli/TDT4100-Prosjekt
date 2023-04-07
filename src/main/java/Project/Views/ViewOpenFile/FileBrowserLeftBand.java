package Project.Views.ViewOpenFile;

import Project.Program;
import Project.UIElements.UIButton;
import Project.UIElements.UIDocumentTableEntry;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class FileBrowserLeftBand extends UINode {
    
    public static final double height = 50;
    public static final double padding = 20;
    public static final double imageSize = 50;
    
    private static final double fontSize = 16;
    
    private Rectangle background;
    
    private UIButton button_open;
    private UIButton button_delete;
    private UIButton button_description;
    private UIButton button_location;
    private UIButton button_revealInFinder;
    
    public FileBrowserLeftBand(ViewFileBrowser fileBrowser) {
        
        super();
        
        double width = Program.viewSize.width - UIDocumentTableEntry.getTableEntryWidth();
        double rectHeight = Program.viewSize.height - ViewFileBrowser.topBandHeight;
        
        background = new Rectangle( width , rectHeight);
        background.setFill( Color.gray(0.8) );
        getChildren().add(background);
        
        double button_width = Program.viewSize.width - UIDocumentTableEntry.getTableEntryWidth() - 2 * padding;
        UISize size = new UISize(button_width, height);
        
        Point2D startLocation = new Point2D(0, 0);
        
        button_open = new UIButton(startLocation, size, "Åpne");
        button_delete = new UIButton(startLocation, size, "Slett");
        button_description = new UIButton(startLocation, size, "Beskrivelse");
        button_location = new UIButton(startLocation, size, "Plassering");
        button_revealInFinder = new UIButton(startLocation, size, "Vis i Finder");
        
        init_button(button_open,            padding,                    "open.png"              );
        init_button(button_delete,          height + 2 * padding,       "delete.png"            );
        init_button(button_description,     2 * height + 3 * padding,   "moreInfo.png"          );
        init_button(button_location,        3 * height + 4 * padding,   "path.png"              );
        init_button(button_revealInFinder,  4 * height + 5 * padding,   "revealInFinder.png"    );
        
        button_open.setActionInside( () -> {
            
            UIDocumentTableEntry selected = fileBrowser.getTable().getSelected();
            
            if (selected != null) {
                fileBrowser.getTable().pressedEnter();
            }
            
        } );
        
        button_delete.setActionInside( () -> {
            
            UIDocumentTableEntry selected = fileBrowser.getTable().getSelected();
            
            if (selected != null) {
                
                // TODO: Slett filen
            
            }
            
        } );
        
        button_description.setActionInside( () -> {
            
            UIDocumentTableEntry selected = fileBrowser.getTable().getSelected();
            
            if (selected != null) {
                
                // TODO: Vis beskrivelse
            
            }
            
        } );
        
        button_location.setActionInside( () -> {
            
            UIDocumentTableEntry selected = fileBrowser.getTable().getSelected();
            
            if (selected != null) {
                
                String folder = "/Users/frederikedvardsen/Desktop/Prosjekt/TDT4100-prosjekt-frederee/src/main/java/Project/Files/";
                String fileName = selected.getDocument().getFileNameWithExtension();
                String path = folder + fileName;
                
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(path);
                clipboard.setContent(content);
                
                // Lag også en animasjon slik at brukeren får vite at path ligger på clipboard
                
            }
            
        } );
        
        button_revealInFinder.setActionInside( () -> {
            
            UIDocumentTableEntry selected = fileBrowser.getTable().getSelected();
            
            if (selected != null) {
                
                String folder = "/Users/frederikedvardsen/Desktop/Prosjekt/TDT4100-prosjekt-frederee/src/main/java/Project/Files/";
                String fileName = selected.getDocument().getFileNameWithExtension();
                String path = folder + fileName;
                
                try {
                        
                    Runtime.getRuntime().exec(new String[]{"open", "-R", path});
                
                } catch (Exception e) {
                    
                    System.out.println("Error " + e.getLocalizedMessage());
                    
                }
                    
            }
            
        } );
        
    }
    
    private void init_button(UIButton button, double y, String imgName) {
        
        if ( imgName == "" ) {
            imgName = "open.png";
        }
        
        Point2D position = new Point2D(padding, y);
        
        button.setTranslateX(position.getX());
        button.setTranslateY(position.getY());
        
        button.setMainLabelFont( new Font("Courier New", fontSize) );
        button.setMainLabelFontColor(Color.rgb(30, 50, 100));
        button.setMainLabelStyle("-fx-font-weight: bold;");
        
        button.setMainLabelTranslationX(imageSize + padding / 2);
        button.setMainLabelTranslationY(height / 2 - fontSize / 2);
        
        button.setFill(180, 180, 180, 0.8);
        
        button.setImage( new Image(
            "file:TDT4100-prosjekt-frederee/src/main/java/Project/Images/FileBrowser/" + imgName,
            imageSize, imageSize, true, true
        ) );
        
        addChild(button);
        
    }
    
}
