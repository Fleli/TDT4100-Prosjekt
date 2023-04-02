package Project.IDE;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class Line extends Pane {
    
    /*
     * *** *** *** LINE STATICS *** *** ***
    */
    
    public static double height = 20.0;
    
    /*
     * *** *** *** LINE FIELDS *** *** ***
    */
    
    // linjene lagrer hverandre som en dobbelt-lenket liste
    private Line above;
    private Line below;
    
    private int lineNumber;
    
    private Rectangle background;
    private Label textLabel;
    private Label lineLabel;
    
    private boolean cursorVisible = true;
    
    private StringBuilder text = new StringBuilder();
    private int cursorIndex = 0;
    
    private Rectangle cursorNode;
    
    private int indentation = 0;
    
    private Label indentDebugLabel = new Label();
    
    /*
     * *** *** *** CONSTRUCTOR *** *** ***
     */
    
    public Line( Paint color ) {
        
        background = new Rectangle(800, height);
        background.setFill(color);
        
        Font font = new Font("Courier New", 12);
        
        textLabel = new Label();
        textLabel.setFont( font );
        textLabel.setTranslateX(20);
        textLabel.setTextFill( Color.rgb(255, 255, 255) );
        
        lineLabel = new Label();
        lineLabel.setFont( font );
        lineLabel.setText("0");
        lineLabel.setTextFill( Color.rgb(255, 255, 255) );
        
        cursorNode = new Rectangle(2, 18);
        cursorNode.setFill( Color.rgb(190, 190, 190) );
        
        indentDebugLabel.setTranslateX(300);
        indentDebugLabel.setTextFill( Color.rgb(255, 255, 255) );
        
        getChildren().addAll( background , textLabel , lineLabel , cursorNode , indentDebugLabel );
        
        refreshUI();
        
    }
    
    /*
     * *** *** *** PRIVATE HELPERS *** *** ***
     */
    
    private void refreshUI() {
        
        textLabel.setText( text.toString() );
        
        cursorNode.setTranslateX ( 20 + cursorIndex * 12.0 * 0.6 );
        
    }
    
    private int netLeftBraces() {
        
        int sum = 0;
        
        for ( Character c : text.toString().toCharArray() ) {
            if ( c == '{' )         sum++;
            else if ( c == '}' )    sum--;
        }
        
        return sum;
        
    }
    
    private int beginningRightBraces() {
        
        int count = 0;
        
        int index = 0;
        
        while ( index < text.length()  &&  text.charAt(index) == ' ' ) {
            index++;
        }
        
        if ( index >= text.length() ) return 0;
        
        while ( index < text.length()  &&  text.charAt(index) == '}' ) {
            count++;
            index++;
        }
        
        return count;
        
    }
    
    /*
     * *** *** *** PUBLIC METHODS *** *** ***
    */
    
    public void write ( String s ) {
        
        text.insert(cursorIndex, s);
        cursorIndex += s.length();
        
        if ( s.equals("}") ) {
            updateIndentation(indentation);
            indentCorrectly();
        }
        
        refreshUI();
        
    }
    
    /**
     * Oppdater linjenummeret for denne linjen. Funksjonen sørger for oppdatering av linjenummer-label, samt rekursivt kall for å oppdatere linjenumre nedenfor.
     * @param lineNo Linjenummeret for denne linjen
     */
    public void setLineNumber ( int lineNo ) {
        
        lineNumber = lineNo;
        lineLabel.setText(String.valueOf(lineNumber));
        
        if ( below == null ) return;
        
        below.setLineNumber ( lineNo + 1 );
        
    }
    
    /**
     * Setter inn ny linje nedenfor denne, og skyver eventuelle tidligere linjer nedenfor, under den nye linja.
     * @param newBelow Linjen som settes inn mellom denne, og den som (eventuelt) var nedenfor denne tidligere.
     */
    public void insertLineBelow( Line newBelow ) {
        
        if ( below == null ) {
            setBelow(newBelow);
            updateIndentation(indentation);
            recursivelyIndentCorrectly();
            return;
        }
        
        Line oldBelow = below;
        
        removeBelow();
        
        setBelow(newBelow);
        newBelow.setBelow(oldBelow);
        
        updateIndentation(indentation);
        recursivelyIndentCorrectly();
        
    }
    
    /**
     * Setter inn en linje nedenfor. Vil utløse {@code IllegalStateException}  dersom linja allerede har en linje nedenfor.
     * @param below Linja som skal plasseres nedenfor denne.
     */
    public void setBelow ( Line below ) {
        
        if ( this.below != null ) throw new IllegalStateException("Already has line below");
        
        if ( below.above != null ) throw new IllegalStateException("New below line already has above line");
        
        this.below = below;
        below.above = this;
        
        this.getChildren().add(below);
        
        below.setTranslateY(height);
        below.setLineNumber(lineNumber + 1);
        
    }
    
    public void removeBelow() {
        getChildren().remove(below);
        below.above = null;
        below = null;
    }
    
    public void activate() {
        // unimplemented
        cursorNode.setVisible(cursorVisible);
    }
    
    public void deactivate() {
        // unimplemented
        cursorNode.setVisible(false);
    }
    
    public void cursorBlink() {
        cursorVisible = !cursorVisible;
        cursorNode.setVisible(cursorVisible);
    }
    
    public int getCursorIndex() {
        return cursorIndex;
    }
    
    public int getLength() {
        return text.length();
    }
    
    public void moveCursorLeft ( int count ) {
        
        if ( cursorIndex - count < 0 ) throw new IllegalArgumentException("That would make the index negative");
        
        cursorIndex -= count;
        
        refreshUI();
        
    }
    
    public void moveCursorRight ( int count ) {
        
        if ( cursorIndex + count > text.length() ) throw new IllegalArgumentException("That would make the index too large");
        
        cursorIndex += count;
        
        refreshUI();
        
    }
    
    public void moveFarLeft() {
        cursorIndex = 0;
        refreshUI();
    }
    
    public void moveFarRight() {
        cursorIndex = text.length();
        refreshUI();
    }
    
    public void backspace ( int count ) {
        
        if ( cursorIndex - count < 0 ) throw new IllegalArgumentException("Cannot backspace that much.");
        
        text = text.replace(cursorIndex - count, cursorIndex, "");
        cursorIndex -= count;
        
        refreshUI();
        
    }
    
    public void forwardDelete ( int count ) {
        
        if ( cursorIndex + count > text.length() ) throw new IllegalArgumentException("Cannot forward-delete that much.");
        
        text = text.replace(cursorIndex, cursorIndex + count, "");
        
        refreshUI();
        
    }
    
    public Line getAbove() {
        return above;
    }
    
    public Line getBelow() {
        return below;
    }
    
    /**
     * Update the line's cursor index. If the new index is larger than what is allowed, the index will be the maximum allowed index (it will not throw)
     * @param newCursorIndex The new index of the line's cursor – will be adjusted to fit the line's text.
     */
    public void setCursorIndex ( int newCursorIndex ) {
        
        int min = 0;
        int max = text.length();
        
        cursorIndex = newCursorIndex;
        cursorIndex = Math.max(cursorIndex, min);
        cursorIndex = Math.min(cursorIndex, max);
        
        refreshUI();
        
    }
    
    public String getTextRightOfCursor() {
        
        return text.substring(cursorIndex);
        
    }
    
    public void updateIndentation ( int newIndentation ) {
        
        indentation = newIndentation;
        
        int next = Math.max(0, indentation + netLeftBraces());
        
        indentDebugLabel.setText("" + indentation);
        
        if ( below == null ) return;
        
        below.updateIndentation(next);
        
    }
    
    /**
     * Assuming indentation is already updated.
     */
    public void indentCorrectly() {
        
        while ( text.length() > 0   &&   text.charAt(0) == ' ' ) {
            text.replace(0, 1, "");
            cursorIndex--;
        }
        
        for ( int i = 0 ; i < 4 * (indentation - beginningRightBraces()) ; i++ ) {
            text.insert(0, " ");
            cursorIndex++;
        }
        
        setCursorIndex(cursorIndex);
        
        refreshUI();
        
    }
    
    public void recursivelyIndentCorrectly() {
        
        indentCorrectly();
        
        if ( below == null ) return;
        
        below.recursivelyIndentCorrectly();
        
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public String getText() {
        return text.toString();
    }
    
    /**
     * *** *** *** TO STRING *** *** ***
    */
    
    @Override
    public String toString() {
        return "Line " + lineNumber + " containing text [" + text + "]";
    }
    
}
