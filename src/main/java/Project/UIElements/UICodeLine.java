package Project.UIElements;

import Project.Compiler.Compiler.Error;
import Project.Views.ViewIDE.ViewIDE;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class UICodeLine extends UITextField {
    
    public static final double codeLineSpacing = 6;
    
    private ViewIDE ide;
    
    private double width;
    
    private UICodeLine line_above;
    private UICodeLine line_below;
    
    private Label lineNumberLabel;
    
    private UICodeErrorNode errorNode;
    
    private double fontSize;
    
    private int lineNumber;
    
    // Red, green, blue when the line is inactive
    private int r, g, b;
    
    // Red, green, blue when the line is active
    private int ra, ga, ba;
    
    public UICodeLine(double fontSize, ViewIDE ide, double width ) {
        
        super ( new Point2D(0, fontSize + codeLineSpacing) , new UISize(width, fontSize + codeLineSpacing), "");
        
        this.ide = ide;
        this.width = width;
        this.fontSize = fontSize;
        
        lineNumberLabel = new Label();
        lineNumberLabel.setFont ( new Font("Courier New", fontSize) );
        lineNumberLabel.setTextFill(Color.WHITE);
        lineNumberLabel.setTranslateX(3);
        getChildren().add(lineNumberLabel);
        
        setMainLabelTranslationX(30);
        setMainLabelTranslationY(0);
        setMainLabelFont( new Font("Courier New", fontSize) );
        
        r = 25;
        g = 30;
        b = 50;
        
        ra = r + 20;
        ga = g + 20;
        ba = b + 20;
        
        errorNode = new UICodeErrorNode(width, fontSize, codeLineSpacing);
        addChild(errorNode);
        
        refreshUI();
        setCorrectFill();
        
    }
    
    @Override
    public void mouseDown(Point2D location) {
        
        super.mouseDown(location);
        
        if ( isActive() ) {
            ide.setActiveLine(this);
        }
        
        setCorrectFill();
        
    }
    
    @Override
    public void keyDown(KeyEvent keyEvent) {
        
        if ( !isActive() ) {
            return;
        }
        
        UINode.ignoreKeyDown = true;
        KeyCode code = keyEvent.getCode();
        
        if ( code == KeyCode.ENTER ) {
            
            didPressEnter();
            
        } else if ( code == KeyCode.LEFT  &&  isLeft()  &&  line_above != null ) {
            
            ide.setActiveLine(line_above);
            line_above.moveFarRight();
            
        } else if ( code == KeyCode.RIGHT  &&  isRight()  &&  line_below != null ) {
            
            ide.setActiveLine(line_below);
            line_below.moveFarLeft();
            
        } else if ( code == KeyCode.BACK_SPACE  &&  isLeft()  &&  line_above != null ) {
            
            removeThisLine();
            
        } else if ( code == KeyCode.UP ) {
            
            handle_keyCode_up();
            
        } else if ( code == KeyCode.DOWN ) {
            
            handle_keyCode_dn();
            
        }
        
        else {
            
            super.keyDown(keyEvent);
            
        }
        
        refreshUI();
        refreshSyntaxHighlighting();
        setCorrectFill();
        
    }
    
    public UICodeLine getLineBelow() {
        return line_below;
    }
    
    public UICodeLine getLineAbove() {
        return line_above;
    }
    
    public void insertLineBelow(UICodeLine newBelow) {
        
        if ( newBelow == null ) {
            throw new IllegalStateException("New below cannot be null");
        }
        
        String textRightOfCursor = removeAndReturnTextRightOfCursor();
        
        UICodeLine oldBelow = line_below;
        
        removeLineBelow();
        
        setLineBelow(newBelow);
        
        newBelow.requestCursorIndex(0);
        newBelow.writeText(textRightOfCursor);
        newBelow.requestCursorIndex(0);
        
        if ( oldBelow != null ) {
            newBelow.setLineBelow(oldBelow);
        }
        
        newBelow.setLineNumber(lineNumber + 1);
        
        ide.setActiveLine(newBelow);
        ide.increaseNumberOfLines(1);
        
    }
    
    public void removeThisLine() {
        
        /**
         * TODO: Ordne slik at denne fungerer riktig. Prøv å gå gjennom kronologisk for å se hva som egentlig skjer. For trøtt nå
         */
        
        if ( line_above == null ) {
            return;
        }
        
        String textAtRight = removeAndReturnTextRightOfCursor();
        
        ide.setActiveLine(line_above);
        
        line_above.moveFarRight();
        line_above.writeText(textAtRight);
        
        UICodeLine line_above_this = line_above;
        UICodeLine line_below_this = line_below;
        
        line_above_this.removeLineBelow();
        
        if ( line_below_this != null ) {
            removeLineBelow();
            line_above_this.moveFarRight();
            line_above_this.setLineBelow(line_below_this);
            line_below_this.setLineNumber(lineNumber);
        }
        
        ide.setActiveLine(line_above_this);
        ide.decreaseNumberOfLines(1);
        
    }
    
    public void removeLineBelow() {
        
        if ( line_below == null ) {
            return;
        }
        
        removeChild(line_below);
        line_below.line_above = null;
        line_below = null;
        
    }
    
    public void setLineBelow(UICodeLine newBelow) {
        
        if ( line_below != null ) {
            throw new IllegalStateException("Must remove below first");
        }
        
        line_below = newBelow;
        line_below.line_above = this;
        addChild(newBelow);
        
    }
    
    public void setLineNumber(int line) {
        
        this.lineNumber = line;
        
        if ( line_below != null ) {
            line_below.setLineNumber(line + 1);
        }
        
        lineNumberLabel.setText("" + lineNumber);
        
    }
    
    public void handle_keyCode_up() {
        
        if ( line_above == null ) {
            return;
        }
        
        line_above.requestCursorIndex(getCursorIndex());
        ide.setActiveLine(line_above);
        
    }
    
    public void handle_keyCode_dn() {
        
        if ( line_below == null ) {
            return;
        }
        
        line_below.requestCursorIndex(getCursorIndex());
        ide.setActiveLine(line_below);
        
    }
    
    @Override
    public void setMainLabelTranslationY(double translationY) {
        lineNumberLabel.setTranslateY(translationY);
        super.setMainLabelTranslationY(translationY);
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    @Override
    public void writeText(String text) {
        super.writeText(text);
        refreshSyntaxHighlighting();
    }
    
    private void refreshSyntaxHighlighting() {
        String lineContent = getText();
        UILabel attributedText = ide.syntaxHighlighted(lineContent);
        setAttributedText(attributedText);
    }
    
    public String recursivelyFetchSourceCode() {
        
        StringBuilder sourceCode = new StringBuilder(getText());
        
        if ( line_below != null ) {
            
            String below = line_below.recursivelyFetchSourceCode().toString();
            
            sourceCode.append("\n");
            sourceCode.append(below);
            
        }
        
        return sourceCode.toString();
        
    }
    
    public void pushDownError(Error error) {
        
        if ( error.getLine() != lineNumber  &&  line_below != null ) {
            
            line_below.pushDownError(error);
            
        } else if ( error.getLine() != lineNumber ) {
            
            throw new IllegalStateException("Error @ line " + error.getLine() + " but line " + lineNumber + "is last.");
            
        } else {
            
            errorNode.addError(error);
            
        }
        
    }
    
    public void clearErrors() {
        
        //setCorrectFill();
        
        if ( !errorNode.isEmpty() ) {
                
            if ( errorNode != null ) {
                removeChild(errorNode);
            }
                
            errorNode = new UICodeErrorNode(width, fontSize, codeLineSpacing);
            addChild(errorNode);
            
        }
        
        if ( line_below != null ) {
            line_below.clearErrors();
        }
        
    }
    
    public void didPressEnter() {
        UICodeLine newLine = new UICodeLine(fontSize, ide, width);
        insertLineBelow(newLine);
    }
    
    public void setCorrectFill() {
        
        int r, g, b;
        
        if ( isActive() ) {
            r = ra;
            g = ga;
            b = ba;
        } else {
            r = this.r;
            g = this.g;
            b = this.b;
        }
        
        setFill(r, g, b);
        
    }
    
    @Override
    public void activate() {
        super.activate();
        setCorrectFill();
    }
    
    @Override
    public void deactivate() {
        super.deactivate();
        setCorrectFill();
    }
    
}
