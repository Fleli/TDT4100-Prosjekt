package Project.Views.ViewOpenFile;

import Project.Program;
import Project.UIElements.UIButton;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import Project.Views.ViewMenu;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class FileBrowserTopBand extends UINode {
    
    private Rectangle background;
    private Rectangle separationLine;
    
    private UIButton button_backToMenu;
    
    public FileBrowserTopBand(UISize size, Program mainProgram) {
        
        super();
        
        background = new Rectangle( Program.viewSize.width , ViewFileBrowser.topBandHeight );
        background.setFill( Color.rgb(40, 70, 140) );
        getChildren().add(background);
        
        separationLine = new Rectangle( Program.viewSize.width , 4);
        separationLine.setFill(Color.BLACK);
        separationLine.setTranslateY(background.getHeight() - separationLine.getHeight());
        getChildren().add(separationLine);
        
        button_backToMenu = new UIButton(
            new Point2D(20, 20), 
            new UISize(50, 50), 
            ""
        );
        button_backToMenu.setImage( new Image(
            "file:TDT4100-prosjekt-frederee/src/main/java/Project/Images/leftArrow.png",
            50, 50, true, true
        ) );
        button_backToMenu.setActionInside( () -> {
            mainProgram.setView( new ViewMenu(size, mainProgram) );
        } );
        button_backToMenu.setTransparent();
        addChild(button_backToMenu);
        
    }
    
    
}
