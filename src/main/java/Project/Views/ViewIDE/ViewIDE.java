package Project.Views.ViewIDE;

import java.util.List;

import Project.Program;
import Project.Compiler.Compiler.Compiler;
import Project.Compiler.Compiler.Error;
import Project.Compiler.Lexer.Token;
import Project.Documents.Document;
import Project.FileInterface.FileInterface;
import Project.UIElements.UICodeLine;
import Project.UIElements.UILabel;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import Project.Views.UIView;
import Project.VirtualMachine.Runtime;
import Project.VirtualMachine.VMException;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ViewIDE extends UIView {
    
    public static final double topLineHeight = 100;
    public static final double codeAreaHeight = 700;
    
    private static double preferred_fontSize = 15;
    
    private static double codeLineTranslateX = 0;
    private static double codeLineWidth = 1000;
    
    private static double scrollSensitity = 0.5;
    
    private Compiler compiler;
    
    private Document document;
    
    private UICodeLine topLine;
    private UICodeLine activeLine;
    
    private int numberOfLines = 1;
    
    private IDETopBand upperBand;
    private Rectangle lowerBand;
    private Rectangle codeBackground;
    
    private ViewIDEConsole console;
    
    private boolean notLoading = true;
    
    public ViewIDE(UISize size, Document document) {
        
        // TODO: Rens denne konstruktøren, fordi den er ikke pen å lese gjennom
        
        super(size);
        
        this.document = document;
        
        codeBackground = new Rectangle(codeLineWidth, codeAreaHeight);
        codeBackground.setFill(Color.rgb(25, 30, 50, 1)); // TODO: Link this color with codeline colors
        codeBackground.setTranslateY(topLineHeight);
        getChildren().add(codeBackground);
        
        topLine = new UICodeLine(preferred_fontSize, this, codeLineWidth, 0);
        topLine.setLineNumber(1);
        topLine.setTranslateX(codeLineTranslateX);
        topLine.setTranslateY(topLineHeight);
        setActiveLine(topLine);
        addChild(topLine);
        
        double consoleWidth = Program.viewSize.width - codeLineWidth;
        console = new ViewIDEConsole(consoleWidth, codeAreaHeight);
        console.setTranslateX(codeLineWidth);
        console.setTranslateY(topLineHeight);
        addChild(console);
        
        compiler = new Compiler();
        
        Color bandColor = Color.rgb(130, 150, 170);
        
        upperBand = new IDETopBand(this, bandColor, topLineHeight);
        addChild(upperBand);
        
        lowerBand = new Rectangle(Program.viewSize.width, Program.viewSize.height - topLineHeight - codeAreaHeight);
        lowerBand.setTranslateY(topLineHeight + codeAreaHeight);
        lowerBand.setFill(bandColor);
        getChildren().add(lowerBand);
        
        notLoading = false;
        loadDocument();
        notLoading = true;
        
    }
    
    public Document getDocument() {
        return document;
    }
    
    public void setActiveLine(UICodeLine newActive) {
        
        if ( activeLine != null ) {
            activeLine.deactivate();
        }
        
        activeLine = newActive;
        activeLine.activate();
        
    }
    
    public UILabel syntaxHighlighted(UICodeLine codeLine, String text) {
        
        UILabel syntaxHighlightedLabel = new UILabel(preferred_fontSize);
        
        List<Token> syntaxHighlightableTokens = compiler.getSyntaxHighlightableTokens(text);
        
        int netLeftBraces = 0;
        
        for ( Token token : syntaxHighlightableTokens ) {
            
            String content = token.content();
            String type = token.type();
            int col = token.startColumn();
            
            if ( token.typeIs("{") ) {
                netLeftBraces += 1;
            } else if ( token.typeIs("}") ) {
                netLeftBraces -= 1;
            }
            
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
        
        codeLine.setNetLeftBraces(netLeftBraces);
        
        return syntaxHighlightedLabel;
        
    }
    
    @Override
    public void didScroll(double x, double y, double dx, double dy) {
        
        boolean isInsideX_codeArea = x > codeLineTranslateX  &&  x < codeLineTranslateX + codeLineWidth;
        boolean isInsideY_codeArea = y > topLineHeight  &&  y < topLineHeight + codeAreaHeight;
        
        boolean isInsideX_console = x > codeLineTranslateX + codeLineWidth;
        
        if ( isInsideX_codeArea  &&  isInsideY_codeArea ) {
            
            UINode.ignoreDidScroll = true;
            
            double newTY = topLine.getTranslateY() + dy * scrollSensitity;
            topLine.setTranslateY(newTY);
            
            placeCodeLinesCorrectly();
            
        } else if ( isInsideX_console  &&  isInsideY_codeArea ) {
            
            UINode.ignoreDidScroll = true;
            
            console.delegatedScroll(dx, dy);
            
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
        
        saveAndCompileAndShowErrorMessages();
        
        long end = System.currentTimeMillis();
        
        System.out.println("Saving, compilation and error messages took " + (end - start) + " ms.");
        
        
        super.afterKeyDown();
    }
    
    public void compileAndRun() {
        
        console.clear();
        
        String sourceCode = topLine.recursivelyFetchSourceCode();
        
        compiler = new Compiler();
        compiler.compile(sourceCode, true);
        
        Runtime runtime = new Runtime( compiler.getExecutable() , 4096, 4096 , console);
        
        try {
            
            Runtime.printDebugInfo = false;
            runtime.run();
            
        } catch (VMException exception) {
            
            System.out.println("Runtime exception: " + exception.getLocalizedMessage());
            
        }
        
        
        runtime.printStack();
        runtime.printHeap();
    
    }
    
    private void saveAndCompileAndShowErrorMessages() {
        
        String sourceCode = topLine.recursivelyFetchSourceCode();
        
        save(sourceCode);
        
        topLine.clearErrors();
        
        try {
            
            compiler = new Compiler();
            compiler.compile(sourceCode, false);
            
        } catch (Exception e) {
            
            throw e;
            
        }
        
        System.out.println("Error");
        for ( Error error : compiler.getErrors() ) {
            topLine.pushDownError(error);
            System.out.println(error);
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
    
    private void loadDocument() {
        
        String content = document.getContent();
        
        for ( char c : content.toCharArray() ) {
            
            if ( c == '\n' ) {
                
                activeLine.didPressEnter();
                
            } else {
                    
                activeLine.writeText("" + c);
            
            }
                
        }
        
        saveAndCompileAndShowErrorMessages();
        
    }
    
    public boolean notLoading() {
        return notLoading;
    }
    
    private void save(String sourceCode) {
        
        try {
                
            document.setContent(sourceCode);
            FileInterface.saveDocument(document);
        
        } catch (Exception e) {
            
            System.out.println(e.getLocalizedMessage());
            
        }
        
    }
    
}
