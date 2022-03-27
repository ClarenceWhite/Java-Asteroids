import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Window extends Application{
    //override class method 'start' to create a new window for the game
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Welcome to asteroids!"); //set a title for the stage

        BorderPane pane = new BorderPane(); // create a pane
        Scene scene = new Scene(pane); // add a new scene
        stage.setScene(scene); // set scene of stage to 'scene'
        stage.setResizable(false);

        Canvas canvas = new Canvas(1000,1000); // canvas
        GraphicsContext context = canvas.getGraphicsContext2D();
        pane.setCenter(canvas); //set center to canvas

        Sprites background = new Sprites("space.png");
        background.position.setXY(400,300);
        background.render(context);

        Sprites spaceship = new Sprites("spaceship.png");
        spaceship.position.setXY(300,300);
        spaceship.render(context);

        Sprites as = new Sprites("asteroids_big.png");
        as.position.setXY(300,500);
        as.render(context);

        Sprites as2 = new Sprites("asteroids_mid.png");
        as2.position.setXY(300,600);
        as2.render(context);

        Sprites as3 = new Sprites("asteroids_small.png");
        as3.position.setXY(300,700);
        as3.render(context);

        stage.show();
    }


    // main method to launch the window
    public static void main(String[] args) {
        try{
            launch(args);
        }
        catch (Exception error){
            error.printStackTrace();
        }
        finally {
           System.exit(0);
        }
    }

}