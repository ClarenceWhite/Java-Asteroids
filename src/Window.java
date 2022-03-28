import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Window extends Application{
    //override class method 'start' to create a new window for the game
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    @Override
    public void start(Stage stage) throws Exception {

        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);

        Ship ship = new Ship(WIDTH / 2, HEIGHT / 2);
        List<Asteroids> asteroids = new ArrayList<Asteroids>();

        for (int i = 0; i < 10; i++) {
            Random r = new Random();
            Asteroids asteroid = new Asteroids(r.nextInt(WIDTH / 3), r.nextInt(HEIGHT), r.nextInt(2) + 1);
            asteroids.add(asteroid);
        }

        for (Asteroids asteroid:asteroids) {
            pane.getChildren().add(asteroid.getElement());
        }
        pane.getChildren().add(ship.getElement());

        new AnimationTimer() {

            @Override
            public void handle(long now) {
                asteroids.forEach(asteroid -> asteroid.move());
            }
        }.start();

        Scene scene = new Scene(pane);
        scene.setFill(Color.WHITE);
        stage.setScene(scene);
        stage.show();
    }

    // main method to launch the window
    public static void main(String[] args) {
        launch(args);

    }

}