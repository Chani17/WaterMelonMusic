package kosa.watermelon.watermelonmusic;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class PlaylistController implements Initializable {

    @FXML private TableView<PlaylistSong> playlistView;
    @FXML private TableColumn<PlaylistSong, Boolean> check;
    @FXML private TableColumn<PlaylistSong, String> songName;
    @FXML private TableColumn<PlaylistSong, String> artist;
    @FXML private TableColumn<PlaylistSong, Void> playBtn;
    @FXML private Button delete;
    @FXML private Button deleteAll;
    @FXML private Button goToPlaylistUser_BTN;
    @FXML private Label playlistName_Label;
    
    private SessionManager sessionManager;
	  private Member currentMember;
    private Playlist playlist;
    
    
    @FXML private Button goToDashboard_BTN;
    private Member currentMember;
    private final Map<PlaylistSong, Boolean> selectedSongs = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        delete.setOnAction(this::handleDeleteAction);
        deleteAll.setOnAction(this::handleDeleteAllAction);
        sessionManager = SessionManager.getInstance();
    }

    public void setMember(Member member) {
        this.currentMember = member;
        System.out.println("PlaylistController: Member set with ID - " + currentMember.getId());
        setListView();
    }
    
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        System.out.println("PlaylistController: Playlist set with ID - " + playlist.getPlaylistID());
        setListView();
        
        // Playlist 이름을 Label에 설정
        if (playlist != null) {
        	playlistName_Label.setText(playlist.getPlaylistName());
        }
    }
    
    private void setListView() {
        if (currentMember == null || playlist == null) {
            System.out.println("Current member or playlist is null. Cannot load playlist.");
            return;
        }

        System.out.println("Loading playlist for member ID - " + currentMember.getId() + " and playlist ID - " + playlist.getPlaylistID());
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<PlaylistSong> playlistSongs = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement("SELECT s.song_id, s.song_name, a.artist_name, p.playlist_id" +
                    "FROM Playlist p, TABLE(p.song) song " +
                    "LEFT OUTER JOIN Song s ON song.COLUMN_VALUE = s.song_id " +
                    "LEFT OUTER JOIN Artist a ON s.artist_id = a.artist_id " +
                    "WHERE p.member_id=?");
            pstmt.setString(1, currentMember.getId());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Long id = rs.getLong("song_id");
                String name = rs.getString("song_name");
                String artistName = rs.getString("artist_name");
                PlaylistSong playlistSong = new PlaylistSong(id, name, artistName);
                playlistSongs.add(playlistSong);
                selectedSongs.put(playlistSong, false);
            }
            ObservableList<PlaylistSong> playlist = FXCollections.observableArrayList(playlistSongs);
            songName.setCellValueFactory(new PropertyValueFactory<>("songName"));
            artist.setCellValueFactory(new PropertyValueFactory<>("artistName"));
            playlistView.setItems(playlist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(pstmt, rs, conn);
        }

        check.setCellValueFactory(data -> {
            PlaylistSong song = data.getValue();
            SimpleBooleanProperty property = new SimpleBooleanProperty(selectedSongs.get(song));
            property.addListener((observable, oldValue, newValue) -> selectedSongs.put(song, newValue));
            return property;
        });

        check.setCellFactory(new Callback<>() {
            @Override
            public TableCell<PlaylistSong, Boolean> call(TableColumn<PlaylistSong, Boolean> param) {
                return new TableCell<>() {
                    private final CheckBox checkBox = new CheckBox();

                    {
                        checkBox.setOnAction(event -> {
                            PlaylistSong song = getTableView().getItems().get(getIndex());
                            selectedSongs.put(song, checkBox.isSelected());
                        });
                    }

                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            PlaylistSong song = getTableView().getItems().get(getIndex());
                            checkBox.setSelected(selectedSongs.get(song));
                            setGraphic(checkBox);
                        }
                    }
                };
            }
        });

        playBtn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<PlaylistSong, Void> call(TableColumn<PlaylistSong, Void> param) {
                return new TableCell<>() {
                    private final Button playButton = new Button();

                    {
                        Image btnImg = new Image(getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/playButton.png"));
                        ImageView imageView = new ImageView(btnImg);
                        imageView.setFitHeight(20);
                        imageView.setFitWidth(20);
                        playButton.setGraphic(imageView);
                        playButton.setOnAction(event -> {
                            PlaylistSong selectedSong = getTableView().getItems().get(getIndex());
                            System.out.println(selectedSong.getSongName() + "를 재생합니다.");
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(playButton);
                        }
                    }
                };
            }
        });

        playlistView.setItems(FXCollections.observableArrayList(playlistSongs));
    }

    @FXML
    private void handleDeleteAction(ActionEvent event) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            for (Map.Entry<PlaylistSong, Boolean> entry : selectedSongs.entrySet()) {
                if (entry.getValue()) {
                    PlaylistSong song = entry.getKey();
                    System.out.println("result = " + song.getPlaylistId());

                    // Fetch the current SONG_ARRAY for the playlist
                    pstmt = conn.prepareStatement("SELECT SONG FROM PLAYLIST WHERE MEMBER_ID = ? AND PLAYLIST_ID = ?");
                    pstmt.setString(1, currentMember.getId());
                    pstmt.setLong(2, song.getPlaylistId());
                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        Array songArray = rs.getArray("SONG");
                        Long[] songIds = (Long[]) songArray.getArray();
                        List<Long> songList = new ArrayList<>(Arrays.asList(songIds));

                        // Remove the song ID from the array
                        songList.removeIf(id -> id == song.getPlaylistId());

                        // Update the playlist with the modified SONG_ARRAY
                        Integer[] updatedSongArray = songList.toArray(new Integer[0]);
                        Array updatedArray = conn.createArrayOf("NUMBER", updatedSongArray);

                        pstmt = conn.prepareStatement("UPDATE PLAYLIST SET SONG = ? WHERE MEMBER_ID = ? AND PLAYLIST_ID = ?");
                        pstmt.setArray(1, updatedArray);
                        pstmt.setString(2, currentMember.getId());
                        pstmt.setLong(3, song.getPlaylistId());
                        pstmt.executeUpdate();
                    }
                }
            }
            setListView(); // Refresh the list view after deletion
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(pstmt, rs, conn);
        }
    }

    @FXML
    private void handleDeleteAllAction(ActionEvent event) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement("DELETE FROM Playlist WHERE member_id=?");
            pstmt.setString(1, currentMember.getId());
            pstmt.executeUpdate();
            setListView(); // Refresh the list view after deletion
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(pstmt, null, conn);
        }
    }

  @FXML // My Playlist → PlaylistUser 페이지 이동 이벤트 처리
	private void goToPlaylistUser_Action(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("playlistUser.fxml"));
			Parent parent = loader.load();
			
			// PlaylistUserController 인스턴스를 가져와서 멤버 설정
	        PlaylistUserController controller = loader.getController();
	        controller.setMember(currentMember);
			
			Stage newStage = new Stage();
			Stage currentStage = (Stage) goToPlaylistUser_BTN.getScene().getWindow();
			
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("플레이리스트");
			newStage.setScene(new Scene(parent, 800, 600));
			Image icon = new Image(
	        		getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고 이미지 파일 경로 지정
			newStage.getIcons().add(icon);
			newStage.show();
			currentStage.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
