package Project.UIElements;

import java.util.Timer;
import java.util.TimerTask;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class UITextField extends UIButton {
    
    private static final String allowed_chars = 
        "abcdefghijklmnopqrstuvwxyzæøåABCDEFGHIJKLMNOPQRSTUVWXYZÆØÅ0123456789()[]{}<>@\"\'+-*/%$#^&| :;_.,=!";
        
    private static final double cursorWidth = 1.5;
    
    private static final long blinkTime = 500;
    
    private double fontSize = 20;
    
    private Rectangle cursor = new Rectangle(cursorWidth, fontSize + 2, Color.grayRgb(200) );
    
    private boolean isActive = false;
    
    private StringBuilder stringBuilder = new StringBuilder();
    
    private int cursorIndex = 0;
    
    private String defaultText;
    
    private boolean showCursor = false;
    
    private Timer cursorBlinkTimer = new Timer();
    
    private UILabel attributedText;
    
    @Override
    public void mouseDown(Point2D location) {
        
        super.mouseDown(location);
        
        if ( isActive ) {
            int newCursorIndex = convertXToCursorIndex(location.getX());
            requestCursorIndex(newCursorIndex);
        }
        
    }
    
    @Override
    public void keyDown(KeyEvent keyEvent) {
        
        if ( !isActive ) {
            return;
        }
        
        String keyText = keyEvent.getText();
        
        if ( keyText.length() == 1  &&  allowed_chars.indexOf(keyText) != -1 ) {
            
            writeText(keyText);
            
        } else switch ( keyEvent.getCode() ) {
            
            case BACK_SPACE: {
                handleBackspace(keyEvent);
                break;
            } case DELETE: {
                handleDelete(keyEvent);
                break;
            } case LEFT: {
                handleKeyLeft(keyEvent);
                refreshUI();
                break;
            } case RIGHT: {
                handleKeyRight(keyEvent);
                refreshUI();
                break;
            } case TAB: {
                writeText(" ");
                while ( cursorIndex % 4 != 0 ) {
                    writeText(" ");
                }
                break;
            }
            
            default: break;
            
        }
        
        refreshUI();
        
    }
    
    
    public UITextField(Point2D position, UISize size, String defaultText) {
        
        super(position, size, defaultText);
        
        this.defaultText = defaultText;
        
        cursor.setTranslateY ( getMainLabelTranslateY() + 2 );
        getChildren().add(cursor);
        
        setMainLabelFont( new Font("Courier New", fontSize) );
        setMainLabelFontColor(Color.WHITE);
        
        cursorBlinkTimer.scheduleAtFixedRate( new TimerTask() {
            @Override
            public void run() {
                showCursor = !showCursor;
                cursor.setVisible ( showCursor && isActive );
            }
        }, blinkTime, blinkTime);
        
        setActionInside( () -> {
            activate();
        });
        
        setActionOutside( () -> {
            deactivate();
        });
        
        refreshUI();
        
    }
    
    public void activate() {
        isActive = true;
        refreshUI();
    }
    
    public void deactivate() {
        isActive = false;
        refreshUI();
    }
    
    public void refreshUI() {
        
        if ( stringBuilder.length() == 0 ) {
            
            // Vis default-tekst og gray ut
            setMainLabelFontColor( Color.rgb(125, 125, 125, 1) );
            setText(defaultText);
            
        } else {
            
            // Vis tekst, full farge
            setMainLabelFontColor( Color.rgb(225, 225, 225, 1) );
            setText(stringBuilder.toString());
            
        }
        
        cursor.setVisible ( showCursor && isActive );
        cursor.setTranslateX ( getMainLabelTranslateX() + cursorIndex * fontSize * 0.6 );
        
    }
    
    private void handleBackspace(KeyEvent event) {
        
        if ( event.isShortcutDown() ) {
            while ( cursorIndex > 0 ) {
                singleBackspace();
            }
        } else {
            singleBackspace();
        }
        
    }
    
    private void singleBackspace() {
        
        if ( cursorIndex > 0 ) {
            stringBuilder.deleteCharAt(cursorIndex - 1);
            cursorIndex -= 1;
        }
        
    }
    
    private void handleDelete(KeyEvent event) {
        
        if ( event.isShortcutDown() ) {
            while ( cursorIndex < stringBuilder.length() ) {
                singleDelete();
            }
        } else {
            singleDelete();
        }
        
    }
    
    private void singleDelete() {
        
        if ( cursorIndex < stringBuilder.length() ) {
            singleRight();
            singleBackspace();
        }
        
    }
    
    private void handleKeyLeft ( KeyEvent event ) {
        if ( event.isShortcutDown() ) {
            moveFarLeft();
        } else {                // event.isAltDown() for option
            singleLeft();
        }
    }
    
    private void singleLeft() {
        cursorIndex = Math.max(0, cursorIndex - 1);
        refreshUI();
    }
    
    private void handleKeyRight ( KeyEvent event ) {
        if ( event.isShortcutDown() ) {
            moveFarRight();
        } else {                // event.isAltDown() for option
            singleRight();
        }
    }
    
    private void singleRight() {
        cursorIndex = Math.min(stringBuilder.length(), cursorIndex + 1);
        refreshUI();
    }
    
    private void resizeCursor() {
        
        getChildren().remove(cursor);
        
        cursor = new Rectangle(2, fontSize + 2, Color.grayRgb(200));
        
        getChildren().add(cursor);
        
        cursor.setTranslateY ( getMainLabelTranslateY() + 2 );
        
    }
    
    @Override
    public void setMainLabelFont(Font newFont) {
        
        this.fontSize = newFont.getSize();
        
        resizeCursor();
        
        super.setMainLabelFont(newFont);
        
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public String removeAndReturnTextRightOfCursor() {
        
        String text = stringBuilder.substring(cursorIndex);
        
        int oldCursor = cursorIndex;
        
        moveFarRight();
        
        while ( cursorIndex > oldCursor ) {
            singleBackspace();
        }
        
        refreshUI();
        
        return text;
        
    }
    
    public void setTextAndCursorIndex(String text, int newCursorIndex) {
        
        if ( newCursorIndex < 0  ||  newCursorIndex > text.length() ) {
            throw new IllegalArgumentException("Invalid cursor index: " + cursorIndex);
        }
        
        stringBuilder = new StringBuilder(text);
        cursorIndex = newCursorIndex;
        
        refreshUI();
        
    }
    
    public boolean isLeft() {
        return ( cursorIndex == 0 );
    }
    
    public boolean isRight() {
        return ( cursorIndex == stringBuilder.length() );
    }
    
    public void moveFarLeft() {
        cursorIndex = 0;
        refreshUI();
    }
    
    public void moveFarRight() {
        cursorIndex = stringBuilder.length();
        refreshUI();
    }
    
    public void writeText(String text) {
        stringBuilder.insert(cursorIndex, text);
        cursorIndex += text.length();
        setAttributedText(null);
        refreshUI();
    }
    
    public void requestCursorIndex(int newCursorIndex) {
        
        if ( newCursorIndex < 0 ) {
            throw new IllegalStateException("New cursor index @ request cannot be " + newCursorIndex);
        }
        
        int max = stringBuilder.length();
        cursorIndex = Math.min(newCursorIndex, max);
        
    }
    
    public int getCursorIndex() {
        return cursorIndex;
    }
    
    public void setAttributedText(UILabel newAttributedText) {
        
        if ( attributedText != null ) {
            removeChild(attributedText);
        }
        
        if ( newAttributedText == null ) {
            setMainLabelVisible(true);
            return;
        }
        
        attributedText = newAttributedText;
        attributedText.setTranslateX(getMainLabelTranslateX());
        attributedText.setTranslateY(getMainLabelTranslateY());
        setMainLabelVisible(false);
        addChild(attributedText);
        
        refreshUI();
        
    }
    
    public String getText() {
        return stringBuilder.toString();
    }
    
    private int convertXToCursorIndex(double x) {
        
        double translationAdjusted = x - getMainLabelTranslateX();
        double index_nonInt = translationAdjusted / ( fontSize * 0.6 );
        
        double index = Math.round(index_nonInt);
        
        return (int) index;
        
    }
    
}
