package kosa.watermelon.watermelonmusic;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;

import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    @FXML private Button saveButton;
    private MediaPlayer mediaPlayer;
    private Song song;
    private Member currentMember;

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

        saveButton.setOnAction(e -> saveMusic());
    }

    public void setSong(Song song) {
        this.song = song;
        System.out.println("editmusic : " + song.getName());
        setMember(currentMember);
        setEditView();
        initializeMediaPlayer();
    }

    private void initializeMediaPlayer() {
        File file = new File(song.getMediaSource());
        String uriString = file.toURI().toString();
        Media media = new Media(uriString);
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

    private void saveMusic() {
        // Open the popup to get the song name from the user
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("saveEditSong.fxml"));
            Parent root = loader.load();
            SaveEditSongController controller = loader.getController();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Enter Song Name");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

            // Get the entered song name from the controller
            String songName = controller.getSongName();
            if (songName != null && !songName.isEmpty()) {
                // Continue with saving the music
                double start = startPointSlider.getValue();
                double end = endPointSlider.getValue();
                String sourceFilePath = song.getMediaSource();
                String destinationFilePath = "C:\\dev\\resources\\music\\" + songName + ".mp3";

                slice(sourceFilePath, destinationFilePath, start, end);
                Connection conn = DBUtil.getConnection();
                PreparedStatement countPstmt = conn.prepareStatement("SELECT COUNT(*) FROM EDITSONG WHERE MEMBER_ID=?");
                countPstmt.setString(1, currentMember.getId());
                ResultSet rsCount = countPstmt.executeQuery();

                long editSongId = 0L;
                if (rsCount.next()) {
                    editSongId = rsCount.getLong(1) + 1;  // Assuming you want to use the next ID
                }

                PreparedStatement savePstmt = conn.prepareStatement("INSERT INTO EDITSONG (EDITSONG_ID, EDITSONG_NAME, SONG_FILE, SONG_ID, MEMBER_ID) VALUES (?,?,?,?,?)");

                savePstmt.setLong(1, editSongId);
                savePstmt.setString(2, songName);
                savePstmt.setString(3, destinationFilePath);
                savePstmt.setLong(4, song.getId());
                savePstmt.setString(5, currentMember.getId());

                savePstmt.executeUpdate();
                System.out.println("음악이 성공적으로 저장되었습니다.");

                savePstmt.close();
                DBUtil.close(rsCount, countPstmt, conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void slice(String sourceFilePath, String destinationFilePath, double startPoint, double endPoint) {
        try {
            File sourceFile = new File(sourceFilePath);
            FileInputStream fis = new FileInputStream(sourceFile);
            FileOutputStream fos = new FileOutputStream(destinationFilePath);

            long fileLength = sourceFile.length();
            long startByte = (long) (startPoint / 100.0 * fileLength);
            long endByte = (long) (endPoint / 100.0 * fileLength);

            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;

            fis.skip(startByte);
            while ((bytesRead = fis.read(buffer)) != -1 && totalBytesRead < (endByte - startByte)) {
                if (totalBytesRead + bytesRead > (endByte - startByte)) {
                    bytesRead = (int) ((endByte - startByte) - totalBytesRead);
                }
                fos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }

            fis.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setMember(Member member) {
        this.currentMember = member;
    }
}