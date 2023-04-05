package Project.Views.ViewIDE;

import Project.Program;
import Project.UIElements.UIButton;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import Project.VirtualMachine.Runtime;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class ViewIDEMenuElements extends UINode {
    
    public static final double topLineHeight = 100;
    public static final double consoleHeight = 200;
    
    private Rectangle topLine;
    
    private Text text;
    
    private UIButton runButton;
    
    private ViewIDEConsole console;
    
    private ViewIDE ide;
    
    public ViewIDEMenuElements(String fileName, String fileExtension, ViewIDE ide, double consoleWidth) {
        
        super();
        
        this.ide = ide;
        
        topLine = new Rectangle(Program.viewSize.width, topLineHeight);
        topLine.setFill(Color.grayRgb(200));
        getChildren().add(topLine);
        
        text = new Text(fileName + "." + fileExtension);
        text.setTranslateX(20);
        text.setTranslateY(20);
        text.setStyle("-fx-font: bold 20pt \"Courier New\";");
        text.setFill(Color.grayRgb(40));
        getChildren().add(text);
        
        runButton = new UIButton(
            new Point2D(400, 20), 
            new UISize(60, 60), 
            "Compile & run"
        );
        runButton.setActionInside( () -> {
            Runtime.printDebugInfo = true;
            this.ide.compileAndRun();
        });
        runButton.setMainLabelFontColor(Color.grayRgb(200));
        runButton.setImage( new Image(
            "file:TDT4100-prosjekt-frederee/src/main/java/Project/Images/compileAndRun.png",
            60, 60, true, true
        ) );
        addChild(runButton);
        
        double tY = Program.viewSize.height - consoleHeight;
        
        console = new ViewIDEConsole(consoleWidth);
        console.setTranslateY(tY);
        addChild(console);
        
    }
    
    public ViewIDEConsole getConsole() {
        return console;
    }
    
}
