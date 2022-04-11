import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.*;

public class Window extends Application{
    //override class method 'start' to create a new window for the game
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    private static int level = 1;

    @Override
    public void start(Stage stage){

        Pane pane = new Pane(); //Create new pane
        pane.setPrefSize(WIDTH, HEIGHT); //set pane size

        AtomicInteger points = new AtomicInteger();

        Text text = new Text(10, 20, "Points: 0");
        Text textLevel = new Text(10, 50, "Level: 1");
        Text health = new Text(WIDTH - 90, 20, "Lives: 3");
        Text alert = new Text(WIDTH/2-80,50, "");

        pane.getChildren().add(health);
        health.setFill(Color.WHITE);
        health.setFont(new Font(24));

        pane.getChildren().add(text);
        text.setFill(Color.WHITE);
        text.setFont(new Font(24));

        pane.getChildren().add(textLevel);
        textLevel.setFill(Color.WHITE);
        textLevel.setFont(new Font(24));

        pane.getChildren().add(alert);
        alert.setFill(Color.GREEN);
        alert.setFont(new Font(32));

        ArrayList<Asteroids> asteroids = new ArrayList<>(); //make an array list to store all asteroids
        ArrayList<Bullet> bullets = new ArrayList<>(); //an array list to store all bullets
        ArrayList<Bullet> alienBullets = new ArrayList<>(); //an array list to store only alien bullets
        ArrayList<Alien> aliens = new ArrayList<>(); //an array list for alien
        ArrayList<ArrayList> positions = new ArrayList<>(); // an array list to store the real-time position of all asteroids and alien

        Random r = new Random(); //random
        Asteroids asteroid = new Asteroids(r.nextInt(WIDTH / 3), r.nextInt(HEIGHT), r.nextInt(3) + 1); //randomly create different sizes of asteroids
        asteroids.add(asteroid); //add asteroids
        pane.getChildren().add(asteroid.getElement()); //add the asteroid to the screen
        asteroid.getElement().setStroke(Color.WHITE); // set the stroke color of the asteroid to white

        Ship ship = new Ship(WIDTH / 2, HEIGHT / 2); //initialize the ship at the center of the window
        pane.getChildren().add(ship.getElement()); // add a spaceship to the screen
        ship.getElement().setStroke(Color.WHITE); //set color of spaceship

        Alien alien = new Alien(r.nextInt(50,100), r.nextInt(50,HEIGHT-50)); //initialize an alien ship
        alien.setLives(0); //initialize alien live to 0

        Scene scene = new Scene(pane); //add a scene to the pane
        scene.setFill(Color.BLACK); // set background color to black
        stage.setScene(scene); //set scene to stage
        stage.show(); //show stage

        HashMap<KeyCode, Boolean> pressedKeys = new HashMap<KeyCode, Boolean>(); //hashmap store KeyCode and Boolean pairs
        scene.setOnKeyPressed(event -> pressedKeys.put(event.getCode(), Boolean.TRUE)); //key pressed
        scene.setOnKeyReleased(event -> pressedKeys.put(event.getCode(), Boolean.FALSE)); //key released


        //The class AnimationTimer allows to create a timer, that is called in each frame while it is active.
        //An extending class has to override the method handle(long) which will be called in every frame.
        // The methods start() and stop() allow to start and stop the timer.
        new AnimationTimer() {
            long lastPressSpace = 0; // a timer for fire shooting speed to avoid shooting too fast
            long lastPressH = 0; //a timer for hyperspace to avoid high reading input frequency
            double startTime = System.currentTimeMillis(); //set game start time
            int alienFlag = 0; //a flag for alien ship appearing
            double lastAlienBullet = 0; //a timer for fire shooting speed of alien



            @Override
            public void handle(long now) {
                //if user ship runs out of live
                if(ship.getLives() <= 0) {
                    alert.setFill(Color.RED);
                    alert.setText("Game Over!!");
                    stop();
                }
                //left key to rotate angle by -5 degree
                if(pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.rotate( -5);
                }
                //right key to rotate angle by 5 degree
                if(pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.rotate(5);
                }
                //up key to accelerate
                if (pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }
                //H key to hyperspace jump
                if (pressedKeys.getOrDefault(KeyCode.H, false)) {
                    if (System.currentTimeMillis() - lastPressH > 1000) {
                        ArrayList<Double> shipsnewXY = shipNewXY();
                        if (!positions.contains(shipsnewXY)) { //check if a new random place has conflict with any asteroids or alien
                            pane.getChildren().remove(ship.getElement());  //remove the user ship from pane
                            ship.setXY(shipsnewXY.get(0), shipsnewXY.get(1));
                            pane.getChildren().add(ship.getElement());  //remove the user ship from pane
                        }
                        lastPressH = System.currentTimeMillis();
                    }
                }
                //space key to perform fire function for the user ship
                if (pressedKeys.getOrDefault(KeyCode.SPACE, false)) {
                    if (System.currentTimeMillis() - lastPressSpace > 50) {
                        Bullet bullet = new Bullet((int) ship.getElement().getTranslateX(), (int) ship.getElement().getTranslateY()); //bullet used the position of ship
                        bullet.getElement().setRotate(ship.getElement().getRotate());//bullets used the direction of ship
                        bullets.add(bullet);// generate bullet
                        pane.getChildren().add(bullet.getElement()); // display bullet on the pane
                        bullet.getElement().setFill(Color.ORANGERED); // set color of bullet
                        bullet.applyForce( // call applyForce method to give speed to the bullet
                                Math.cos(Math.toRadians(ship.getElement().getRotate())) * 10,
                                Math.sin(Math.toRadians(ship.getElement().getRotate())) * 10);
                    }
                    lastPressSpace = System.currentTimeMillis();
                }

                // make asteroids and ship move around
                asteroids.forEach(Asteroids::move);
                ship.move();
                //select a random time to generate alien ship, and make it move
                if (System.currentTimeMillis()-startTime > r.nextInt(10000, 100000) && alienFlag == 0) {
                    alien.setLives(1); //set live for alien ship
                    pane.getChildren().add(alien.getElement()); //add alien ship to pane
                    alien.getElement().setStroke(Color.WHITE);  //set color of alien ship
                    aliens.add(alien); //add alien to alien list
                    alienFlag++; // alienFlag increment
                    startTime = System.currentTimeMillis(); // refresh start time
                }
                if (alien.getLives() > 0) { //if the time is in random time bound, live of alien will be set to 1, and make it move
                    alien.move();
                    positions.clear(); //clean the positions list first
                    positions.add(alien.getXY()); //add current alien position to positions list
                }

                //let the alien ship shooting toward the user ship every 3 seconds
                if (alien.getLives() != 0 && System.currentTimeMillis()-lastAlienBullet > 3000 && alienFlag == 1) {
                    System.out.println("Alien fire!!!");
                    double userx = ship.getElement().getTranslateX();
                    double usery = ship.getElement().getTranslateY();
                    double alienx = alien.getElement().getTranslateX()+10;
                    double alieny = alien.getElement().getTranslateY()+10;
                    double distance = Math.sqrt((userx-alienx)*(userx-alienx) + (usery-alieny)*(usery-alieny));
                    double bulletRotate1 = Math.toDegrees(Math.asin(Math.abs((usery-alieny))/distance));
                    double bulletRotate2 = 180-Math.toDegrees(Math.asin(Math.abs((usery-alieny))/distance));
                    Bullet alienBullet = new Bullet((int) alienx, (int) alieny);
                    //set the direction of bullets from alien
                    if (userx > alienx) {
                        if (usery < alieny) {
                            alienBullet.getElement().setRotate(alien.getElement().getRotate()-bulletRotate1);
                        }
                        else {
                            alienBullet.getElement().setRotate(alien.getElement().getRotate()+bulletRotate1);
                        }
                    }
                    else {
                        if (usery < alieny) {
                            alienBullet.getElement().setRotate(alien.getElement().getRotate()-bulletRotate2);
                        }
                        else {
                            alienBullet.getElement().setRotate(alien.getElement().getRotate()+bulletRotate2);
                        }
                    }

                    bullets.add(alienBullet); //add alien bullet to general bullet list
                    alienBullets.add(alienBullet); // also add alien bullet to alien bullet list
                    pane.getChildren().add(alienBullet.getElement()); // add alien bullet to pane
                    alienBullet.getElement().setFill(Color.BLUE); // set color of alien bullet
                    alienBullet.applyForce(
                            Math.cos(Math.toRadians(alienBullet.getElement().getRotate())) * 10,
                            Math.sin(Math.toRadians(alienBullet.getElement().getRotate())) * 10);

                    lastAlienBullet = System.currentTimeMillis();

                }
                bullets.forEach(Element::move);  //make all bullets move

                //check the distance the alien ship covered, if it has traveled more than window width, remove it
                if (alien.travelDistance() > WIDTH-110 && alien.getLives() != 0) {
                    System.out.println("alien travel and disappear");
                    alien.setLives(0);
                    pane.getChildren().remove(alien.getElement());
                }

                //check if there is collision between user ship and alien bullets
                for (Bullet alienBullet: alienBullets) {
                    if (ship.collide(alienBullet) && alienBullet.getLives() != 0) {
                        pane.getChildren().remove(ship.getElement());  //remove the user ship from pane
                        System.out.println("User hits with alien bullet!");
                        alienBullet.setLives(0);
                        pane.getChildren().remove(alienBullet.getElement());
                        //place the user ship to another safe place
                        ArrayList<Double> shipsnewXY = shipNewXY(); //get a new place
                        if (!positions.contains(shipsnewXY)) { //check if a new random place has conflict with any asteroids or alien
                            try {
                                Thread.sleep(500);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ship.setXY(shipsnewXY.get(0), shipsnewXY.get(1));
                            pane.getChildren().add(ship.getElement());  //remove the user ship from pane
                        };
                        ship.setLives(ship.getLives()-1);
                        health.setText("Lives: " + ship.getLives());
                    }
                }

                //check if there is collision between asteroid and ship
                for (Asteroids asteroid: asteroids) {
                    positions.clear();
                    positions.add(asteroid.getXY()); //add current asteroids position to positions list
                    if (ship.collide(asteroid) && asteroid.getLives() != 0) {
                        pane.getChildren().remove(ship.getElement());  //remove the user ship from pane
                        System.out.println("User hits with asteroids!");
                        //place the user ship to another safe place
                        ArrayList<Double> shipsnewXY = shipNewXY(); //get a new place
                        if (!positions.contains(shipsnewXY)) { //check if a new random place has conflict with any asteroids or alien
                            try {
                                Thread.sleep(500);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ship.setXY(shipsnewXY.get(0), shipsnewXY.get(1));
                            pane.getChildren().add(ship.getElement());  //remove the user ship from pane
                        };
                        ship.setLives(ship.getLives()-1); //decrease live
                        health.setText("Lives: " + ship.getLives());
                    }

                    }


                //check if there is collision between all the bullets and asteroids
                for (Bullet bullet:bullets) {
                    //check if user's bullets collided with alien ship
                    if (bullet.collide(alien) && bullet.getLives() != 0 && alien.getLives() != 0) {
                        if (alienBullets.contains(bullet)) {  //if the bullet is from the alien itself, ignore it
                            continue;
                        }
                        //if the bullet is from the user, the user's bullet will break the alien ship
                        System.out.println("alien hit by user!");
                        bullet.setLives(0);
                        alien.setLives(0);
                        pane.getChildren().remove(alien.getElement());
                        pane.getChildren().remove(bullet.getElement());
                        text.setText("Points: " + points.addAndGet(200)); // add points 200 for shooting the alien
                    }
                    // check if bullets have traveled for a certain distance and whether they'd be removed
                    if (bullet.shouldRemove() && bullet.getLives() != 0){
                        bullet.setLives(0); //remove bullet's live
                        pane.getChildren().remove(bullet.getElement()); // remove bullet from pane
                    }

                    if(bullet.getLives() != 0) {
                        //stream in Java can be defined as a sequence of elements from a source,
                        // the stream here represents the asteroids objects in the list 'asteroids'
                        List<Asteroids> collisions = asteroids.stream()
                                .filter(asteroid -> (asteroid.collide(bullet) && asteroid.getLives() != 0)).toList(); //collect those asteroids to list

                        if (collisions.size() != 0 && bullet.getLives() != 0) { //if the collision list is not empty
                            bullet.setLives(0); //remove the bullet's live that hit the asteroid
                            pane.getChildren().remove(bullet.getElement()); //remove the bullet from the pane
                        }
                        //iterate through the collision list
                        collisions.forEach(collided -> { //for each asteroid which has collided with a bullet
                            if (collided.getFlag() == 3 || collided.getFlag() == 2) { //if it is big or middle asteroid
                                //create a left child
                                Asteroids left_child = new Asteroids((int) collided.getElement().getTranslateX(),
                                        (int) collided.getElement().getTranslateY(), collided.getFlag() - 1);
                                asteroids.add(left_child); //create a new asteroid which is one size smaller than the broken one
                                left_child.getElement().setStroke(Color.WHITE);
                                pane.getChildren().add(left_child.getElement());
                                //create a right child
                                Asteroids right_child = new Asteroids((int) collided.getElement().getTranslateX(),
                                        (int) collided.getElement().getTranslateY(), collided.getFlag() - 1); //create another new asteroid which is one size smaller than the broken one
                                asteroids.add(right_child);
                                right_child.getElement().setStroke(Color.WHITE);
                                pane.getChildren().add(right_child.getElement());
                            }
                            text.setText("Points: " + points.addAndGet(100)); // add points
                            collided.setLives(0); //remove collided asteroids' live
                            pane.getChildren().remove(collided.getElement()); //remove asteroids from pane
                        });
                    }
                }

                //Game Level
                if (checkAsteroids(asteroids) && (alien.getLives() == 0 || checkAlien(aliens)) && ship.getLives() > 0) {
                    //remove bullets from both general bullet list and alien bullets list, and the screen as well
                    for (Bullet bullet: bullets) {
                        pane.getChildren().remove(bullet.getElement());
                    }
                    for (Bullet bullet: alienBullets) {
                        pane.getChildren().remove(bullet.getElement());
                    }
                    bullets.clear();
                    alienBullets.clear();
                    //clear the asteroids list as well
                    asteroids.clear();
                    // if the alien appeared, remove it in the alien list
                    if (!checkAlien(aliens)) {
                        aliens.clear();
                    }
                    level++; //level increment
                    for (int i = 0; i < level; i++) {
                        Asteroids asteroid = new Asteroids(r.nextInt(WIDTH / 3), r.nextInt(HEIGHT), r.nextInt(3) + 1); //randomly create different sizes of asteroids
                        asteroids.add(asteroid); //add asteroids
                        pane.getChildren().add(asteroid.getElement());
                        asteroid.getElement().setStroke(Color.WHITE);
                    }
                    textLevel.setText("Level : " + level);
                    startTime = System.currentTimeMillis(); //reset game start time
                    System.out.println("before flag reset" + alienFlag);
                    alienFlag = 0; //reset alien flag
                    System.out.println("after flag reset" + alienFlag);
                }
            }
        }.start();
    }

    //method to check if all the asteroids have removed by the user
    public boolean checkAsteroids (ArrayList<Asteroids> asteroids) {
        for (Asteroids asteroid: asteroids) {
            if (asteroid.getLives() != 0) {
                return false;
            }
        }
        return true;
    }
    //method to check if there is alien in alien list
    public boolean checkAlien (ArrayList<Alien> alien) {
        if (alien.size() == 0) {
            return true;
        }
        return false;
    }
    //method to generate a new position
    public ArrayList<Double> shipNewXY () {
        Random r = new Random();
        ArrayList<Double> shipNewXY = new ArrayList<>();
        shipNewXY.add(r.nextDouble(WIDTH-20));
        shipNewXY.add(r.nextDouble(HEIGHT-20));
        return shipNewXY;
    }



    // main method to launch the window
    public static void main(String[] args) {
        launch(args);
    }

}



