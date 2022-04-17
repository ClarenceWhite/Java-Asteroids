package asteroids.main;

import java.util.Random;
import javafx.scene.shape.Polygon;

public class RandomPolygon {
    //We can create pentagons in different shapes using this method, each pentagon represents an asteroid
    //The formula to calculate out different shapes of pentagons is: https://mathworld.wolfram.com/Pentagon.html
    public Polygon createPolygon(int flag) {
        Random r = new Random();
        double size = (flag * 3 + r.nextDouble(2)) * 6; //set the size of pentagon/asteroid
        //set variables according to the formula
        Polygon polygon = new Polygon();
        double c1 = Math.cos(Math.PI * 2 / 5);
        double c2 = Math.cos(Math.PI / 5);
        double s1 = Math.sin(Math.PI * 2 / 5);
        double s2 = Math.sin(Math.PI * 4 / 5);

        //draw the polygon
        polygon.getPoints().addAll(
                size, 0.0,
                size * c1, -1 * size * s1,
                -1 * size * c2, -1 * size * s2,
                -1 * size * c2, size * s2,
                size * c1, size * s1);

        for (int i = 0; i < polygon.getPoints().size(); i++) {
            int change = r.nextInt(5) - 2;
            polygon.getPoints().set(i, polygon.getPoints().get(i) + change);
        }
        return polygon;
    }
}
