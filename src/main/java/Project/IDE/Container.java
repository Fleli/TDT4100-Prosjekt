package Project.IDE;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Container extends Pane {
    
    static String acceptedInputs = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ[](){}=/,.-:;@*+$!\"? ";
    static Paint preferredLineColor = Color.rgb(64, 64, 64);
    
    private List<Line> lines = new ArrayList<Line>();
    
    private Line active;
    
    private Timer blinkTimer;
    
    public Container() {
        
        Line l1 = new Line( preferredLineColor );
        l1.setLineNumber(1);
        lines.add(l1);
        getChildren().add(l1);
        setActive(l1);
        
        blinkTimer = new Timer();
        
        TimerTask task = new TimerTask() {
            public void run() {
                if ( active != null ) active.cursorBlink();
            }
        };
        
        blinkTimer.scheduleAtFixedRate ( task , 300 , 300 );
        
    }
    
    
    private void setActive( Line newActive ) {
        
        if ( active != null ) {
            active.deactivate();
        }
        
        active = newActive;
        active.activate();
        
    }
    
    private void deleteLine ( Line line ) {
        
        lines.remove(line);
        
        Line above = line.getAbove();
        Line below = line.getBelow();
        
        if ( above != null  &&  below != null ) {
            above.setBelow(below);
        } else if ( above != null ) {
            above.removeBelow();
        }
        
    }
    
    
    public void keyAction ( KeyEvent key ) {
        
        if ( active == null ) return;
            
        String text = key.getText();
        
        System.out.println(key.getCode());
        
        if ( acceptedInputs.indexOf(text) != -1   &&   text.length() == 1 ) {
            active.write(text);
            return;
        }
        
        switch ( key.getCode() ) {
            
            case ENTER: {
                
                Line newLine = new Line( preferredLineColor );
                
                String rightText = active.getTextRightOfCursor();
                
                active.forwardDelete(rightText.length());
                
                active.insertLineBelow(newLine);
                lines.add(active.getLineNumber(), newLine);
                
                setActive(newLine);
                
                active.write(rightText);
                
                active.moveCursorLeft(rightText.length());
                
                break;
                
            } case LEFT: {
                
                if ( active.getCursorIndex() == 0 ) return; // skal egentlig gå opp til forrige linje her
                
                if ( key.isShortcutDown() )     active.moveFarLeft();
                else                            active.moveCursorLeft(1);
                
                break;
                
            } case RIGHT: {
                
                if ( active.getCursorIndex() == active.getLength() ) return; // skal egentlig gå ned til neste linje her
                
                if ( key.isShortcutDown() )     active.moveFarRight();
                else                            active.moveCursorRight(1);
                
                break;
                
            } case BACK_SPACE: {
                
                if ( active.getCursorIndex() == 0 ) {
                    
                    System.out.println(active.getAbove());
                    
                    if ( active.getAbove() == null ) return;
                    
                    Line old = active;
                    Line above = old.getAbove();
                    
                    String oldContent = old.getText();
                    
                    setActive(above);
                    deleteLine(old);
                    
                    active.moveFarRight();
                    active.write(oldContent);
                    active.moveCursorLeft(oldContent.length());
                    
                    return;
                    
                }
                
                active.backspace(1);
                
                break;
                
            } case UP: {
                
                if ( active.getAbove() == null ) return;
                
                int cursorIndex = active.getCursorIndex();
                
                setActive(active.getAbove());
                active.setCursorIndex(cursorIndex);
                
                break;
                
            } case DOWN: {
                
                if ( active.getBelow() == null ) return;
                
                int cursorIndex = active.getCursorIndex();
                
                setActive(active.getBelow());
                active.setCursorIndex(cursorIndex);
                
                break;
                
            } case TAB: {
                
                active.write(" ");
                
                while ( active.getCursorIndex() % 4 != 0 ) active.write(" ");
                
                break;
                
            } case DELETE: {
                
                if ( active.getCursorIndex() >= active.getLength() ) return;
                
                active.forwardDelete(1);
                
                break;
                
            } default: {
                
                break;
                
            }
            
            
        }
        
        
    }
    
    public void didClick ( Point2D point ) {
        
        System.out.println("Passed down point: " + point.getX() + " " + point.getY());
        
        int x = (int) Math.floor( (point.getX() - 20.0) / (12.0 * 0.6) );
        int y = (int) Math.ceil( point.getY() / Line.height ) - 1;
        
        if ( y < 0  ||  y >= lines.size() ) {
            System.out.println("y was " + y + " which is too much");
            return;
        }
        
        setActive(lines.get(y));
        active.setCursorIndex(x);
        
    }
    
    
}

