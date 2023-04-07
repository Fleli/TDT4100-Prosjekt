package Project.UIElements;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

public class UINode extends Pane {
    
    public static boolean ignoreMouseDown = false;
    public static boolean ignoreKeyDown = false;
    public static boolean ignoreDidScroll = false;
    
    /**
     * Implemented by all {@code UINode} classes, and is final.
     * Will pass its mouseDown event down the responder chain, and
     * adjust for subnodes' {@code translation} properties. Will call
     * {@code mouseDown(location)} (which is allowed to be overridden),
     * so that the nodes that need to handle mouseDown events, can do so.
     * @param location The location, not adjusted for this node's
     * coordinate system, where the mouseDown event occured.
     */
    public final void MOUSE_CLICKED(Point2D location) {
        
        double adjustedX = location.getX() - getTranslateX();
        double adjustedY = location.getY() - getTranslateY();
        Point2D adjustedLocation = new Point2D(adjustedX, adjustedY);
        
        mouseDown(adjustedLocation);
        
        List<Node> children = new ArrayList<Node>(getChildren());
        
        for ( Node child : children ) {
            if ( ignoreMouseDown ) {
                break;
            }
            if ( child instanceof UINode ) {
                UINode uiNode = (UINode) child;
                uiNode.MOUSE_CLICKED(adjustedLocation);
            }
        }
        
        afterMouseDown();
        
    }
    
    /**
     * Implemented by all {@code UINode} classes, and is final.
     * Will pass its keyPressed event down the responder chain. Will call
     * {@code keyDown(keyEvent)} (which is allowed to be overridden),
     * so that the nodes that need to handle keyDown events, can do so.
     * @param keyEvent The key event, including characters, modifier keys
     * and so on.
     */
    public final void KEY_PRESSED(KeyEvent keyEvent) {
        
        keyDown(keyEvent);
        
        List<Node> children = new ArrayList<Node>(getChildren());
        
        for ( Node child : children ) {
            if ( ignoreKeyDown ) {
                break;
            }
            if ( child instanceof UINode ) {
                UINode uiNode = (UINode) child;
                uiNode.KEY_PRESSED(keyEvent);
            }
        }
        
        afterKeyDown();
        
    }
    
    public final void DID_SCROLL(double x, double y, double dx, double dy) {
        
        didScroll(x, y, dx, dy);
        
        List<Node> children = new ArrayList<Node>(getChildren());
        
        for ( Node child : children ) {
            if ( ignoreDidScroll ) {
                return;
            }
            if ( child instanceof UINode ) {
                UINode uiNode = (UINode) child;
                uiNode.DID_SCROLL(x, y, dx, dy);
            }
        }
        
    }
    
    /**
     * Method for handling mouseDown actions in {@code UINode} objects.
     * This is overridable by subclasses of {@code UINode}, and is by
     * default an empty function.
     * @param location The location the mouse event happened at, adjusted
     * to be local for the receiver.
     */
    public void mouseDown(Point2D location) {}
    
    /**
     * Will run after MOUSE_CLICKED has been called on all children.
     */
    public void afterMouseDown() {}
    
    /**
     * Method for handling keyDown actions in {@code UINode} objects.
     * This is overridable by subclasses of {@code UINode}, and is by
     * default an empty function.
     * @param keyEvent The key event, including characters, modifier keys
     * and so on.
     */
    public void keyDown(KeyEvent keyEvent) {}
    
    /**
     * Will run after KEY_PRESSED has been called on all children.
     */
    public void afterKeyDown() {}
    
    /**
     * Method for handing scroll events.
     * @param x The x location of the cursor when the scroll occured.
     * @param y The y location of the cursor when the scroll occured.
     * @param dx The horizontal scroll.
     * @param dy The vertical scroll.
     */
    public void didScroll(double x, double y, double dx, double dy) {}
    
    public UINode() {
        super();
    }
    
    public void addChild( UINode child ) {
        getChildren().add(child);
    }
    
    public void removeChild ( UINode child ) {
        getChildren().remove(child);
    }
    
}
