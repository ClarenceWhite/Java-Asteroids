import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Window extends Application{
    //override class method 'start' to create a new window for the game
    @Override
    public void start(Stage stage) throws Exception {
        Pane pane = new Pane();
        pane.setPrefSize(1000, 1000);

        Scene scene = new Scene(pane);
        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.show();
    }
    // main method to launch the window
    public static void main(String[] args) {
        launch(args);
    }

}