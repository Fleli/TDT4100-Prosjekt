package Project.Views.ViewIDE.LanguageDelegate;

import Project.Program;
import Project.UIElements.UIAction;
import Project.UIElements.UIButton;
import Project.UIElements.UICodeLine;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class UpperBand_f extends UINode {
    
    private static final double padding = 30;
    private static final double textSpace = 140;
    
    private Rectangle upperBand;
    
    private UIButton button_run;
    private UIButton button_compile;
    private UIButton button_debug;
    
    private UIButton button_debug_clock;
    
    private UIButton button_back;
    
    private double height;
    
    private UISize buttonSize;
    
    public UpperBand_f(Color bandColor, double height, Delegate_f delegate, UICodeLine topLine) {
        
        super();
        
        this.height = height;
        
        upperBand = new Rectangle(Program.viewSize.width, height);
        upperBand.setFill(bandColor);
        upperBand.toFront();
        getChildren().add(upperBand);
        
        Point2D zero = new Point2D(0, 0);
        buttonSize = new UISize(height - 2 * padding + textSpace, height - 2 * padding);
        
        button_run = new UIButton(zero, buttonSize, "Kjør");
        button_compile = new UIButton(zero, buttonSize, "Kompiler");
        button_debug = new UIButton(zero, buttonSize, "Debug");
        
        init_button(button_run      , 400       , "ferdig.png"  );
        init_button(button_compile  , 700       , "hammer.png"  );
        init_button(button_debug    , 1000      , "diagram.png" );
        
        button_run.setActionInside( () -> {
            delegate.clearDebugArea();
            delegate.requestDebugAreaView(0);
            delegate.run(topLine);
        } );
        
        button_compile.setActionInside( () -> {
            // Compile, og produser executable (og vis info om denne på et vis)
        } );
        
        button_debug.setActionInside( () -> {
            delegate.debug(topLine);
        } );
        
        button_debug_clock = new UIButton(
            new Point2D(50, buttonSize.height / 2 - buttonSize.height * 0.6 / 2), 
            new UISize(buttonSize.width * 0.6, buttonSize.height * 0.6), 
            "Debug Clock"
        );
        button_debug_clock.setMainLabelTranslationX(20);
        button_debug_clock.setMainLabelFont( new Font("Courier New", 14) );
        button_debug_clock.setMainLabelFontColor(Color.rgb(40, 20, 30));
        button_debug_clock.setMainLabelStyle("-fx-font-weight: bold;");
        button_debug_clock.setFill(150, 220, 170);
        button_debug_clock.setImage( new Image(
            "file:TDT4100-prosjekt-frederee/src/main/java/Project/Images/Editor/ferdig.png",
            buttonSize.height * 0.6, buttonSize.height * 0.6, true, true
        ) );
        button_debug.addChild(button_debug_clock);
        button_debug_clock.setTranslateX(buttonSize.width + 20);
        button_debug_clock.setVisible(true);
        
        button_debug_clock.setActionInside( () -> {
            delegate.debugger_nextClock(topLine);
        } );
        
        button_back = new UIButton(
            new Point2D(height / 2 - buttonSize.height / 2, 50), 
            buttonSize, 
            "Tilbake"
        );
        init_button(button_back, height, "ferdig.png");
        
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
            buttonSize.width / 2 - 2 * padding, buttonSize.height / 2 - 2 * padding, true, true
        ) );
        
        addChild(button);
        
    }
    
    public void setBackToMenuAction(UIAction action) {
        button_back.setActionInside(action);
    }
    
}
