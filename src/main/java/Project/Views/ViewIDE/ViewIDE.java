package Project.Views.ViewIDE;

import java.util.List;

import Project.Program;
import Project.Compiler.Compiler.Compiler;
import Project.Compiler.Compiler.Error;
import Project.Compiler.Lexer.Token;
import Project.FileSystem.File;
import Project.UIElements.UIButton;
import Project.UIElements.UICodeLine;
import Project.UIElements.UILabel;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import Project.Views.UIView;
import Project.VirtualMachine.Runtime;
import Project.VirtualMachine.VMException;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class ViewIDE extends UIView {
    
    public static final double topLineHeight = 100;
    public static final double consoleHeight = 200;
    
    private static double preferred_fontSize = 15;
    
    private static double codeLineTranslateX = 0;
    private static double codeLineWidth = 1000;
    
    private static double scrollSensitity = 0.5;
    
    private Compiler compiler;
    
    private File file;
    
    private UICodeLine topLine;
    private UICodeLine activeLine;
    
    private int numberOfLines = 1;
    
    private Rectangle upperBand;
    private Rectangle codeBackground;
    
    private Text text;
    
    private UIButton runButton;
    
    private ViewIDEConsole console;
    
    public ViewIDE(UISize size, File file) {
        
        // TODO: Rens denne konstruktøren, fordi den er ikke pen å lese gjennom
        
        super(size);
        
        this.file = file;
        
        double codeAreaHeight = Program.viewSize.height - topLineHeight - consoleHeight;
        codeBackground = new Rectangle(codeLineWidth, codeAreaHeight);
        codeBackground.setFill(Color.rgb(25, 30, 50, 1)); // TODO: Link this color with codeline colors
        codeBackground.setTranslateY(topLineHeight);
        getChildren().add(codeBackground);
        
        topLine = new UICodeLine(preferred_fontSize, this, codeLineWidth);
        topLine.setLineNumber(1);
        topLine.setTranslateX(codeLineTranslateX);
        topLine.setTranslateY(topLineHeight);
        setActiveLine(topLine);
        addChild(topLine);
        
        String fileName = file.getFileName();
        String fileExtension = file.getExtension();
        
        compiler = new Compiler();
        
        upperBand = new Rectangle(Program.viewSize.width, topLineHeight);
        upperBand.setFill(Color.grayRgb(200));
        upperBand.toFront();
        getChildren().add(upperBand);
        
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
            compileAndRun();
        });
        runButton.setMainLabelFontColor(Color.grayRgb(200));
        runButton.setImage( new Image(
            "file:TDT4100-prosjekt-frederee/src/main/java/Project/Images/compileAndRun.png",
            60, 60, true, true
        ) );
        addChild(runButton);
        
        double tY = Program.viewSize.height - consoleHeight;
        
        console = new ViewIDEConsole(codeLineWidth);
        console.setTranslateY(tY);
        addChild(console);
        
    }
    
    public File getFile() {
        return file;
    }
    
    public void setActiveLine(UICodeLine newActive) {
        
        if ( activeLine != null ) {
            activeLine.deactivate();
        }
        
        activeLine = newActive;
        activeLine.activate();
        
    }
    
    public UILabel syntaxHighlighted(String text) {
        
        UILabel syntaxHighlightedLabel = new UILabel(preferred_fontSize);
        
        List<Token> syntaxHighlightableTokens = compiler.getSyntaxHighlightableTokens(text);
        
        for ( Token token : syntaxHighlightableTokens ) {
            
            String content = token.content();
            String type = token.type();
            int col = token.startColumn();
            
            String special = null;
            
            Color color;
            
            switch (type) {
                
                case "identifier": {
                    color = Color.rgb(160, 200, 240);
                    break;
                } case "intLiteral": {
                    color = Color.rgb(235, 235, 80);
                    break;
                } case "operator": {
                    color = Color.rgb(220, 220, 220);
                    break;
                } case "control": {
                    color = Color.rgb(235, 235, 235);
                    break;
                } case "comment": {
                    color = Color.rgb(30, 160, 40);
                    special = "-fx-font-style: italic;";
                    break;
                } case "keyword": {
                    color = Color.rgb(230, 100, 100);
                    special = "-fx-font-weight: bold;";
                    // Kan bruke for feilmeldinger:  -fx-border-color: red; -fx-border-width: 0 0 1 0;
                    break;
                } case "stringLiteral": {
                    color = Color.rgb(230, 160, 60);
                    break;
                } case "error": {
                    color = Color.rgb(240, 80, 80);
                    special = "-fx-border-color: red; -fx-border-width: 0 0 1 0;";
                    break;
                } default: {
                    color = Color.rgb(240, 240, 240);
                    break;
                }
                
            }
            
            syntaxHighlightedLabel.addAttributedText(content, color, col, special);
            
        }
        
        return syntaxHighlightedLabel;
        
    }
    
    @Override
    public void didScroll(double x, double y, double dx, double dy) {
        
        boolean isInsideX = x > codeLineTranslateX  &&  x < codeLineTranslateX + codeLineWidth;
        boolean isInsideY = y > topLineHeight  &&  y < Program.viewSize.height - consoleHeight;
        
        if ( isInsideX  &&  isInsideY ) {
            
            UINode.ignoreDidScroll = true;
            
            double newTY = topLine.getTranslateY() + dy * scrollSensitity;
            topLine.setTranslateY(newTY);
            
            placeCodeLinesCorrectly();
            
        } else {
            
            super.didScroll(x, y, dx, dy);
            
        }
        
    }
    
    @Override
    public void keyDown(KeyEvent keyEvent) {
        
        /**
         * TODO: Bruk regionStart og regionEnd (line/col for begge)
         */
        
        if ( activeLine != null  &&  keyEvent.getCode() == KeyCode.V  &&  keyEvent.isMetaDown() ) {
            
            Clipboard cb = Clipboard.getSystemClipboard();
            String string = cb.getString();
            
            for ( Character c : string.toCharArray() ) {
                
                if ( c == '\n' ) {
                    
                    activeLine.didPressEnter();
                    
                } else {
                    
                    activeLine.writeText(new String("" + c));
                    
                }
                
            }
            
            UINode.ignoreKeyDown = true;
            
        } else {
            
            super.keyDown(keyEvent);
            
        }
        
    }
    
    @Override
    public void afterKeyDown() {
        
        long start = System.currentTimeMillis();
        
        compileAndShowErrorMessages();
        
        long end = System.currentTimeMillis();
        
        System.out.println("Compile-time: " + (end - start) + " millis.");
        
        super.afterKeyDown();
    }
    
    public void compileAndRun() {
        
        String sourceCode = topLine.recursivelyFetchSourceCode();
        
        compiler = new Compiler();
        compiler.compile(sourceCode, true);
        
        Runtime runtime = new Runtime( compiler.getExecutable() , 256, 256 , console);
        
        try {
            
            runtime.run();
            
        } catch (VMException exception) {
            
            // TODO: Handle VMException
            
        }
        
        
        runtime.printStack();
        runtime.printHeap();
    
    }
    
    private void compileAndShowErrorMessages() {
        
        String sourceCode = topLine.recursivelyFetchSourceCode();
        
        long start_fetch = System.currentTimeMillis();
        
        topLine.clearErrors();
        
        long end_fetch = System.currentTimeMillis();
        
        System.out.println("\nFetching and cleaning took: " + (end_fetch - start_fetch) + " millis.");
        
        try {
            
            compiler = new Compiler();
            compiler.compile(sourceCode, false);
            
        } catch (Exception e) {
            
            throw e;
            
        }
        
        for ( Error error : compiler.getErrors() ) {
            topLine.pushDownError(error);
        }
        
    }
    
    public void increaseNumberOfLines(int increase) {
        numberOfLines += increase;
    }
    
    public void decreaseNumberOfLines(int decrease) {
        numberOfLines -= decrease;
    }
    
    public void placeCodeLinesCorrectly() {
        
        double newTY = topLine.getTranslateY();
        
        newTY = Math.min(newTY, topLineHeight);
        newTY = Math.max(newTY, (1 - numberOfLines) * ( preferred_fontSize + UICodeLine.codeLineSpacing) + topLineHeight);
        
        topLine.setTranslateY(newTY);
        
    }
    
}
