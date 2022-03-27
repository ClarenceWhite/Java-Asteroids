
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class BGM {
    public void mainBGM(){
        Media mainBGM = new Media(new File("mainBGM.mp3").toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(mainBGM);
        mediaPlayer.play();
    }
}
