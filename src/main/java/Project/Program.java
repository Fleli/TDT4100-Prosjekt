package Project;

// git ls-files | grep java |  xargs wc -l
// for å finne antall linjer med .java-extension

import Project.Documents.Document;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import Project.Views.ViewMenu;
import Project.Views.ViewIDE.ViewIDE;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Program extends Application {
    
    // Eksperimentelt funnet som nøyaktig skjermstørrelse her på Macbook Pro 13" 2020 (M1)
    public final static UISize viewSize = new UISize(1440, 900);
    
    private Pane root;
    private Scene scene;
    
    private UINode activeView;
    
    // ...
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        initialize();
        
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, (mouse) -> {
            
            UINode.ignoreMouseDown = false;
            
            if ( activeView != null ) {
                activeView.MOUSE_CLICKED( new Point2D(mouse.getX(), mouse.getY()) );
            }
            
        });
        
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (keyEvent) -> {
            
            UINode.ignoreKeyDown = false;
            
            if ( activeView != null ) {
                activeView.KEY_PRESSED(keyEvent);
            }
            
        });
        
        scene.addEventFilter(ScrollEvent.SCROLL, (scroll) -> {
            
            UINode.ignoreDidScroll = false;
            
            double dx = scroll.getDeltaX();
            double dy = scroll.getDeltaY();
            
            double x = scroll.getX();
            double y = scroll.getY();
            
            if ( activeView != null ) {
                activeView.DID_SCROLL(x, y, dx, dy);
            }
            
        });
        
        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stage");
        primaryStage.show();
        
    }
    
    private void initialize() {
        root = new Pane();
        scene = new Scene(root, viewSize.width, viewSize.height);
        setView(new ViewMenu(viewSize, this));
    }
    
    public static void main(String[] args) {
        launch(Program.class, args);
    }
    
    public void setView ( UINode view ) {
        
        if ( activeView != null ) {
            root.getChildren().remove(activeView);
        }
        
        activeView = view;
        root.getChildren().add(view);
        
    }
    
    public void beginEditing ( Document file ) {
        ViewIDE viewIDE = new ViewIDE(viewSize, file);
        setView(viewIDE);
    }
    
}
