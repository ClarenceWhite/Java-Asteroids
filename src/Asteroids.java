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

    //constructor for the asteroids class
    public Asteroids(int x, int y, int flag) {
        super(new RandomPolygon().createPolygon(flag), x , y); //from super class Element create a new polygon

        Random r = new Random(); //random
        super.getElement().setRotate(r.nextInt(360)); //set a rotation angle for new asteroids

        if(flag == 1) { //if it is a small asteroid
            super.applyForce(r.nextInt(-9, 9), r.nextInt(-9,9)); //set the speed
        } else if(flag == 2) { //if it is a middle size asteroid
            super.applyForce(r.nextInt(-6,6), r.nextInt(-6, 6)); //set the speed
        } else { //if it is a big size asteroid
            super.applyForce(r.nextInt(-3, 3), r.nextInt(-3, 3)); //set the speed
        }

        this.flag = flag;
        this.angularMomentum = 0.5 - r.nextDouble();
    }
    // a getter to get flag(the size) of the asteroid
    public int getFlag(){
        return this.flag;
    }

    @Override
    public void move() { // make the asteroid moving and rotating at the same time
        super.move();
        super.getElement().setRotate(super.getElement().getRotate() + angularMomentum);
    }
}
