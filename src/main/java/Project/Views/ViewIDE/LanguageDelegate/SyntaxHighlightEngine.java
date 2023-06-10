package Project.Views.ViewIDE.LanguageDelegate;

import Project.UIElements.UICodeLine;
import Project.UIElements.UILabel;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.util.List;

import Project.Compiler.Compiler.Compiler;
import Project.Compiler.InstructionGeneration.DebugRegion;
import Project.Compiler.Lexer.Token;
import Project.Compiler.Compiler.Error;

public class SyntaxHighlightEngine {
    
    public static final void syntaxHighlight(UICodeLine line, DebugRegion passedInDebugRegion) {
        
        String text = line.getText();
        double fontSize = line.getFontSize();
        
        Compiler compiler = new Compiler();
        
        UILabel syntaxHighlightedLabel = new UILabel(fontSize);
        
        List<Token> syntaxHighlightableTokens = compiler.getSyntaxHighlightableTokens(text);
        
        int netLeftBraces = 0;
        
        Integer debugStart = null;
        int debugEnd = 0;
        
        line.removeHighlight();
        
        if ( passedInDebugRegion != null  &&  passedInDebugRegion.start_line == line.getLineNumber()) {
            debugStart = passedInDebugRegion.start_col;
        }
        
        for ( Token token : syntaxHighlightableTokens ) {
            
            String content = token.content();
            String type = token.type();
            int col = token.startColumn();
            
            boolean isIssue = false;
            boolean isWarning = false;
            
            Integer errorLineLength = null;
            
            for (Error error : line.getErrors()) {
                
                int lineNumber = error.getLine();
                Integer column = error.getColumn();
                DebugRegion debugRegion = error.getDebugRegion();
                
                if (lineNumber == line.getLineNumber()  &&  column != null  &&  column == token.startColumn()) {
                    
                    if (error.getSeverity().equals("issue")) {
                        isIssue = true;
                    } else {
                        isWarning = true;
                    }
                    
                    if (debugRegion != null) {
                        errorLineLength = debugRegion.end_col - debugRegion.start_col - 1;
                    } else {
                        errorLineLength = token.content().length();
                    }
                    
                }
                
            }
            
            if ( token.typeIs("{") ) {
                netLeftBraces += 1;
            } else if ( token.typeIs("}") ) {
                netLeftBraces -= 1;
            }
            
            String special = "";
            SVGPath squiggly = null;
            
            if (debugStart != null) {
                
                if (passedInDebugRegion.start_col <= token.startColumn()  && 
                    passedInDebugRegion.end_col >= token.startColumn() + token.content().length() - 1)
                {
                    debugEnd = token.startColumn() + token.content().length() - 1;
                }
                    
            }
            
            if (isIssue || isWarning) {
                
                double width = errorLineLength * fontSize * 0.6;
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
            
            boolean isDebug = false;
            
            if (passedInDebugRegion != null) {
                isDebug = passedInDebugRegion.columnContains(token)  &&  line.getLineNumber() == passedInDebugRegion.start_line;
            }
            
            switch (type) {
                
                case "identifier": {
                    if (isDebug) {
                        color = Color.rgb(50, 90, 100);
                    } else {
                        color = Color.rgb(120, 240, 240);
                    }
                    break;
                } case "intLiteral": {
                    if (isDebug) {
                        color = Color.rgb(130, 130, 20);
                    } else {
                        color = Color.rgb(235, 235, 80);
                    }
                    break;
                } case "operator": {
                    if (isDebug) {
                        color = Color.rgb(90, 90, 90);
                    } else {
                        color = Color.rgb(220, 220, 220);
                    }
                    break;
                } case "control": {
                    if (isDebug) {
                        color = Color.rgb(100, 100, 100);
                    } else {
                        color = Color.rgb(235, 235, 235);
                    }
                    break;
                } case "comment": {
                    color = Color.rgb(30, 160, 40);
                    special += "-fx-font-style: italic;";
                    break;
                } case "keyword": {
                    special += "-fx-font-weight: bold;";
                    if (Compiler.isDataTypes(content) && isDebug) {
                        color = Color.rgb(30, 150, 75);
                    } else if (Compiler.isDataTypes(content)) {
                        color = Color.rgb(90, 220, 130);
                    } else if (isDebug) {
                        color = Color.rgb(230, 100, 100);
                    } else {
                        color = Color.rgb(230, 100, 100);
                    }
                    // Kan bruke for feilmeldinger:  -fx-border-color: red; -fx-border-width: 0 0 1 0;
                    break;
                } case "stringLiteral": {
                    color = Color.rgb(230, 160, 60);
                    break;
                } case "error": {
                    color = Color.rgb(240, 80, 80);
                    special += "-fx-border-color: red; -fx-border-width: 0 0 1 0;";
                    break;
                } default: {
                    if (isDebug) {
                        color = Color.rgb(120, 120, 130);
                    } else {
                        color = Color.rgb(240, 240, 240);
                    }
                    break;
                }
                
            }
               
            syntaxHighlightedLabel.addAttributedText(content, color, col, special, squiggly);
            
        }
        
        if (debugStart != null) {
            line.highlight(debugStart, debugEnd, Color.YELLOW);
        }
         
        line.setNetLeftBraces(netLeftBraces);
        
        line.setAttributedText(syntaxHighlightedLabel);
        
    }
    
}
