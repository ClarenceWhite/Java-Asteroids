package asteroids.main;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;



public class Element extends Polygon {
    //The Point2D class defines a point representing a location in (x,y) coordinate space.
    //This class is only the abstract superclass for all objects that store a 2D coordinate.
    // The actual storage representation of the coordinates is left to the subclass.
    private Point2D movement;
    private Polygon polygon;
    private int lives;

    //constructor for the class 'asteroids.main.Element'
    public Element(Polygon polygon, int x, int y) {
        this.polygon = polygon;
        this.polygon.setTranslateX(x);
        this.polygon.setTranslateY(y);

        this.movement = new Point2D(0,0);
        this.lives = 1;
    }

    public Polygon getElement() {
        return polygon;
    }
    //move method
    public void move() {
        double new_x = this.polygon.getTranslateX() + this.movement.getX();
        double new_y = this.polygon.getTranslateY() + this.movement.getY();
        if(new_x < 0) { //if new_x less than 0
            new_x += Window.WIDTH; // move from left side of the window to right
        } else if (new_x > Window.WIDTH) { // if new_x greater than the width of window
            new_x = new_x % Window.WIDTH; //move from right side of the window to left
        }
        if(new_y < 0) { //if new_y less than 0
            new_y += Window.HEIGHT; //disappear from top and reappear at the bottom part
        } else if (new_y > Window.HEIGHT) { // if new_y greater than the height of window
            new_y = new_y % Window.HEIGHT; // disappear from bottom and reappear at the top part
        }
        //set new position
        this.polygon.setTranslateX(new_x);
        this.polygon.setTranslateY(new_y);
    }

    // Apply a given force with horizontal and vertical components.
    public void applyForce(double x, double y) {
        this.movement = this.movement.add(x * 0.3, y * 0.3);
        // System.out.println(this.movement);
    }

    // Apply a thrust on the facing direction
    public void accelerate() {
        double changeX = Math.cos(Math.toRadians(this.polygon.getRotate()));
        double changeY = Math.sin(Math.toRadians(this.polygon.getRotate()));

        changeX *= 0.01;
        changeY *= 0.01;

        this.movement = this.movement.add(changeX, changeY);
    }

    // Rotate the object with given angle
    public void rotate(double degree) throws IllegalArgumentException{
        // Negative degree means counter-clockwise
        if(Math.abs(degree) > 360) {
            throw new IllegalArgumentException();
        }
        this.polygon.setRotate(this.polygon.getRotate() + degree);
    }

    //check collide function using intersect method
    public boolean collide(Element other) {
        Shape collisionArea = Shape.intersect(this.polygon, other.getElement());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

    public int getLives() {return this.lives;}

    public void lossLive() {
        this.lives--;
    }

    public void setLives(int live) {
        this.lives = live;
    }
}
