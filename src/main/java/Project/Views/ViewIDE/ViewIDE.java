package Project.Views.ViewIDE;

import java.util.List;

import Project.Console;
import Project.Program;
import Project.Compiler.Compiler.Compiler;
import Project.Compiler.Compiler.Error;
import Project.Compiler.Lexer.Token;
import Project.FileSystem.File;
import Project.UIElements.UICodeLine;
import Project.UIElements.UILabel;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import Project.Views.UIView;
import Project.VirtualMachine.Runtime;
import javafx.scene.paint.Color;

public class ViewIDE extends UIView {
    
    private static double preferred_fontSize = 16;
    
    private static double codeLineTranslateX = 0;
    private static double codeLineWidth = 1000;
    
    private static double scrollSensitity = 0.5;
    
    private Compiler compiler;
    
    private File file;
    
    private UICodeLine topLine;
    private UICodeLine activeLine;
    
    private int numberOfLines = 1;
    
    private ViewIDEMenuElements menuElements;
    
    public ViewIDE(UISize size, File file) {
        
        super(size);
        
        this.file = file;
        
        topLine = new UICodeLine(preferred_fontSize, this, codeLineWidth);
        topLine.setLineNumber(1);
        topLine.setTranslateX(codeLineTranslateX);
        topLine.setTranslateY(ViewIDEMenuElements.topLineHeight);
        setActiveLine(topLine);
        addChild(topLine);
        
        String fileName = file.getFileName();
        String fileExtension = file.getExtension();
        menuElements = new ViewIDEMenuElements(fileName, fileExtension, this, codeLineWidth);
        addChild(menuElements);
        
        compiler = new Compiler();
        
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
        
        System.out.println(activeLine.getLineNumber());
        
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
        boolean isInsideY = y > ViewIDEMenuElements.topLineHeight  &&  y < Program.viewSize.height - ViewIDEMenuElements.consoleHeight;
        
        if ( isInsideX  &&  isInsideY ) {
            
            UINode.ignoreDidScroll = true;
            
            double newTY = topLine.getTranslateY() + dy * scrollSensitity;
            
            newTY = Math.min(newTY, ViewIDEMenuElements.topLineHeight);
            
            double minimumTY = (1 - numberOfLines) * ( preferred_fontSize + UICodeLine.codeLineSpacing) + ViewIDEMenuElements.topLineHeight;
            
            newTY = Math.max(newTY, minimumTY);
            
            topLine.setTranslateY(newTY);
            
        } else {
            
            super.didScroll(x, y, dx, dy);
            
        }
        
    }
    
    @Override
    public void afterKeyDown() {
        compileAndShowErrorMessages();
        super.afterKeyDown();
    }
    
    public void compileAndRun() {
        
        String sourceCode = topLine.recursivelyFetchSourceCode();
        
        System.out.println("Will compile and run with source code:\n" + sourceCode);
        
        try {
            
            compiler = new Compiler();
            compiler.compile(sourceCode, false);
            
            System.out.println("Successful: " + compiler.getExecutable());
            
            Console runtimeConsole = menuElements.getConsole();
            
            Runtime runtime = new Runtime( compiler.getExecutable() , 256, 256 , runtimeConsole);
            runtime.run();
            
            runtime.printStack();
            runtime.printHeap();
        
        } catch (Exception e) {
            
            System.out.println("Compile-time exception:");
            System.out.println(e);
            
        }
        
    }
    
    private void compileAndShowErrorMessages() {
        
        String sourceCode = topLine.recursivelyFetchSourceCode();
        
        topLine.clearErrors();
        
        System.out.println("Will compile (and show error messages) with source code:\n" + sourceCode);
        
        try {
                
            compiler = new Compiler();
            compiler.compile(sourceCode, false);
            
        } catch (Exception e) {
            
            System.out.println("\n\n---Try block failed.---\n\n\n");
            throw e;
            
        }
        
        System.out.println("Errors:");
        for ( Error error : compiler.getErrors() ) {
            System.out.println(error);
            topLine.pushDownError(error);
        }
        
    }
    
    public void increaseNumberOfLines(int increase) {
        numberOfLines += increase;
    }
    
    public void decreaseNumberOfLines(int decrease) {
        numberOfLines -= decrease;
    }
    
}
