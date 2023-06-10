package Project.Views;

import Project.Program;
import Project.FileInterface.FileInterface;
import Project.UIElements.UIButton;
import Project.UIElements.UISize;
import Project.Views.ViewOpenFile.ViewFileBrowser;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ViewMenu extends UIView {
    
    private static final double     fromLeftEdge        =   150;
    private static final double     logoFromTop         =   60;
    private static final double     logoToButtons       =   50;
    private static final double     buttonSpace         =   20;
    private static final double     logoWithAndHeight   =   250;
    private static final UISize     buttonSize          =   new UISize(300, 80);
    
    private static final double     newFileWindowWidth  =   600;
    private static final double     newFileWindowHeight =   400; 
    
    private UIButton btn_newFile    = new UIButton( 
        new Point2D(fromLeftEdge, logoFromTop + logoWithAndHeight + logoToButtons + (buttonSize.height + buttonSpace) * 0), 
        buttonSize, 
        "Ny fil"
    );
    
    private UIButton btn_openFile   = new UIButton( 
        new Point2D(fromLeftEdge, logoFromTop + logoWithAndHeight + logoToButtons + (buttonSize.height + buttonSpace) * 1), 
        buttonSize, 
        "Åpne fil"
    );
    
    private NewFileWindow newFileWindow;
    
    private ImageView imgView_logo = new ImageView( new Image(
        "file:src/main/java/Project/Images/xcodelogo.png",
        logoWithAndHeight, logoWithAndHeight, true, true
    ));
    
    public ViewMenu ( UISize size , Program mainProgram ) {
        
        super(size);
        
        setMainProgram(mainProgram);
        
        newFileWindow = new NewFileWindow(newFileWindowWidth, newFileWindowHeight, 16, mainProgram);
        newFileWindow.setViewOrder(-5);
        newFileWindow.setVisible(false);
        newFileWindow.setBackgroundColor(150, 150, 150, 0.75);
        newFileWindow.setWindowColor(230, 240, 230);
        addChild(newFileWindow);
        
        setBackgroundColor(180, 200, 190);
        
        btn_newFile.setSubLabelText("Opprett en ny .f-kildekodefil i programmets filmappe");
        btn_newFile.setFill(255, 255, 255);
        addChild(btn_newFile);
        
        btn_openFile.setSubLabelText("Åpne en fil som har blitt opprettet og lagret tidligere.");
        btn_openFile.setFill(255, 255, 255);
        addChild(btn_openFile);
        
        btn_newFile.setActionInside( () -> {
            newFileWindow.setVisible(true);
        });
        
        btn_openFile.setActionInside( () -> {
            
            if (newFileWindow.isVisible()) return;
            
            try {
                viewTransition( new ViewFileBrowser(size, FileInterface.getAllDocuments(), mainProgram) );
            } catch (Exception e) {
                System.out.println("FEIL:");
                System.out.println(e.getLocalizedMessage());
                System.exit(1);
            }
            
        });
        
        imgView_logo.setTranslateX(fromLeftEdge + buttonSize.width / 2 - logoWithAndHeight / 2);
        imgView_logo.setTranslateY(logoFromTop);
        getChildren().add(imgView_logo);
        
    }
    
    public void viewTransition ( UIView newView ) {
        mainProgram().setView(newView);
    }
    
}
