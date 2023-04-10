package Project.Views.ViewIDE.LanguageDelegates.Delegate_f;

import Project.Program;
import Project.UIElements.UIButton;
import Project.UIElements.UICodeLine;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class TopBand_f extends UINode {
    
    private static final double padding = 30;
    private static final double textSpace = 140;
    
    private Rectangle upperBand;
    
    private UIButton button_run;
    private UIButton button_compile;
    private UIButton button_debug;
    
    private UIButton button_debug_clock;
    
    private double height;
    
    public TopBand_f(Color bandColor, double height, Delegate_f delegate, UICodeLine topLine) {
        
        super();
        
        this.height = height;
        
        upperBand = new Rectangle(Program.viewSize.width, height);
        upperBand.setFill(bandColor);
        upperBand.toFront();
        getChildren().add(upperBand);
        
        Point2D zero = new Point2D(0, 0);
        UISize buttonSize = new UISize(height - 2 * padding + textSpace, height - 2 * padding);
        
        button_run = new UIButton(zero, buttonSize, "Kjør");
        button_compile = new UIButton(zero, buttonSize, "Kompiler");
        button_debug = new UIButton(zero, buttonSize, "Debug");
        
        init_button(button_run      , 400       , "ferdig.png"  );
        init_button(button_compile  , 700       , "hammer.png"  );
        init_button(button_debug    , 1000      , "diagram.png" );
        
        button_run.setActionInside( () -> {
            delegate.clearDebugArea();
            delegate.requestDebugAreaView(0);
            delegate.run();
        } );
        
        button_compile.setActionInside( () -> {
            // Compile, og produser executable (og vis info om denne på et vis)
        } );
        
        button_debug.setActionInside( () -> {
            delegate.debug();
            // Legg til debug-knapper
        } );
        
        button_debug_clock = new UIButton(zero, buttonSize, "Debugclock");
        init_button(button_debug_clock, 0, "ferdig.png");
        
        button_debug_clock.setActionInside( () -> {
            delegate.debugger_nextClock(topLine);
        } );
        
    }
    
    private void init_button(UIButton button, double x, String image) {
        
        button.setFill(150, 220, 170);
        
        button.setMainLabelFont( new Font( "Courier New" , 20) );
        button.setMainLabelFontColor(Color.rgb(40, 20, 30));
        button.setMainLabelStyle("-fx-font-weight: bold;");
        
        button.setTranslateX(x);
        button.setTranslateY(padding);
        
        button.setMainLabelTranslationX(height - padding);
        
        button.setImage( new Image(
            "file:TDT4100-prosjekt-frederee/src/main/java/Project/Images/Editor/" + image,
            height - 2 * padding, height - 2 * padding, true, true
        ) );
        
        addChild(button);
        
    }
    
}
