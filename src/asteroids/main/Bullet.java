package asteroids.main;

import javafx.scene.shape.Polygon;

public class Bullet extends Element{
    double initX;
    double initY;
    double initTime;
    //constructor for bullet
    public Bullet(int x, int y) {
        super(new Polygon(2,-2, 2,2, -2,2, -2,-2), x, y);
        this.initX=x; //save the init x value for bullet
        this.initY=y; // save the init y value for the bullet
        this.initTime = System.currentTimeMillis();
    }
    //to get the current x of bullet
    public double getX() {
        return this.getElement().getTranslateX();
    }
    //to get the current y of bullet
    public double getY() {
        return this.getElement().getTranslateY();
    }
    public double getTravelTime() {
        return System.currentTimeMillis()-this.initTime;
    }
    //calculate the distance that the bullet traveled
    public boolean shouldRemove() {
        if(this.getTravelTime() > 4000){ //if the bullet has traveled for 5 seconds
            return true; //return true, means it should be removed
        }else{
            return false;
        }
    }

}
