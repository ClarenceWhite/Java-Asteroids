import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;


public class Element extends Polygon {

    private Point2D movement;
    private Polygon polygon;

    public Element(Polygon polygon, int x, int y) {
        this.polygon = polygon;
        this.polygon.setTranslateX(x);
        this.polygon.setTranslateY(y);

        this.movement = new Point2D(0,0);
    }

    public Polygon getElement() {
        return polygon;
    }

    public void move() {
        this.polygon.setTranslateX((this.polygon.getTranslateX() + this.movement.getX()) % Window.WIDTH);
        this.polygon.setTranslateY((this.polygon.getTranslateY() + this.movement.getY()) % Window.HEIGHT);
    }

    // Apply a given force with horizontal and vertical components.
    public void applyForce(double x, double y) {
        this.movement = this.movement.add(x * 0.3, y * 0.3);
    }

    // Apply a thrust on the facing direction
    public void accelerate() {
        double changeX = Math.cos(Math.toRadians(this.polygon.getRotate()));
        double changeY = Math.sin(Math.toRadians(this.polygon.getRotate()));

        changeX *= 0.05;
        changeY *= 0.05;

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

    public boolean collide(Element other) {
        Shape collisionArea = Shape.intersect(this.polygon, other.getElement());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }
}
