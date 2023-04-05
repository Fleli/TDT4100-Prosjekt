package Project.UIElements;

public class UISize {
    
    public double width;
    public double height;
    
    public UISize ( double width, double height ) {
        this.width = width;
        this.height = height;
    }
    
    public static final UISize zero = new UISize (0, 0);
    
    public static UISize add ( UISize p1 , UISize p2 ) {
        return new UISize(p1.width + p2.width, p1.height + p2.height);
    }
    
    public static UISize subtract ( UISize p1 , UISize p2 ) {
        return new UISize(p1.width - p2.width, p1.height - p2.height);
    }
    
}
