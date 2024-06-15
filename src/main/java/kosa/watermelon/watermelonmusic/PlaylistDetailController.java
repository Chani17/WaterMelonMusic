package kosa.watermelon.watermelonmusic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.xml.transform.Result;

/**
 * PlatlistDetailController 클래스 : 플레이리스트 세부 정보를 보여주는 컨트롤러 클래스
 * 
 * 작성자 : 김효정
 */
public class PlaylistDetailController {

    // FXML 필드
    @FXML
    private TableView<Song> songTableView;
    @FXML
    private TableColumn<Song, String> songNameColumn;
    @FXML
    private TableColumn<Song, String> artistNameColumn;
    @FXML
    private TableColumn<Song, Void> playBTNColumn;
    @FXML
    private Button goToPostingPage_BTN;
    @FXML
    private Button delete;
    @FXML
    private Button deleteAll;
    @FXML
    private Label playlistName_Label;
    @FXML
    private Label playlistOwner_Label;

    private Member currentMember;
    private ObservableList<Song> songs;
    private final Map<PlaylistSong, Boolean> selectedSongs = new HashMap<>();
    private Playlist playlist;
    private long playlistId;

    /**
     * 초기화 메서드
     */
    @FXML
    public void initialize() {
        songs = FXCollections.observableArrayList();
        songNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        artistNameColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        songTableView.setItems(songs);

        // Playlist 이름과 소유자 Label에 초기 값을 설정
        playlistName_Label.setText("Default Playlist Name");
        playlistOwner_Label.setText("Default Owner Name");

        // 세션 매니저를 사용하여 currentMember를 설정
        this.currentMember = SessionManager.getInstance().getCurrentMember();
        if (this.currentMember != null) {
            System.out.println("PlaylistDetailController: Member set with ID - " + currentMember.getId());
        } else {
            System.out.println("PlaylistDetailController: No member set");
        }

        songTableView.setStyle("-fx-font-family: 'D2Coding'; -fx-font-size: 10pt;");

        // TableView의 각 행에 대한 폰트 설정
        songTableView.setRowFactory(tv -> {
            TableRow<Song> row = new TableRow<>();
            row.setStyle("-fx-font-family: 'D2Coding'; -fx-font-size: 10pt;");
            return row;
        });

        // play button 클릭 시 음악 play
        playBTNColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Song, Void> call(TableColumn<Song, Void> param) {
                return new TableCell<>() {
                    private final Button playButton = new Button("▶");

                    {
                        // 버튼 클릭 시 이벤트 처리
                        playButton.setOnAction(event -> {
                            Song selectedSong = getTableView().getItems().get(getIndex());
                            selectedSong.setClickCnt();

                            try {
                                Connection conn = DBUtil.getConnection();
                                PreparedStatement pstmt = conn
                                        .prepareStatement("SELECT click_count FROM Song WHERE song_id=?");
                                pstmt.setLong(1, selectedSong.getId());
                                ResultSet rs = pstmt.executeQuery();

                                if (rs.next()) {
                                    PreparedStatement updatePstmt = conn
                                            .prepareStatement("UPDATE Song SET click_count=? WHERE song_id=?");
                                    updatePstmt.setInt(1, rs.getInt("click_count") + 1);
                                    updatePstmt.setLong(2, selectedSong.getId());
                                    updatePstmt.executeUpdate();
                                }

                                Stage newStage = new Stage();
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("playview.fxml"));
                                Parent playView = loader.load();
                                PlayViewController controller = loader.getController();
                                controller.setSongId(selectedSong.getId());
                                Scene scene = new Scene(playView);

                                newStage.setTitle(selectedSong.getName() + " - " + selectedSong.getArtist());
                                Image icon = new Image(getClass().getResourceAsStream(
                                        "/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고 이미지 파일 경로
                                newStage.getIcons().add(icon);
                                newStage.setScene(scene);
                                newStage.showAndWait();

                                DBUtil.close(conn, pstmt, rs);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        Font font = Font.font("D2Coding Bold", 18);
                        playButton.setFont(font);
                    }

                    // 셸 Rendering
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty)
                            setGraphic(null);
                        else {
                            setGraphic(playButton);
                            setAlignment(Pos.CENTER);
                        }
                    }
                };
            }
        });
    }

    /**
     * 플레이리스트 ID 설정 메서드
     *
     * @param playlistId 플레이리스트 ID
     */
    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // 데이터베이스에서 플레이리스트 정보와 소유자 ID를 가져옴
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement("SELECT p.PLAYLIST_NAME, p.MEMBER_ID, m.NICKNAME "
                    + "FROM PLAYLIST p JOIN MEMBER m ON p.MEMBER_ID = m.MEMBER_ID " + "WHERE p.PLAYLIST_ID = ?");
            pstmt.setLong(1, playlistId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                // 플레이리스트와 소유자 정보를 가져와서 설정함
                String playlistName = rs.getString("PLAYLIST_NAME");
                String ownerId = rs.getString("MEMBER_ID");
                String ownerNickname = rs.getString("NICKNAME");

                // 플레이리스트 이름과 소유자 정보를 설정함
                playlistName_Label.setText(playlistName);
                playlistOwner_Label.setText(ownerNickname);

                // 현재 멤버의 ID와 플레이리스트의 소유자 ID가 다르더라도 플레이리스트를 설정함
                // 소유자의 플레이리스트를 확인할 수 있는 권한이 있음
                loadSongsFromDatabase();
            } else {
                System.out.println("PlaylistDetailController: Playlist not found for ID - " + playlistId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 데이터베이스에서 노래를 로드하는 메서드
     */
    private void loadSongsFromDatabase() {
        String sql = "SELECT s.SONG_ID, s.SONG_NAME, a.ARTIST_NAME, s.CLICK_COUNT "
                + "FROM SONG s JOIN ARTIST a ON s.ARTIST_ID = a.ARTIST_ID "
                + "JOIN TABLE (SELECT SONG FROM PLAYLIST WHERE PLAYLIST_ID = ?) p ON s.SONG_ID = p.COLUMN_VALUE";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, playlistId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                    long songId = rs.getLong("SONG_ID");
                    String songName = rs.getString("SONG_NAME");
                    String artistName = rs.getString("ARTIST_NAME");
                    long clickCount = rs.getLong("CLICK_COUNT");

                    Song song = new Song(0, songId, songName, artistName, clickCount);
                    songs.add(song);
                }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 멤버 설정 메서드
     *
     * @param member 멤버 객체
     */
    public void setMember(Member member) {
        this.currentMember = member;
        setListView();
    }

    /**
     * 리스트뷰 설정 메서드
     */
    private void setListView() {
        if (currentMember == null || playlist == null) {
            System.out.println("Current member or playlist is null. Cannot load playlist.");
            return;
        }

        System.out.println("Loading playlist for member ID - " + currentMember.getId() + " and playlist ID - "
                + playlist.getPlaylistId());

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Song> playlistSongs = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(
                    "SELECT s.song_id, s.song_name, a.artist_name " + "FROM Playlist p, TABLE(p.song) song "
                            + "LEFT OUTER JOIN Song s ON song.COLUMN_VALUE = s.song_id "
                            + "LEFT OUTER JOIN Artist a ON s.artist_id = a.artist_id " + "WHERE p.playlist_id=?");
            pstmt.setLong(1, playlist.getPlaylistId());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Long id = rs.getLong("song_id");
                String name = rs.getString("song_name");
                String artistName = rs.getString("artist_name");
                PlaylistSong playlistSong = new PlaylistSong(id, name, artistName);
                selectedSongs.put(playlistSong, false);
            }
            ObservableList<Song> playlist = FXCollections.observableArrayList(playlistSongs);
            songNameColumn.setCellValueFactory(new PropertyValueFactory<>("songName"));
            artistNameColumn.setCellValueFactory(new PropertyValueFactory<>("artistName"));
            songTableView.setItems(playlist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(pstmt, rs, conn);
        }

        songTableView.setItems(FXCollections.observableArrayList(playlistSongs));
    }

    @FXML // 포스팅된 플레이리스트 디테일 페이지 → Posting 페이지 이동 이벤트 처리
    private void goToPostingPage_Action(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("postingPage.fxml"));
            Parent parent = loader.load();

            PostingPageController controller = loader.getController();
            controller.setMember(currentMember);

            Stage newStage = new Stage();
            Stage currentStage = (Stage) goToPostingPage_BTN.getScene().getWindow();

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setTitle("포스팅 페이지");
            newStage.setScene(new Scene(parent, 800, 600));
            Image icon = new Image(
                    getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png"));
            newStage.getIcons().add(icon);
            newStage.show();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}