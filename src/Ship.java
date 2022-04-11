import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

public class Ship extends Element{
    Random r = new Random();
    public Ship(int x, int y) {
        super(new Polygon(-10, -10, 20, 0, -10, 10), x, y);
        super.setLives(3);
    }

    //use setter to implement hyper jump function
    public void setXY(double x, double y) {
        this.getElement().setTranslateX(x);
        this.getElement().setTranslateY(y);
    }

    //getter to get current location of user ship
    public ArrayList<Double> getXY () {
        ArrayList<Double> position = new ArrayList<>();
        position.add(this.getElement().getTranslateX());
        position.add(this.getElement().getTranslateY());
        return position;
    }

}
