package kosa.watermelon.watermelonmusic;

import javafx.event.ActionEvent;
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
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
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
    @FXML private Label startTimeMinute;
    @FXML private Label startTimeSecond;
    @FXML private Button playButtonStart;
    @FXML private Button pauseButtonStart;
    @FXML private Button stopButtonStart;
    @FXML private Label endTimeMinute;
    @FXML private Label endTimeSecond;
    @FXML private Button playButtonEnd;
    @FXML private Button pauseButtonEnd;
    @FXML private Button stopButtonEnd;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    private MediaPlayer mediaPlayer;
    private MediaPlayer startMediaPlayer;
    private MediaPlayer endMediaPlayer;
    private Song song;
    private Member currentMember;
    private boolean isSliderChanging = false;
    private boolean isPlayingStart = false;
    private boolean isPlayingEnd = false;
    private double lastStartPosition = 0;
    private double lastEndPosition = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 슬라이더 리스너를 추가하여 시작 시간과 끝 시간을 업데이트
        startPointSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!isSliderChanging) {
                updateStartTime(newValue.doubleValue());
                lastStartPosition = newValue.doubleValue();
            }
        });

        endPointSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!isSliderChanging) {
                updateEndTime(newValue.doubleValue());
                lastEndPosition = newValue.doubleValue();
            }
        });

        // 슬라이더 조작 이벤트 핸들러 설정
        startPointSlider.setOnMousePressed(event -> isSliderChanging = true);
        startPointSlider.setOnMouseReleased(event -> {
            isSliderChanging = false;
            updateStartTime(startPointSlider.getValue());
            lastStartPosition = startPointSlider.getValue();
            if (isPlayingStart) {
                playFromStart();
            }
        });

        endPointSlider.setOnMousePressed(event -> isSliderChanging = true);
        endPointSlider.setOnMouseReleased(event -> {
            isSliderChanging = false;
            updateEndTime(endPointSlider.getValue());
            lastEndPosition = endPointSlider.getValue();
            if (isPlayingEnd) {
                playFromEnd();
            }
        });

        // 플레이어 제어 버튼에 대한 이벤트 핸들러 설정
        playButtonStart.setOnAction(e -> playFromStart());
        pauseButtonStart.setOnAction(e -> pauseStart());
        stopButtonStart.setOnAction(e -> stopStart());

        playButtonEnd.setOnAction(e -> playFromEnd());
        pauseButtonEnd.setOnAction(e -> pauseEnd());
        stopButtonEnd.setOnAction(e -> stopEnd());

        saveButton.setOnAction(e -> saveMusic());
    }

    @FXML
    private void backToPage(ActionEvent event) {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
    }

    public void setSong(Song song) {
        this.song = song;
        System.out.println("editmusic : " + song.getName());
        setMember(currentMember);
        initializeMediaPlayer();
        setEditView();
    }

    private void initializeMediaPlayer() {
        File file = new File(song.getMediaSource());
        String uriString = file.toURI().toString();
        Media media = new Media(uriString);
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnReady(() -> {
            initializeSliders();
        });

        // Separate media players for start and end
        startMediaPlayer = new MediaPlayer(media);
        endMediaPlayer = new MediaPlayer(media);
    }

    private void initializeSliders() {
        // 슬라이더 초기값 설정 및 시간 업데이트
        double durationInSeconds = mediaPlayer.getTotalDuration().toSeconds();
        startPointSlider.setMax(durationInSeconds);
        endPointSlider.setMax(durationInSeconds);

        startPointSlider.setValueChanging(true);
        endPointSlider.setValueChanging(true);

        startPointSlider.setValue(0);
        endPointSlider.setValue(0); // Assuming you want the end slider to start at the end of the song
    }

    private void setEditView() {
        ByteArrayInputStream bis = new ByteArrayInputStream(song.getAlbumCover());
        Image image = new Image(bis);
        albumCover.setImage(image);
        songTitle.setText(song.getName());
        artistName.setText(song.getArtist());

//        mediaPlayer.currentTimeProperty().addListener((observable, oldTime, newTime) -> updateStartPlayTime());
    }

    private void updateEndTime(double point) {
        if (mediaPlayer != null) {
            int minutes = (int)point / 60;
            int seconds = (int) point % 60;

            endPointSlider.setValue(point);
            endTimeMinute.setText(String.format("%02d", minutes));
            endTimeSecond.setText(String.format("%02d", seconds));
        }
    }

    private void updateStartTime(double point) {
        if (mediaPlayer != null) {
            int minutes = (int) point / 60;
            int seconds = (int) point % 60;

            startPointSlider.setValue(point);
            startTimeMinute.setText(String.format("%02d", minutes));
            startTimeSecond.setText(String.format("%02d", seconds));
        }
    }

    private void stopStart() {
        if (startMediaPlayer != null) {
            lastStartPosition = startPointSlider.getValue();
            System.out.println("stop = " + lastStartPosition);
            startMediaPlayer.stop();
            isPlayingStart = false;
        }
    }

    private void pauseStart() {
        if (startMediaPlayer != null) {
            startMediaPlayer.pause();
            isPlayingStart = false;
        }
    }

    private void stopEnd() {
        if (endMediaPlayer != null) {
            lastEndPosition = endPointSlider.getValue();
            endMediaPlayer.stop();
            isPlayingEnd = false;
        }
    }

    private void pauseEnd() {
        if (endMediaPlayer != null) {
            endMediaPlayer.pause();
            isPlayingEnd = false;
        }
    }

    private void playFromEnd() {
        if (endMediaPlayer != null) {
            System.out.println("lastEndPosition = " + lastEndPosition);
            endMediaPlayer.seek(Duration.seconds(lastEndPosition));
            endMediaPlayer.play();
            isPlayingEnd = true;
            endMediaPlayer.currentTimeProperty().addListener((observable, oldTime, newTime) -> updateEndPlayTime());
        }
    }

    private void playFromStart() {
        if (startMediaPlayer != null) {
            System.out.println("lastStartPosition = " + lastStartPosition);
            startMediaPlayer.seek(Duration.seconds(lastStartPosition));
            startMediaPlayer.play();
            isPlayingStart = true;
            startMediaPlayer.currentTimeProperty().addListener((observable, oldTime, newTime) -> updateStartPlayTime());
        }
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
            Image icon = new Image(
	        		getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고 이미지 파일 경로 지정
			popupStage.getIcons().add(icon);
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

                System.out.println("start = " + start + " end = " + end);

                slice(sourceFilePath, destinationFilePath, start, end);
                Connection conn = DBUtil.getConnection();
                PreparedStatement countPstmt = conn.prepareStatement("SELECT COUNT(*) FROM EDITSONG");
//                countPstmt.setString(1, currentMember.getId());
                ResultSet rsCount = countPstmt.executeQuery();

                long editSongId = 0L;
                if (rsCount.next()) {
                    editSongId = rsCount.getLong(1);  // Assuming you want to use the next ID
                    System.out.println("editSongId = " + editSongId);
                }

                PreparedStatement savePstmt = conn.prepareStatement("INSERT INTO EDITSONG (EDITSONG_ID, SONG_NAME, SONG_FILE, SONG_ID, MEMBER_ID) VALUES (?,?,?,?,?)");

                savePstmt.setLong(1, editSongId + 1);
                savePstmt.setString(2, songName);
                savePstmt.setString(3, destinationFilePath);
                savePstmt.setLong(4, song.getId());
                savePstmt.setString(5, currentMember.getId());

                savePstmt.executeUpdate();
                System.out.println("음악이 성공적으로 저장되었습니다.");

                savePstmt.close();
                DBUtil.close(conn, countPstmt, rsCount);
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


    private void updateStartPlayTime() {
        if (startMediaPlayer == null || isSliderChanging) return;

        long totalTime = (long) startMediaPlayer.getTotalDuration().toSeconds();
        long currentTime = (long) startMediaPlayer.getCurrentTime().toSeconds();
        long currentTimeMinute = currentTime / 60;
        long currentTimeSecond = currentTime % 60;

        startPointSlider.setValue((double) currentTime / totalTime * 100);
        startTimeMinute.setText(String.format("%02d", currentTimeMinute));
        startTimeSecond.setText(String.format("%02d", currentTimeSecond));
    }

    private void updateEndPlayTime() {
        if (endMediaPlayer == null || isSliderChanging) return;

        long totalTime = (long) endMediaPlayer.getTotalDuration().toSeconds();
        long currentTime = (long) endMediaPlayer.getCurrentTime().toSeconds();
        long currentTimeMinute = currentTime / 60;
        long currentTimeSecond = currentTime % 60;

        endPointSlider.setValue((double) currentTime / totalTime * 100);
        endTimeMinute.setText(String.format("%02d", currentTimeMinute));
        endTimeSecond.setText(String.format("%02d", currentTimeSecond));
    }

    public void setMember(Member member) {
        this.currentMember = member;
    }
}
