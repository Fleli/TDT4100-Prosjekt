package Project;

import Project.IDE.Editor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        Pane root = new Pane(); // 2560, 1600
        Scene scene = new Scene(root, 2560, 1600);
        
        Editor editor = new Editor();
        
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            editor.keyAction(key);
        });
        
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, (mouse) -> {
            editor.mouseAction(mouse);
        });
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stage");
        primaryStage.show();
        
        root.getChildren().add(editor);
        
    }
    
    public static void main(String[] args) {
        launch(App.class, args);
    }
    
}
