package Project.Views;

import Project.Program;
import Project.FileInterface.FileInterface;
import Project.UIElements.UIButton;
import Project.UIElements.UISize;
import Project.Views.ViewOpenFile.ViewOpenFile;
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
    
    private UIButton btn_settings   = new UIButton( 
        new Point2D(fromLeftEdge, logoFromTop + logoWithAndHeight + logoToButtons + (buttonSize.height + buttonSpace) * 2), 
        buttonSize,
        "Innstillinger"
    );
    
    private ImageView imgView_logo = new ImageView( new Image(
        "file:TDT4100-prosjekt-frederee/src/main/java/Project/Images/xcodelogo.png",
        logoWithAndHeight, logoWithAndHeight, true, true
    ));
    
    public ViewMenu ( UISize size , Program mainProgram ) {
        
        super(size);
        
        setMainProgram(mainProgram);
        
        setBackgroundColor(180, 200, 190);
        
        btn_newFile.setSubLabelText("Lag en ny fil, for eksempel en .f-kildekodefil eller en tekstfil.");
        btn_newFile.setFill(255, 255, 255);
        addChild(btn_newFile);
        
        btn_openFile.setSubLabelText("Åpne en fil som har blitt opprettet og lagret tidligere.");
        btn_openFile.setFill(255, 255, 255);
        addChild(btn_openFile);
        
        btn_settings.setSubLabelText("Se gjennom eller endre innstillingene for IDE-en.");
        btn_settings.setFill(255, 255, 255);
        addChild(btn_settings);
        
        btn_newFile.setActionInside( () -> {
            viewTransition( new ViewNewFile(size, mainProgram()) );
        });
        
        btn_openFile.setActionInside( () -> {
            
            try {
                viewTransition( new ViewOpenFile(size, FileInterface.getAllDocuments(), mainProgram) );
            } catch (Exception e) {
                System.out.println(e);
            }
            
        });
        
        btn_settings.setActionInside( () -> {
            //btn_settings.flipColor();
        });
        
        imgView_logo.setTranslateX(fromLeftEdge + buttonSize.width / 2 - logoWithAndHeight / 2);
        imgView_logo.setTranslateY(logoFromTop);
        getChildren().add(imgView_logo);
        
    }
    
    public void viewTransition ( UIView newView ) {
        mainProgram().setView(newView);
    }
    
}
