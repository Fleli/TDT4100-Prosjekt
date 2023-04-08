package Project.Views.ViewIDE;

import Project.Program;
import Project.Compiler.Compiler.Compiler;
import Project.Documents.Document;
import Project.FileInterface.FileInterface;
import Project.UIElements.UICodeLine;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import Project.Views.UIView;
import Project.Views.ViewIDE.LanguageDelegates.Delegate_f;
import Project.Views.ViewIDE.LanguageDelegates.LanguageDelegate;
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
    
    private LanguageDelegate delegate;
    
    // TODO: Se mer på denne
    private boolean autosave = true;
    
    public ViewIDE(UISize size, Document document) {
        
        // TODO: Rens denne konstruktøren, fordi den er ikke pen å lese gjennom
        
        super(size);
        
        this.document = document;
        
        delegate = new Delegate_f();
        
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
    
    public void refreshSyntaxHighlighting(UICodeLine line) {
        delegate.syntaxHighlight(line);
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
        
        delegate.reactOnTextWritten(topLine);
        super.afterKeyDown();
        
        if (autosave) save(topLine.recursivelyFetchSourceCode());
        
    }
    
    public void compileAndRun() {
        
        console.clear();
        
        String sourceCode = topLine.recursivelyFetchSourceCode();
        
        compiler = new Compiler();
        compiler.compile(sourceCode, true);
        
        Runtime runtime = new Runtime( compiler.getExecutable() , 512, 512 , console);
        
        try {
            
            Runtime.printDebugInfo = false;
            runtime.run();
            
        } catch (VMException exception) {
            
            System.out.println("Runtime exception: " + exception.getLocalizedMessage());
            
        }
        
        runtime.printStack();
        runtime.printHeap();
    
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
        
        delegate.reactOnTextWritten(topLine);
        
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
