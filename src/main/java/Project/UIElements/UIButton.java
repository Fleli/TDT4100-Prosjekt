package Project.UIElements;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class UIButton extends UINode {
    
    private Label mainLabel;
    private Label subLabel;
    
    private Rectangle rectangle;
    
    private UIAction action_inside;
    private UIAction action_outside;
    
    private ImageView imgView;
    
    @Override
    public void mouseDown(Point2D location) {
        
        if ( rectangle.contains(location) ) {
            if ( action_inside != null ) {
                action_inside.execute();
            }
        } else if ( action_outside != null ) {
            action_outside.execute();
        }
        
    }
    
    public UIButton ( Point2D position , UISize size , String text ) {
        
        super();
        
        setWidth(size.width);
        setHeight(size.height);
        
        setTranslateX(position.getX());
        setTranslateY(position.getY());
        
        rectangle = new Rectangle(size.width, size.height);
        getChildren().add(rectangle);
        
        mainLabel = new Label();
        mainLabel.setText(text);
        mainLabel.setFont( new Font("Courier New", 20) );
        mainLabel.setTranslateX(5);
        mainLabel.setTranslateY(5);
        getChildren().add(mainLabel);
        
    }
    
    public void setMainLabelStyle(String style) {
        mainLabel.setStyle(style);
    }
    
    public void setFill ( int red , int green , int blue ) {
        rectangle.setFill( Color.rgb(red, green, blue) );
    }
    
    public void setFill ( int red , int green , int blue , double opacity ) {
        rectangle.setFill( Color.rgb(red, green, blue, opacity) );
    } 
    
    public void setTransparent() {
        rectangle.setFill(Color.TRANSPARENT);
    }
    
    public void setText ( String text ) {
        mainLabel.setText(text);
    }
    
    public void setTextAlignment ( Pos value ) {
        // TODO: Sjekk at denne faktisk virker, det har vært endel problemer med den
        mainLabel.setAlignment(value);
    }
    
    public void setSize ( UISize size ) {
        
        if ( rectangle != null ) {
            getChildren().remove(rectangle);
            rectangle = null;
        }
        
        rectangle = new Rectangle(size.width, size.height);
        getChildren().add(rectangle);
        
    }
    
    public void setBackgroundStyle(String style) {
        rectangle.setStyle(style);
    }
    
    public void setActionInside ( UIAction action_inside ) {
        this.action_inside = action_inside;
    }
    
    public void setActionOutside ( UIAction action_outside ) {
        this.action_outside = action_outside;
    }
    
    public void setSubLabelText ( String text ) {
        
        if ( subLabel != null ) {
            getChildren().remove(subLabel);
        }
        
        subLabel = new Label();
        subLabel.setFont( new Font("Courier New", 14) );
        subLabel.setTranslateX(5);
        subLabel.setTranslateY(30);
        subLabel.setMaxWidth( rectangle.getWidth() - 10 );
        subLabel.setText(text);
        subLabel.setWrapText(true);
        getChildren().add(subLabel);
        
    }
    
    public void setMainLabelFontColor ( Paint newFontColor ) {
        mainLabel.setTextFill(newFontColor);
    }
    
    public void setMainLabelFont ( Font newFont ) {
        mainLabel.setFont(newFont);
    }
    
    public double getMainLabelTranslateX() {
        return mainLabel.getTranslateX();
    }
    
    public double getMainLabelTranslateY() {
        return mainLabel.getTranslateY();
    }
    
    public void setImage(Image img) {
        
        if ( img == null  ||  img.isError() ) {
            throw new IllegalArgumentException("Image is " + img);
        }
        
        if ( img.getWidth() > getWidth()  ||  img.getHeight() > getHeight() ) {
            throw new IllegalArgumentException("Image is too big");
        }
        
        if ( imgView != null ) {
            getChildren().remove(imgView);
        }
        
        imgView = new ImageView(img);
        imgView.setStyle("-fx-background-color: blue;");
        getChildren().add(imgView);
        
    }
    
    public void setMainLabelTranslationX ( double translationX ) {
        mainLabel.setTranslateX(translationX);
    }
    
    public void setMainLabelTranslationY ( double translationY ) {
        mainLabel.setTranslateY(translationY);
    }
    
    public void setMainLabelVisible(boolean visible) {
        mainLabel.setVisible(visible);
    }
    
}
