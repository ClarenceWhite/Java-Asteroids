package asteroids.main;

import javafx.animation.AnimationTimer;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Window extends Application{
    private String name = userName(); //record user name
    private Stage stage;
    private Scene scene1; //scene1 for menu
    private Scene scene2; //scene2 for game
    private Scene scene3; //scene3 for showing high score
    private Scene scene4; //scene4 for user instrucition

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    private static int level = 1;

    private Pane pane = new Pane(); // pane
    private Pane pane2 = new Pane(); //pane2
    private Pane pane3 = new Pane(); //pane3
    private Pane pane4 = new Pane(); //pane4
    private VBox menuBox = new VBox(-5); //menu box one for menu page
    private Line line;

    private List<Pair<String, Runnable>> menuData = Arrays.asList(  //menuData one for menu page
            new Pair<String, Runnable>("Start Game", () -> {PlayGame();}),
            new Pair<String, Runnable>("High Score", () -> {showHighScore();}),
            new Pair<String, Runnable>("How to Play", () -> {showInstruction();}),
            new Pair<String, Runnable>("Exit", Platform::exit)
    );


    //add menu contents to the pane
    private Parent createContent() {
        addBackground();
        addTitle();

        double lineX = WIDTH / 2 - 100;
        double lineY = HEIGHT / 3 + 60;

        addLine(lineX, lineY);
        addMenu(lineX + 5, lineY + 5);

        startAnimation();

        return pane;
    }
    //add background to the menu pane
    private void addBackground() {
        ImageView imageView = new ImageView(new Image(getClass().getResource("background.png").toExternalForm()));
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);

        pane.getChildren().add(imageView);
    }
    //add title to the menu pane
    private void addTitle() {
        Title title = new Title("ASTEROIDS");
        title.setTranslateX(WIDTH / 2 - title.getTitleWidth() / 2);
        title.setTranslateY(HEIGHT / 3);

        pane.getChildren().add(title);
    }
    //add an animation line to the menu pane
    private void addLine(double x, double y) {
        line = new Line(x, y, x, y + 150);
        line.setStrokeWidth(3);
        line.setStroke(Color.color(1, 1, 1, 0.75));
        line.setEffect(new DropShadow(5, Color.BLACK));
        line.setScaleY(0);

        pane.getChildren().add(line);
    }
    //let the line animating
    private void startAnimation() {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1), line);
        st.setToY(1);
        st.setOnFinished(e -> {

            for (int i = 0; i < menuBox.getChildren().size(); i++) {
                Node n = menuBox.getChildren().get(i);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(1 + i * 0.15), n);
                tt.setToX(0);
                tt.setOnFinished(e2 -> n.setClip(null));
                tt.play();
            }
        });
        st.play();
    }
    //add menu buttons
    private void addMenu(double x, double y) {
        menuBox.setTranslateX(x);
        menuBox.setTranslateY(y);
        menuData.forEach(data -> {
            menuItem item = new menuItem(data.getKey());
            item.setOnAction(data.getValue());
            item.setTranslateX(-300);

            Rectangle clip = new Rectangle(300, 30);
            clip.translateXProperty().bind(item.translateXProperty().negate());

            item.setClip(clip);

            menuBox.getChildren().addAll(item);
        });

        pane.getChildren().add(menuBox);
    }



    //start method of class Application
    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        stage.setTitle("Asteroids");

        scene1 = new Scene(createContent());

        stage.setScene(scene1); //set the menu scene as default
        stage.show();
    }


    //play game method
    private Parent PlayGame() {
        ImageView imageView = new ImageView(new Image(getClass().getResource("game_bg.png").toExternalForm()));
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);
        pane2.getChildren().add(imageView);
        pane2.toFront();
        scene2 = new Scene(pane2); //add pane to the scene
        System.out.println("start game clicked... from start method");
        pane2.setPrefSize(WIDTH, HEIGHT); //set pane size
        AtomicInteger points = new AtomicInteger(); //points counter
        String[] highPoints = {""}; //high score counter

        Text text = new Text(10, 20, "Points: 0");
        Text textLevel = new Text(10, 50, "Level: 1");
        Text HighScore = new Text(10, 80, "High Score:");
        Text health = new Text(WIDTH - 90, 20, "Lives: 3");
        Text alert = new Text(WIDTH/2-80,50, "");

        pane2.getChildren().add(health);
        health.setFill(Color.WHITE);
        health.setFont(new Font(24));

        pane2.getChildren().add(HighScore);
        HighScore.setFill(Color.WHITE);
        HighScore.setFont(new Font(24));

        pane2.getChildren().add(text);
        text.setFill(Color.WHITE);
        text.setFont(new Font(24));

        pane2.getChildren().add(textLevel);
        textLevel.setFill(Color.WHITE);
        textLevel.setFont(new Font(24));

        pane2.getChildren().add(alert);
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
        pane2.getChildren().add(asteroid.getElement()); //add the asteroid to the screen
        asteroid.getElement().setStroke(Color.WHITE); // set the stroke color of the asteroid to white

        Ship ship = new Ship(WIDTH / 2, HEIGHT / 2); //initialize the ship at the center of the window
        pane2.getChildren().add(ship.getElement()); // add a spaceship to the screen
        ship.getElement().setStroke(Color.WHITE); //set color of spaceship

        Alien alien = new Alien(r.nextInt(50,100), r.nextInt(50,HEIGHT-50)); //initialize an alien ship
        alien.setLives(0); //initialize alien live to 0

        scene2.setRoot(pane2);
        stage.setScene(scene2); //set scene to stage
        stage.show(); //show stage

        HashMap<KeyCode, Boolean> pressedKeys = new HashMap<KeyCode, Boolean>(); //hashmap store KeyCode and Boolean pairs
        scene2.setOnKeyPressed(event -> pressedKeys.put(event.getCode(), Boolean.TRUE)); //key pressed
        scene2.setOnKeyReleased(event -> pressedKeys.put(event.getCode(), Boolean.FALSE)); //key released
        //replace the high score with recorded high score
        if(highPoints[0].equals("")) {
            highPoints[0] = getHighScore();
            HighScore.setText("High Score: " + highPoints[0]);
        }
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
                    checkScore(points, highPoints);
                    stop();
                }
                //left key to rotate angle by -5 degree
                if(pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.rotate(-5);
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
                            pane2.getChildren().remove(ship.getElement());  //remove the user ship from pane
                            ship.setXY(shipsnewXY.get(0), shipsnewXY.get(1));
                            pane2.getChildren().add(ship.getElement());  //remove the user ship from pane
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
                        pane2.getChildren().add(bullet.getElement()); // display bullet on the pane
                        bullet.getElement().setFill(Color.rgb(255,69,0)); // set color of bullet
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
                    pane2.getChildren().add(alien.getElement()); //add alien ship to pane
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
                    pane2.getChildren().add(alienBullet.getElement()); // add alien bullet to pane
                    alienBullet.getElement().setFill(Color.rgb(0,135,255)); // set color of alien bullet
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
                    pane2.getChildren().remove(alien.getElement());
                }

                //check if there is collision between user ship and alien bullets
                for (Bullet alienBullet: alienBullets) {
                    if (ship.collide(alienBullet) && alienBullet.getLives() != 0) {
                        pane2.getChildren().remove(ship.getElement());  //remove the user ship from pane
                        System.out.println("User hits with alien bullet!");
                        alienBullet.setLives(0);
                        pane2.getChildren().remove(alienBullet.getElement());
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
                            pane2.getChildren().add(ship.getElement());  //remove the user ship from pane
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
                        pane2.getChildren().remove(ship.getElement());  //remove the user ship from pane
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
                            pane2.getChildren().add(ship.getElement());  //remove the user ship from pane
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
                        pane2.getChildren().remove(alien.getElement());
                        pane2.getChildren().remove(bullet.getElement());
                        text.setText("Points: " + points.addAndGet(200)); // add points 200 for shooting the alien
                    }
                    // check if bullets have traveled for a certain distance and whether they'd be removed
                    if (bullet.shouldRemove() && bullet.getLives() != 0){
                        bullet.setLives(0); //remove bullet's live
                        pane2.getChildren().remove(bullet.getElement()); // remove bullet from pane
                    }

                    if(bullet.getLives() != 0) {
                        //stream in Java can be defined as a sequence of elements from a source,
                        // the stream here represents the asteroids objects in the list 'asteroids'
                        List<Asteroids> collisions = asteroids.stream()
                                .filter(asteroid -> (asteroid.collide(bullet) && asteroid.getLives() != 0)).toList(); //collect those asteroids to list

                        if (collisions.size() != 0 && bullet.getLives() != 0) { //if the collision list is not empty
                            bullet.setLives(0); //remove the bullet's live that hit the asteroid
                            pane2.getChildren().remove(bullet.getElement()); //remove the bullet from the pane
                        }
                        //iterate through the collision list
                        collisions.forEach(collided -> { //for each asteroid which has collided with a bullet
                            if (collided.getFlag() == 3 || collided.getFlag() == 2) { //if it is big or middle asteroid
                                //create a left child
                                Asteroids left_child = new Asteroids((int) collided.getElement().getTranslateX(),
                                        (int) collided.getElement().getTranslateY(), collided.getFlag() - 1);
                                asteroids.add(left_child); //create a new asteroid which is one size smaller than the broken one
                                left_child.getElement().setStroke(Color.WHITE);
                                pane2.getChildren().add(left_child.getElement());
                                //create a right child
                                Asteroids right_child = new Asteroids((int) collided.getElement().getTranslateX(),
                                        (int) collided.getElement().getTranslateY(), collided.getFlag() - 1); //create another new asteroid which is one size smaller than the broken one
                                asteroids.add(right_child);
                                right_child.getElement().setStroke(Color.WHITE);
                                pane2.getChildren().add(right_child.getElement());
                            }
                            if (alienBullets.contains(bullet)) {
                                System.out.println("alien's bullet hit the asteroid, do not count points");  //if the alien's bullet hit the asteroids
                            }
                            else {
                                text.setText("Points: " + points.addAndGet(100)); // add points if the user's bullet hit asteroids
                            }
                            collided.setLives(0); //remove collided asteroids' live
                            pane2.getChildren().remove(collided.getElement()); //remove asteroids from pane
                        });
                    }
                }

                //Game Level
                if (checkAsteroids(asteroids) && (alien.getLives() == 0 || checkAlien(aliens)) && ship.getLives() > 0) {
                    //remove bullets from both general bullet list and alien bullets list, and the screen as well
                    for (Bullet bullet: bullets) {
                        pane2.getChildren().remove(bullet.getElement());
                    }
                    for (Bullet bullet: alienBullets) {
                        pane2.getChildren().remove(bullet.getElement());
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
                        pane2.getChildren().add(asteroid.getElement());
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
//        checkScore(points, highPoints);
        return pane2;
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

    //get high score method
    public String getHighScore() {
        System.out.println("calling get high score method ...");
        FileReader readFile = null;
        BufferedReader reader = null;
        try {
            readFile = new FileReader("highscore.dat");
            reader = new BufferedReader(readFile);
            return reader.readLine();
        }
        catch (Exception e) {
            return "null:0";
        }
        finally {
            try {
                if (reader != null){
                    reader.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //check the score and see if we need to save it as high score
    public void checkScore(AtomicInteger points, String[] highPoints) {
        System.out.println("calling checkScore method ...");
        if(highPoints[0].equals("")){
            System.out.println("not changed");
            return;
        }
        if(points.get() > Integer.parseInt(getHighScore().split(":")[1])) {
            System.out.println("get high score");
            highPoints[0] = name + ":" + points.get();

            File scoreFile = new File("highscore.dat");
            if(!scoreFile.exists()) {
                try {
                    System.out.println("creating new file.");
                    scoreFile.createNewFile();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FileWriter writeFile = null;
            BufferedWriter writer = null;
            try {
                writeFile = new FileWriter(scoreFile);
                writer = new BufferedWriter(writeFile);
                writer.write(highPoints[0]);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if(writer != null) {
                        writer.close();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //input name function
    public String userName() {
        //Before play the game, let the user input his/her name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input your name");
        dialog.setHeaderText("Input your name");
        dialog.setContentText("Please input your name here: ");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> System.out.println("Your name: " + name));
        return result.get();
    }

    //method to create contents for high score page
    public Parent showHighScore() {
        //set background for high score page
        ImageView imageView = new ImageView(new Image(getClass().getResource("honor.jpeg").toExternalForm()));
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);
        pane3.getChildren().add(imageView);

        //start a new game button
        javafx.scene.control.Button button = new Button("Start a New Game");
        HBox buttonBox = new HBox(button);
        buttonBox.setLayoutX(WIDTH/2-50);
        buttonBox.setLayoutY(HEIGHT-200);
        button.setOnAction(actionEvent -> {
            PlayGame();
        });
        pane3.getChildren().add(buttonBox);

        //set titles and contents
        Title title1 = new Title("Highest Score");
        title1.setTranslateX(WIDTH / 2 - title1.getTitleWidth() / 2);
        title1.setTranslateY(HEIGHT / 3);
        Title title2 = new Title(getHighScore().split(":")[0]);
        title2.setTranslateX(WIDTH/2-title2.getTitleWidth()/2);
        title2.setTranslateY(HEIGHT/3 + 100);
        Title title3 = new Title(getHighScore().split(":")[1]);
        title3.setTranslateX(WIDTH/2-title3.getTitleWidth()/2);
        title3.setTranslateY(HEIGHT/3 + 200);
        pane3.getChildren().add(title3);
        pane3.getChildren().add(title2);
        pane3.getChildren().add(title1);

        scene3 = new Scene(pane3);
        stage.setScene(scene3);
        stage.show();

        return pane3;
    }

    public Parent showInstruction() {
        //set background for instruction page
        ImageView imageView = new ImageView(new Image(getClass().getResource("instruction_bg.jpeg").toExternalForm()));
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);
        pane4.getChildren().add(imageView);

        //start a new game button
        javafx.scene.control.Button button = new Button("Start a New Game");
        HBox buttonBox = new HBox(button);
        buttonBox.setLayoutX(WIDTH/2-50);
        buttonBox.setLayoutY(HEIGHT-100);
        button.setOnAction(actionEvent -> {
            PlayGame();
        });
        pane4.getChildren().add(buttonBox);

        //set titles and contents
        Title title1 = new Title("How to play");
        title1.setTranslateX(WIDTH / 2 - title1.getTitleWidth() / 2);
        title1.setTranslateY(HEIGHT / 5);
        Title title2 = new Title("Use LEFT and RIGHT to rotate");
        title2.setTranslateX(WIDTH/2-title2.getTitleWidth()/2);
        title2.setTranslateY(HEIGHT/5 + 100);
        Title title3 = new Title("Use UP to apply thrust");
        title3.setTranslateX(WIDTH/2-title3.getTitleWidth()/2);
        title3.setTranslateY(HEIGHT/5 + 200);
        Title title4 = new Title("Use H to hyperspace jump");
        title4.setTranslateX(WIDTH/2-title3.getTitleWidth()/2);
        title4.setTranslateY(HEIGHT/5 + 300);
        Title title5 = new Title("Use SPACE to fire");
        title5.setTranslateX(WIDTH/2-title3.getTitleWidth()/2);
        title5.setTranslateY(HEIGHT/5 + 400);
        pane4.getChildren().add(title5);
        pane4.getChildren().add(title4);
        pane4.getChildren().add(title3);
        pane4.getChildren().add(title2);
        pane4.getChildren().add(title1);

        scene4 = new Scene(pane4);
        stage.setScene(scene4);
        stage.show();

        return pane4;
    }

    // main method to launch the window
    public static void main(String[] args) {
        launch(args);
    }

}
