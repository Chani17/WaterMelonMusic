package kosa.watermelon.watermelonmusic;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.ByteArrayInputStream;
import java.net.URL;

import java.util.ResourceBundle;

public class EditMusicController implements Initializable {

    @FXML private ImageView albumCover;
    @FXML private Label songTitle;
    @FXML private Label artistName;
    @FXML private Slider startPointSlider;
    @FXML private Slider endPointSlider;
    @FXML private Label startTimeHour;
    @FXML private Label startTimeMinute;
    @FXML private Button playButtonStart;
    @FXML private Button pauseButtonStart;
    @FXML private Button stopButtonStart;
    @FXML private Label endTimeHour;
    @FXML private Label endTimeMinute;
    @FXML private Button playButtonEnd;
    @FXML private Button pauseButtonEnd;
    @FXML private Button stopButtonEnd;
    private MediaPlayer mediaPlayer;
    private Song song;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // slider listener를 추가하여 시작 시간과 끝 시간을 update
        startPointSlider.valueProperty().addListener((music, startPoint, endPoint) -> updateStartTime(endPoint.doubleValue()));
        endPointSlider.valueProperty().addListener((music, startPoint, endPoint) -> updateEndTime(endPoint.doubleValue()));

        // 플레이어 제어 버튼에 대한 이벤트 핸들러 설정
        playButtonStart.setOnAction(e -> playFromStart());
        pauseButtonStart.setOnAction(e -> pause());
        stopButtonStart.setOnAction(e -> stop());

        playButtonEnd.setOnAction(e -> playFromEnd());
        pauseButtonEnd.setOnAction(e -> pause());
        stopButtonEnd.setOnAction(e -> stop());
    }

    public void setSong(Song song) {
        this.song = song;
        System.out.println("editmusic : " + song.getName());
        setEditView();
        initializeMediaPlayer();
    }

    private void initializeMediaPlayer() {
        Media media = new Media(song.getMediaSource());
        mediaPlayer = new MediaPlayer(media);
    }

    private void setEditView() {
        ByteArrayInputStream bis = new ByteArrayInputStream(song.getAlbumCover());
        Image image = new Image(bis);
        albumCover.setImage(image);
        songTitle.setText(song.getName());
        artistName.setText(song.getArtist());
    }

    private void updateEndTime(double point) {
        int totalSeconds = (int) (mediaPlayer.getTotalDuration().toSeconds() * (point / 100.0));
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        endTimeHour.setText(String.valueOf(hours));
        endTimeMinute.setText(String.format("%02d", minutes));
    }

    private void updateStartTime(double point) {
        int totalSeconds = (int) (mediaPlayer.getTotalDuration().toSeconds() * (point / 100.0));
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        startTimeHour.setText(String.valueOf(hours));
        startTimeMinute.setText(String.format("%02d", minutes));
    }

    private void stop() {
        mediaPlayer.stop();
    }

    private void pause() {
        mediaPlayer.pause();
    }

    private void playFromEnd() {
        mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(endPointSlider.getValue() / 100));
        mediaPlayer.play();
    }

    private void playFromStart() {
        mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(startPointSlider.getValue() / 100.0));
        mediaPlayer.play();
    }
}
