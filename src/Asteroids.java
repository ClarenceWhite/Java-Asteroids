import java.util.Random;

public class Asteroids extends Element {

    private int flag;
    private double angularMomentum;

    // flag == 3 -> large
    // Size: flag * 3 + r.nextDouble(2)) * 3
    // Speed: within 3 in each direction
    //
    // flag == 2 -> medium
    // Size: flag * 3 + r.nextDouble(2)) * 3
    // Speed: from 3 to 6 in each direction
    //
    // flag == 1 -> small
    // Size: flag * 3 + r.nextDouble(2)) * 3
    // Speed: from 6 to 9 in each direction

    public Asteroids(int x, int y, int flag) {
        super(new RandomPolygon().createPolygon(flag), x , y);

        Random r = new Random();
        super.getElement().setRotate(r.nextInt(360));

        if(flag == 1) {
            super.applyForce(r.nextInt(-9, 9), r.nextInt(-9,9));
        } else if(flag == 2) {
            super.applyForce(r.nextInt(-6,6), r.nextInt(-6, 6));
        } else {
            super.applyForce(r.nextInt(-3, 3), r.nextInt(-3, 3));
        }

        this.angularMomentum = 0.5 - r.nextDouble();
    }

    @Override
    public void move() {
        super.move();
        super.getElement().setRotate(super.getElement().getRotate() + angularMomentum);
    }
}
