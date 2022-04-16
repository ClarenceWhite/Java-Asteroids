package asteroids.main;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

public class Title extends Pane {
    private Text title;

    public Title(String name) {
        String spread = "";
        for (char c : name.toCharArray()) {
            spread += c + " ";
        }

        title = new Text(spread);
        title.setFont(Font.loadFont(Title.class.getResource("ChrustyRock-ORLA.ttf").toExternalForm(), 48));
        title.setFill(Color.WHITE);
        title.setEffect(new DropShadow(30,Color.BLACK));

        getChildren().addAll(title);
    }

    public double getTitleWidth() {
        return title.getLayoutBounds().getWidth();
    }

    public double getTitleHeight() {
        return title.getLayoutBounds().getHeight();
    }
}
