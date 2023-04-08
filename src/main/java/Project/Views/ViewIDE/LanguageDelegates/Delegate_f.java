package Project.Views.ViewIDE.LanguageDelegates;

import java.util.List;

import Project.Compiler.Compiler.Compiler;
import Project.Compiler.Compiler.Error;
import Project.Compiler.Lexer.Token;
import Project.UIElements.UICodeLine;
import Project.UIElements.UILabel;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class Delegate_f implements LanguageDelegate {
    
    private Compiler compiler;
    
    
    public Delegate_f() {
        
        // constructor
        
    }
    
    
    @Override
    public void run(String sourceCode) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }
    
    public void syntaxHighlight(UICodeLine line) {
        
        String text = line.getText();
        double fontSize = line.getFontSize();
        
        compiler = new Compiler();
        
        UILabel syntaxHighlightedLabel = new UILabel(fontSize);
        
        List<Token> syntaxHighlightableTokens = compiler.getSyntaxHighlightableTokens(text);
        
        int netLeftBraces = 0;
        
        for ( Token token : syntaxHighlightableTokens ) {
            
            String content = token.content();
            String type = token.type();
            int col = token.startColumn();
            
            boolean isIssue = false;
            boolean isWarning = false;
                
            for (Error error : line.getErrors()) {
                
                int lineNumber = error.getLine();
                Integer column = error.getColumn();
                
                if (lineNumber == line.getLineNumber()  &&  column != null  &&  column == token.startColumn()) {
                    
                    if (error.getSeverity().equals("issue")) {
                        System.out.println("Found issue: " + error);
                        isIssue = true;
                    } else {
                        System.out.println("Found warning: " + error);
                        isWarning = true;
                    }
                    
                }
                
            }
            
            if ( token.typeIs("{") ) {
                netLeftBraces += 1;
            } else if ( token.typeIs("}") ) {
                netLeftBraces -= 1;
            }
            
            String special = null;
            SVGPath squiggly = null;
            
            if (isIssue || isWarning) {
                
                double width = content.length() * fontSize * 0.6;
                StringBuilder svgContent = new StringBuilder("M");
                
                for ( int x = 0 ; x < width ; x += 2 ) {
                    
                    int y = (x % 4 == 0) ? 0 : 2;
                    
                    svgContent.append(" " + x + " " + y + ",");
                    
                }
                
                svgContent.deleteCharAt(svgContent.length() - 1);
                
                squiggly = new SVGPath();
                squiggly.setContent(svgContent.toString());
                squiggly.setStrokeWidth(1);
                
                
                squiggly.setTranslateY(fontSize + 1);
                
            }
            
            if (isIssue) {
                squiggly.setStroke(Color.RED);
            } else if (isWarning) {
                squiggly.setStroke(Color.YELLOW);
            }
            
            Color color;
            
            switch (type) {
                
                case "identifier": {
                    color = Color.rgb(120, 240, 240);
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
                    if (Compiler.isDataTypes(content)) color = Color.rgb(90, 220, 130);
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
            
            syntaxHighlightedLabel.addAttributedText(content, color, col, special, squiggly);
            
        }
        
        line.setNetLeftBraces(netLeftBraces);
        
        line.setAttributedText(syntaxHighlightedLabel);
        
    }
    
    public void reactOnTextWritten(UICodeLine topLine) {
        
        compiler = new Compiler();
        
        String sourceCode = topLine.recursivelyFetchSourceCode();
        
        compiler.compile(sourceCode, false);
        
        topLine.clearErrors();
        
        for ( Error error : compiler.getErrors() ) {
            topLine.pushDownError(error);
        }
        
        topLine.finishedErrors();
        
    }
    
}
