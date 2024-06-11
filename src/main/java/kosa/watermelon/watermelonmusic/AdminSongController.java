package kosa.watermelon.watermelonmusic;


import java.sql.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminSongController implements Initializable {
    @FXML
    private TextField songName;

    @FXML
    private TextField artistName;

    @FXML
    private TextField albumName;

    @FXML
    private TextField songFile;

    @FXML
    private Button addButton;

    @FXML
    private Button logout_BTN;

    @FXML
    private Label focusLabel; // 관리자 곡 추가 텍스트필드에 커서 깜빡이지 않도록 수정

    private Member currentMember;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.currentMember = SessionManager.getInstance().getCurrentMember();
        if (currentMember != null) {
            System.out.println("AdminSongController: Member set with ID - " + currentMember.getId());
        } else {
            System.out.println("Error: currentMember is null.");
        }

        // TextField에 포커스를 제거하고 다른 곳으로 포커스를 설정
        Platform.runLater(() -> focusLabel.requestFocus());
    }

    private Connection getConnection() throws SQLException {
        return DBUtil.getConnection();
    }

    @FXML
    private void handleAddSong(ActionEvent event) {
        String sName = songName.getText();
        String aName = artistName.getText();
        String alName = albumName.getText();
        String sFile = songFile.getText();

        if (sName.isEmpty() || aName.isEmpty() || alName.isEmpty() || sFile.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "곡 추가 실패", "모든 필드를 입력하세요.");
            return;
        }

        Connection conn = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 아티스트 조회 또는 추가
            int artistId = getOrInsertArtist(conn, aName);

            // 앨범 조회 또는 추가
            int albumId = getOrInsertAlbum(conn, alName, artistId);

            // 곡 정보 삽입
            insertSong(conn, sName, artistId, albumId, sFile);

            conn.commit(); // 트랜잭션 커밋
            showAlert(Alert.AlertType.INFORMATION, "곡 추가 성공", "곡이 성공적으로 추가되었습니다.");
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // 트랜잭션 롤백
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "곡 추가 실패", "곡 추가 중 오류가 발생했습니다.");
        } finally {
            DBUtil.close(null, null, conn);
        }
    }

    private void insertSong(Connection conn, String songName, int artistId, int albumId, String songFile) throws SQLException {
        String insertSongSql = "INSERT INTO SONG (SONG_ID, SONG_NAME, ARTIST_ID, ALBUM_ID, CLICK_COUNT, SONG_FILE) VALUES (SONG_SEQ.NEXTVAL, ?, ?, ?, 0, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSongSql)) {
            pstmt.setString(1, songName);
            pstmt.setInt(2, artistId);
            pstmt.setInt(3, albumId);
            pstmt.setString(4, songFile);
            pstmt.executeUpdate();
        }
    }

    private int getOrInsertArtist(Connection conn, String artistName) throws SQLException {
        String query = "SELECT ARTIST_ID FROM ARTIST WHERE ARTIST_NAME = ?";
        
        // Debugging output
        System.out.println("Executing query: " + query + " with artistName: " + artistName);

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, artistName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ARTIST_ID");
                } else {
                    // Artist does not exist, insert new artist
                    String insertQuery = "INSERT INTO ARTIST (ARTIST_ID, ARTIST_NAME) VALUES (ARTIST_SEQ.NEXTVAL, ?)";
                    System.out.println("Inserting artist with name: " + artistName);
                    try (PreparedStatement insertPstmt = conn.prepareStatement(insertQuery, new String[]{"ARTIST_ID"})) {
                        insertPstmt.setString(1, artistName);
                        insertPstmt.executeUpdate();
                        try (ResultSet generatedKeys = insertPstmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                return generatedKeys.getInt(1);
                            } else {
                                throw new SQLException("Inserting artist failed, no ID obtained.");
                            }
                        }
                    }
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // 예외 발생 시 자세한 정보 출력
            System.err.println("SQLException occurred: " + e.getMessage());
            e.printStackTrace();  // 추가적인 스택 트레이스 출력

            // 모든 예외의 원인 (cause)을 출력
            Throwable cause = e.getCause();
            while (cause != null) {
                System.err.println("Caused by: " + cause.getMessage());
                cause = cause.getCause();
            }
            throw e;  // 예외 다시 던지기
        }
    }

    private int getOrInsertAlbum(Connection conn, String albumName, int artistId) throws SQLException {
        String selectAlbumSql = "SELECT ALBUM_ID FROM ALBUM WHERE ALBUM_NAME = ? AND ARTIST_ID = ?";
        int albumId = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(selectAlbumSql)) {
            pstmt.setString(1, albumName);
            pstmt.setInt(2, artistId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    albumId = rs.getInt("ALBUM_ID");
                }
            }
        }
        if (albumId == 0) {
            // 존재하지 않는 앨범이므로 새로 삽입
            albumId = insertAlbum(conn, albumName, artistId);
        }
        return albumId;
    }

    private int insertAlbum(Connection conn, String albumName, int artistId) throws SQLException {
        String insertAlbumSql = "INSERT INTO ALBUM (ALBUM_ID, ALBUM_NAME, ARTIST_ID) VALUES (ALBUM_SEQ.NEXTVAL, ?, ?)";
        int albumId = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(insertAlbumSql, new String[]{"ALBUM_ID"})) {
            pstmt.setString(1, albumName);
            pstmt.setInt(2, artistId);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    albumId = rs.getInt(1);
                }
            }
        }
        return albumId;
    }
    

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML // 로그아웃 이벤트 처리
    private void logout_Action(ActionEvent event) {
        // 세션 초기화
        SessionManager.getInstance().clearSession();

        // 로그인 창 열기
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);

            // 현재 Stage 찾기
            Stage currentStage = (Stage) logout_BTN.getScene().getWindow();

            // MainApplicatin의 Scene 설정
            Image icon = new Image(
	        		getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고 이미지 파일 경로 지정
            currentStage.getIcons().add(icon);
            currentStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}