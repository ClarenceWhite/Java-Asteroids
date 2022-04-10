import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        Text health = new Text(WIDTH - 70, 20, "Lives: 3");

        pane.getChildren().add(health);
        health.setFill(Color.WHITE);

        pane.getChildren().add(text);
        text.setFill(Color.WHITE);

        pane.getChildren().add(textLevel);
        textLevel.setFill(Color.WHITE);


        Ship ship = new Ship(WIDTH / 2, HEIGHT / 2); //initialize the ship at the center of the window
        ArrayList<Asteroids> asteroids = new ArrayList<>(); //make an array list to store all asteroids
        ArrayList<Bullet> bullets = new ArrayList<>(); //an array list to store all bullets

        Random r = new Random(); //random
        Asteroids asteroid = new Asteroids(r.nextInt(WIDTH / 3), r.nextInt(HEIGHT), r.nextInt(3) + 1); //randomly create different sizes of asteroids
        asteroids.add(asteroid); //add asteroids
        pane.getChildren().add(asteroid.getElement()); //add the asteroid to the screen
        asteroid.getElement().setStroke(Color.WHITE); // set the stroke color of the asteroid to white

        pane.getChildren().add(ship.getElement()); // add a spaceship to the screen
        ship.getElement().setStroke(Color.WHITE); //set color of spaceship

        Scene scene = new Scene(pane); //add a scene to the pane
        scene.setFill(Color.BLACK); // set background color to black
        stage.setScene(scene); //set scene to stage
        stage.show(); //show stage

        HashMap<KeyCode, Boolean> pressedKeys = new HashMap<KeyCode, Boolean>(); //hashmap store KeyCode and Boolean pairs

        scene.setOnKeyPressed(event -> pressedKeys.put(event.getCode(), Boolean.TRUE)); //key pressed

        scene.setOnKeyReleased(event -> pressedKeys.put(event.getCode(), Boolean.FALSE)); //key released

        scene.setOnKeyTyped(event -> {
            if (event.getCharacter().equals("c")) {
                for (Asteroids asteroidC: asteroids) {
                    pane.getChildren().remove(asteroidC.getElement());
                    asteroidC.setLives(0);
                }
            }
        });

        //The class AnimationTimer allows to create a timer, that is called in each frame while it is active.
        //An extending class has to override the method handle(long) which will be called in every frame.
        // The methods start() and stop() allow to start and stop the timer.
        new AnimationTimer() {
            long lastPressProcessed = 0; // a timer for fire shooting speed to avoid shooting too fast

            @Override
            public void handle(long now) {
                if(ship.getLives() <= 0) {
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
                //fire function
                if (pressedKeys.getOrDefault(KeyCode.SPACE, false)) {
//                    System.out.println("before:"+lastPressProcessed);
//                    System.out.println("minus:"+(System.currentTimeMillis()-lastPressProcessed));
                    if (System.currentTimeMillis() - lastPressProcessed > 50) {
                        Bullet bullet = new Bullet((int) ship.getElement().getTranslateX(), (int) ship.getElement().getTranslateY()); //bullet used the position of ship
                        bullet.getElement().setRotate(ship.getElement().getRotate());//bullets used the direction of ship
                        bullets.add(bullet);// generate bullet
                        pane.getChildren().add(bullet.getElement()); // display bullet on the pane
                        bullet.getElement().setFill(Color.WHITE); // set color of bullet
                        bullet.applyForce( // call applyForce method to give speed to the bullet
                                Math.cos(Math.toRadians(ship.getElement().getRotate())) * 10,
                                Math.sin(Math.toRadians(ship.getElement().getRotate())) * 10);
                    }
                    lastPressProcessed = System.currentTimeMillis();
//                    System.out.println("updated:"+lastPressProcessed);
                }

                // make asteroids, bullets, and ship move around
                asteroids.forEach(Asteroids::move);
                bullets.forEach(Element::move);
                ship.move();
                //check if there is collision between asteroid and ship
                for (Asteroids asteroid: asteroids) {
                    if (ship.collide(asteroid) && asteroid.getLives() != 0) {
                        asteroid.setLives(0);
                        pane.getChildren().remove(asteroid.getElement());
                        ship.lossLive();
                        health.setText("Lives: " + ship.getLives());
                    }
                }

                //check if there is collision between bullets and asteroids
                for (Bullet bullet:bullets) {
                    //check if bullets have traveled for a certain distance and whether they'd be removed
                    if (bullet.shouldRemove() && bullet.getLives() != 0){
                        bullet.setLives(0); //remove bullet from the bullets list
                        pane.getChildren().remove(bullet.getElement()); // remove bullet from pane
                    }

                    if(bullet.getLives() != 0) {
                        //stream in Java can be defined as a sequence of elements from a source,
                        // the stream here represents the asteroids objects in the list 'asteroids'
                        List<Asteroids> collisions = asteroids.stream()
                                .filter(asteroid -> (asteroid.collide(bullet) && asteroid.getLives() != 0)).toList(); //collect those asteroids to list

                        if (collisions.size() != 0 && bullet.getLives() != 0) { //if the collision list is not empty
                            bullet.setLives(0); //remove the bullet that hit the asteroid
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
                            text.setText("Points: " + points.addAndGet(100));
                            collided.setLives(0); //remove collided asteroids from asteroids list
                            pane.getChildren().remove(collided.getElement());
                        });
                    }
                }

                // This condition need to be fixed
                if (pane.getChildren().size() < 5 ) {
                    level++;
                    for (int i = 0; i < level; i++) {
                        Random r = new Random(); //random
                        Asteroids asteroid = new Asteroids(r.nextInt(WIDTH / 3), r.nextInt(HEIGHT), r.nextInt(3) + 1); //randomly create different sizes of asteroids
                        asteroids.add(asteroid); //add asteroids
                        pane.getChildren().add(asteroid.getElement());
                        asteroid.getElement().setStroke(Color.WHITE);
                    }
                    textLevel.setText("Level : " + level);
                }
            }
        }.start();
    }

    // main method to launch the window
    public static void main(String[] args) {
        launch(args);
    }

}



