import javafx.scene.shape.Polygon;

import java.util.ArrayList;

public class Alien extends Element{
    double initX;
    double initY;
    //alien's constructor
    public Alien(int x, int y) {
        super(new Polygon(0,0, 75,0, 60,-15, 45,-15, 45,-22.5, 30,-22.5, 30,-15, 15,-15), x, y);
        super.setLives(1);
        super.applyForce(2, 0);
        this.initX = x;
        this.initY = y;
    }

    @Override
    public void move() {
        super.move();
    }

    //to get the current x of alien
    public double getX() {
        return this.getElement().getTranslateX();
    }

    //to get the current y of alien
    public double getY() {
        return this.getElement().getTranslateY();
    }

    //get position of alien
    public ArrayList<Double> getXY () {
        ArrayList<Double> position = new ArrayList<>();
        position.add(this.getElement().getTranslateX());
        position.add(this.getElement().getTranslateY());
        return position;
    }

    //calculate the distance that the alien traveled
    public double travelDistance() {
        double x1 = this.initX; //get the initial x
        double x2 = getX(); // get the initial y
        double distance = Math.abs(x1-x2); //calculate the distance that the alien traveled
        return distance;
    }

}
