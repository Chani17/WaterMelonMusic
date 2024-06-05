package kosa.watermelon.watermelonmusic;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import oracle.jdbc.OracleResultSet;
import oracle.sql.BFILE;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class PlayViewController implements Initializable {

    private final static String id = "admin";
    private final static String pw = "1234";
    private final static String url = "jdbc:oracle:thin:@localhost:1521:xe";

    @FXML
    private Label songTitle;
    @FXML
    private Label artist;
    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button pauseButton;
    @FXML
    private ImageView albumCover;
    @FXML
    private Slider playBar;
    @FXML
    private Label playTimeHour;
    @FXML
    private Label playTimeMinute;
    @FXML
    private Label endTimeHour;
    @FXML
    private Label endTimeMinute;
    private MediaPlayer mediaPlayer;
    private long songId;
    private long totalTime;
    private long totalTimeHour;
    private long totalTimeMinute;
    private boolean isPlaying = true;
    private boolean isSliderChanging = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playBar.setValueChanging(true);     // 외부에서 슬라이더바 조작 가능
        playBar.setValue(0);
        playBar.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (isSliderChanging) {
                double currentTime = (newValue.doubleValue() / 100) * totalTime;
                mediaPlayer.seek(javafx.util.Duration.seconds(currentTime));
            }
        }));

        playBar.setOnMousePressed(event -> isSliderChanging = true);
        playBar.setOnMouseReleased(event -> isSliderChanging = false);

        playButton.setOnAction(event -> togglePlayPause());
        stopButton.setOnAction(event -> stopSong());
        pauseButton.setOnAction(event -> pauseSong());
    }

    public void setSongId(long id) {
        this.songId = id;
        setPlayView();
    }

    private void setPlayView() {
        Connection conn = DBConnection();
        PreparedStatement pstmt_song = null;
        PreparedStatement pstmt_album = null;
        ResultSet res_song = null;
        ResultSet res_album = null;

        try {
            pstmt_song = conn.prepareStatement("SELECT s.song_name, s.song_file, a.album_cover, ar.artist_name\n" +
                    "FROM Song s\n" +
                    "LEFT OUTER JOIN Album a \n" +
                    "ON s.album_id = a.album_id \n" +
                    "LEFT OUTER JOIN Artist ar\n" +
                    "ON ar.artist_id = a.artist_id\n" +
                    "WHERE song_id=?");
            pstmt_song.setLong(1, this.songId);
            res_song = pstmt_song.executeQuery();

            if (res_song.next()) {
                String songName = res_song.getString("song_name");
                String artistName = res_song.getString("artist_name");
                BFILE bfile = ((OracleResultSet) res_song).getBFILE("album_cover");

                songTitle.setText(songName);
                artist.setText(artistName);

                bfile.openFile(); // BFILE 열기
                InputStream inputStream = bfile.getBinaryStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                byte[] imageData = outputStream.toByteArray();
                outputStream.close();
                inputStream.close();
                PlaylistView playlistView = new PlaylistView(songName, artistName, imageData);

                // 이미지 데이터를 이용하여 Image 객체 생성
                Image image = new Image(new ByteArrayInputStream(playlistView.getAlbumCover()));

                // ImageView에 이미지 설정
                albumCover.setImage(image);

                String songFilePath = res_song.getString("song_file");

                // 미디어 등록
                Media media = new Media(new File(songFilePath).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnReady(() -> {
                    totalTime = (long) mediaPlayer.getTotalDuration().toSeconds();
                    totalTimeHour = totalTime / 60;
                    totalTimeMinute = totalTime % 60;
                    endTimeHour.setText(String.format("%02d", totalTimeHour));
                    endTimeMinute.setText(String.format("%02d", totalTimeMinute));
                });
                mediaPlayer.setAutoPlay(true);
                mediaPlayer.currentTimeProperty().addListener((observable, oldTime, newTime) -> updatePlayTime());
                mediaPlayer.setOnEndOfMedia(this::resetMediaPlayer);
            } else {
                System.out.println("No song found with id = " + this.songId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 리소스 정리
                if (res_song != null) res_song.close();
                if (res_album != null) res_album.close();
                if (pstmt_song != null) pstmt_song.close();
                if (pstmt_album != null) pstmt_album.close();
                DBClose(conn);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) return;

        if (isPlaying) {
            pauseSong();
        } else {
            playSong();
        }
    }

    private void playSong() {
        if (mediaPlayer == null) return;

        mediaPlayer.play();
        isPlaying = true;

        // UI를 주기적으로 업데이트하는 스레드 생성
        Thread thread = new Thread(() -> {
            while (isPlaying) {
                Platform.runLater(this::updatePlayTime);

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true); // 애플리케이션 종료 시 스레드 자동 종료
        thread.start();
    }

    private void pauseSong() {
        if (mediaPlayer == null) return;

        mediaPlayer.pause();
        isPlaying = false;
    }

    private void stopSong() {
        if (mediaPlayer == null) return;

        mediaPlayer.stop();
        isPlaying = false;
        playBar.setValue(0);
        playTimeHour.setText("0");
        playTimeMinute.setText("00");
        endTimeHour.setText(String.format("%02d", totalTimeHour));
        endTimeMinute.setText(String.format("%02d", totalTimeMinute));
    }

    private void updatePlayTime() {
        if (mediaPlayer == null || isSliderChanging) return;

        long currentTime = (long) mediaPlayer.getCurrentTime().toSeconds();
        long currentTimeHour = currentTime / 60;
        long currentTimeMinute = currentTime % 60;

        playBar.setValue((double) currentTime / totalTime * 100);

        playTimeHour.setText(String.valueOf(currentTimeHour));
        if (currentTimeMinute < 10) playTimeMinute.setText("0" + currentTimeMinute);
        else playTimeMinute.setText(String.valueOf(currentTimeMinute));

        long remainingTime = totalTime - currentTime;
        long remainingHour = remainingTime / 60;
        long remainingMinute = remainingTime % 60;

        endTimeHour.setText(String.format("%02d", remainingHour));
        endTimeMinute.setText(String.format("%02d", remainingMinute));
    }

    private void resetMediaPlayer() {
        isPlaying = false;
        playBar.setValue(0);
        playTimeHour.setText("0");
        playTimeMinute.setText("00");
        endTimeHour.setText(String.format("%02d", totalTimeHour));
        endTimeMinute.setText(String.format("%02d", totalTimeMinute));
        mediaPlayer.seek(javafx.util.Duration.ZERO);
        mediaPlayer.stop();
    }

    private Connection DBConnection() {
        //드라이버 검색 (db와 연동 준비)
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver search success");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver search fail");
            System.exit(0);
        }

        //데이터베이스 연결 - 커넥션 만들기
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url, id, pw);
            System.out.println("Success");
        } catch (SQLException e) {
            System.err.println("Fail");
            System.exit(0);
        }
        return conn;
    }

    private void DBClose(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}