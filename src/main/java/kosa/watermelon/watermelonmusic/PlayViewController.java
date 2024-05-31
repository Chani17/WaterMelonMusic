package kosa.watermelon.watermelonmusic;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class PlayViewController implements Initializable {

    private final static String id = "";
    private final static String pw = "";
    private final static String url = "";

    @FXML private Label songTitle;
    @FXML private Label artist;
    @FXML private Button playButton;
    @FXML private Button stopButton;
    @FXML private Button pauseButton;
    private MediaPlayer mediaPlayer;
    private int songId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playButton.setOnAction(event -> playSong());
        stopButton.setOnAction(event -> stopSong());
        pauseButton.setOnAction(event -> pauseSong());
    }

    public void setSongId(int id) {
        this.songId = id;
        setPlayView();
    }

    private void setPlayView() {
        Connection conn = DBConnection();
        PreparedStatement pstmt = null;
        ResultSet res = null;

        try{
            pstmt = conn.prepareStatement("SELECT * FROM Song WHERE song_id=?");
            pstmt.setLong(1, this.songId);
            res = pstmt.executeQuery();

            if(res.next()) {
                songTitle.setText(res.getString("song_name"));
                artist.setText(res.getString("artist_id"));

                String songFilePath = res.getString("song_file");

                // JavaFX MediaPlayer를 사용하여 음악 재생
                Media media = new Media(new File(songFilePath).toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();
        } else {
            System.out.println("No song found with id = " + this.songId);
        }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                // 리소스 정리
                res.close();
                pstmt.close();
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
