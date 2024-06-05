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
    private MediaPlayer mediaPlayer;
    private long songId;

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

        try {
            pstmt_song = conn.prepareStatement("SELECT * FROM Song WHERE song_id=?");
            pstmt_album = conn.prepareStatement("SELECT album_cover FROM Album WHERE album_id=?");
            pstmt_song.setLong(1, this.songId);
            res_song = pstmt_song.executeQuery();

            if(res_song.next()) {
                String songName = res_song.getString("song_name");
                String artist_id = res_song.getString("artist_id");
                songTitle.setText(songName);
                artist.setText(artist_id);

                pstmt_album.setLong(1, res_song.getLong("album_id"));
                res_album = pstmt_album.executeQuery();

                if (res_album.next()) {
                    BFILE bfile = ((OracleResultSet) res_album).getBFILE("album_cover");
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
                    PlaylistView playlistView = new PlaylistView(songName, artist_id, imageData);

                    // 이미지 데이터를 이용하여 Image 객체 생성
                    Image image = new Image(new ByteArrayInputStream(playlistView.getAlbumCover()));

                    // ImageView에 이미지 설정
                    albumCover.setImage(image);
                } else {
                    System.out.println("Album not found");
                }

                String songFilePath = res_song.getString("song_file");

                // 미디어 등록
                Media media = new Media(new File(songFilePath).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setAutoPlay(true);
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
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
