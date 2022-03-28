import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Window extends Application{
    //override class method 'start' to create a new window for the game
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    @Override
    public void start(Stage stage) throws Exception {

        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);

        Ship ship = new Ship(WIDTH / 2, HEIGHT / 2);
        ArrayList<Asteroids> asteroids = new ArrayList<Asteroids>();
        ArrayList<Bullet> bullets = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Random r = new Random();
            Asteroids asteroid = new Asteroids(r.nextInt(WIDTH / 3), r.nextInt(HEIGHT), r.nextInt(2) + 1);
            asteroids.add(asteroid);
        }

        for (Asteroids asteroid:asteroids) {
            pane.getChildren().add(asteroid.getElement());
            asteroid.getElement().setStroke(Color.WHITE);
        }
        pane.getChildren().add(ship.getElement());
        ship.getElement().setStroke(Color.WHITE);

        Scene scene = new Scene(pane);
        scene.setFill(Color.BLACK);
        stage.setScene(scene);

        HashMap<KeyCode, Boolean> pressedKeys = new HashMap<KeyCode, Boolean>();

        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });

        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
        });

        stage.show();

        new AnimationTimer() {

            @Override
            public void handle(long now) {
                if(pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.rotate( -5);
                }

                if(pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.rotate(5);
                }

                if (pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }

                if (pressedKeys.getOrDefault(KeyCode.SPACE, false)) {
                    Bullet bullet = new Bullet((int)ship.getElement().getTranslateX(), (int)ship.getElement().getTranslateY());
                    bullet.getElement().setRotate(ship.getElement().getRotate());
                    bullets.add(bullet);

                    pane.getChildren().add(bullet.getElement());
                    bullet.getElement().setFill(Color.WHITE);
                    bullet.applyForce(
                            Math.cos(Math.toRadians(ship.getElement().getRotate())) * 10 ,
                            Math.sin(Math.toRadians(ship.getElement().getRotate())) * 10 );
                }


                asteroids.forEach(asteroid -> asteroid.move());
                bullets.forEach(bullet -> bullet.move());
                ship.move();

                for (Asteroids asteroid: asteroids) {
                    if (ship.collide(asteroid)) {
                        stop();
                    }
                }

                bullets.forEach(bullet -> {
                    List<Asteroids> collisions = asteroids.stream()
                            .filter(asteroid -> asteroid.collide(bullet))
                            .collect(Collectors.toList());

                    collisions.stream().forEach(collided -> {
//                        if(collided.getFlag() != 1) {
//                            asteroids.add(new Asteroids((int)collided.getElement().getTranslateX(),
//                                    (int)collided.getElement().getTranslateY(), collided.getFlag() - 1));
//                            asteroids.add(new Asteroids((int)collided.getElement().getTranslateX(),
//                                    (int)collided.getElement().getTranslateY(), collided.getFlag() - 1));
//                        }
                        asteroids.remove(collided);
                        pane.getChildren().remove(collided.getElement());
                    });
                });


            }
        }.start();
    }

    // main method to launch the window
    public static void main(String[] args) {
        launch(args);

    }

}