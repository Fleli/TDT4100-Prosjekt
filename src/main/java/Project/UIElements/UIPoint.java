package Project.UIElements;

public class UIPoint {
    
    public double x;
    public double y;
    
    public UIPoint ( double x, double y ) {
        this.x = x;
        this.y = y;
    }
    
    public static final UIPoint zero = new UIPoint (0, 0);
    
    public static UIPoint add ( UIPoint p1 , UIPoint p2 ) {
        return new UIPoint(p1.x + p2.x, p1.y + p2.y);
    }
    
    public static UIPoint subtract ( UIPoint p1 , UIPoint p2 ) {
        return new UIPoint(p1.x - p2.x, p1.y - p2.y);
    }
    
}
