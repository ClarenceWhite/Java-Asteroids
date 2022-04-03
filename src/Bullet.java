import javafx.scene.shape.Polygon;

public class Bullet extends Element{
    double initX;
    double initY;
    //constructor for bullet
    public Bullet(int x, int y) {
        super(new Polygon(2,-2, 2,2, -2,2, -2,-2), x, y);
        this.initX=x; //save the init x value for bullet
        this.initY=y; // save the init y value for the bullet
    }
    //to get the current x of bullet
    public double getX() {
//        System.out.println("the bullet's x now is:"+getElement().getTranslateX());
        return this.getElement().getTranslateX();
    }
    //to get the current y of bullet
    public double getY() {
//        System.out.println("the bullet's y now is:"+getElement().getTranslateY());
        return this.getElement().getTranslateY();
    }
    //calculate the distance that the bullet traveled
    public boolean shouldRemove() {
        double x1 = this.initX;
        double x2 = getX();
        double y1 = this.initY;
        double y2 = getY();
        double distance = Math.sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) ); //calculate the distance that the bullet traveled
//        System.out.println("distance traveled:"+distance);
        if(distance > 350){ //if the bullet has traveled for a distance of 10
            return true; //return true, means it should be removed
        }else{
            return false;
        }
    }

}
