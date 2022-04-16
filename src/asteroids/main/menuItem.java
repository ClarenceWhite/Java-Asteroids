package asteroids.main;

import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.beans.binding.Bindings;



public class menuItem extends Pane {
    private Text menu;

    private Effect shadow = new DropShadow(5, Color.BLACK);
    private Effect blur = new BoxBlur(1,1,3);

    public menuItem(String name) {
        Polygon bg = new Polygon(
                0, 0,
                200, 0,
                215, 15,
                200, 30,
                0, 30
        );
        bg.setStroke(Color.color(1,1,1,0.75));
        bg.setEffect(new GaussianBlur());

        bg.fillProperty().bind(
                Bindings.when(pressedProperty())
                        .then(Color.color(0,0,0,0.75))
                        .otherwise(Color.color(0,0,0,0.25))
        );

        menu = new Text(name);
        menu.setTranslateX(5);
        menu.setTranslateY(20);
        menu.setFont(Font.loadFont(menuItem.class.getResource("Paul-le1V.ttf").toExternalForm(), 24));
        menu.setFill(Color.SNOW);

        menu.effectProperty().bind(
                Bindings.when(hoverProperty())
                        .then(shadow)
                        .otherwise(blur)
        );

        getChildren().addAll(bg, menu);
    }

    public void setOnAction(Runnable action){
        setOnMouseClicked(e -> action.run());
    }

}
