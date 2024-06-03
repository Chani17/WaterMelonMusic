package kosa.watermelon.watermelonmusic;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class PlayViewController implements Initializable {

    private final static String id = "admin";
    private final static String pw = "1234";
    private final static String url = "jdbc:oracle:thin:@localhost:1521:xe";

    @FXML private Label songTitle;
    @FXML private Label artist;
    @FXML private Button playButton;
    @FXML private Button stopButton;
    @FXML private Button pauseButton;
    @FXML private ImageView albumCover;
    private MediaPlayer mediaPlayer;
    private long songId;
    private boolean endOfMedia;             // 재생 완료를 확인하는 플래그 필드

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playButton.setOnAction(event -> playSong());
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

        try{
            pstmt_song = conn.prepareStatement("SELECT * FROM Song WHERE song_id=?");
            pstmt_album = conn.prepareStatement("SELECT album_cover FROM Album WHERE album_id=?");
            pstmt_song.setLong(1, this.songId);
            res_song = pstmt_song.executeQuery();

            if(res_song.next()) {
                songTitle.setText(res_song.getString("song_name"));
                artist.setText(res_song.getString("artist_id"));

                pstmt_album.setLong(1, res_song.getLong("album_id"));
                res_album = pstmt_album.executeQuery();

                if(res_album.next()) {
                    Blob blob = res_album.getBlob("album_cover");
//                    System.out.println("Album Name: " + res_album.getString("album_name"));
                    System.out.println("Blob: " + blob);
                    if(blob != null) {
                        // Blob -> byte -> image
                        byte[] imageData = blob.getBytes(1, (int)blob.length());
                        if(imageData.length > 0) {
                            Image image = new Image(new ByteArrayInputStream(imageData));
                            Platform.runLater(() -> albumCover.setImage(image));
                        } else {
                            System.out.println("Image data is empty");
                        }
                    } else {
                        System.out.println("Blob is null");
                    }
                }

                String songFilePath = res_song.getString("song_file");

                // 미디어 등록
                Media media = new Media(new File(songFilePath).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setAutoPlay(true);

        } else {
            System.out.println("No song found with id = " + this.songId);
        }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                // 리소스 정리
                if(res_song != null) res_song.close();
                if(res_album != null) res_album.close();
                if(pstmt_song != null) pstmt_song.close();
                if(pstmt_album != null) pstmt_album.close();
                DBClose(conn);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

        }
    }

    private void playSong() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    private void pauseSong() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    private void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
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
            System.out.println("Sucess");
        } catch (SQLException e) {
            System.err.println("Fail");
            System.exit(0);
        }
        return conn;
    }

    private void DBClose(Connection conn) {
        try {
            if(conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
