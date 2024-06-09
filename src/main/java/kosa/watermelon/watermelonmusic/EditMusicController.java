package kosa.watermelon.watermelonmusic;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;

import java.io.ByteArrayInputStream;
import java.net.URL;

import java.util.ResourceBundle;

public class EditMusicController implements Initializable {

    @FXML private ImageView albumCover;
    @FXML private Label songTitle;
    @FXML private Label artistName;
    private MediaPlayer mediaPlayer;
    private Song song;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setSongId(Song song) {
        this.song = song;
        setEditView();
    }

    private void setEditView() {
        ByteArrayInputStream bis = new ByteArrayInputStream(song.getAlbumCover());
        Image image = new Image(bis);
        albumCover.setImage(image);
        songTitle.setText(song.getName());
        artistName.setText(song.getArtist());



    }
}
