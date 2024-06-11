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
import java.math.BigDecimal;
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
    private final Map<PlaylistSong, Boolean> selectedSongs = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        delete.setOnAction(this::handleDeleteAction);
        deleteAll.setOnAction(this::handleDeleteAllAction);
        sessionManager = SessionManager.getInstance();
        
        playlistView.setStyle("-fx-font-family: 'D2Coding'; -fx-font-size: 10pt;");

		// TableView의 각 행에 대한 폰트 설정
        playlistView.setRowFactory(tv -> {
			TableRow<PlaylistSong> row = new TableRow<>();
			row.setStyle("-fx-font-family: 'D2Coding'; -fx-font-size: 10pt;");
			return row;
		});
    }

    public void setMember(Member member) {
        this.currentMember = member;
        System.out.println("PlaylistController: Member set with ID - " + currentMember.getId());
        setListView();
    }
    
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        System.out.println("PlaylistController: Playlist set with ID - " + playlist.getPlaylistId());
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

        System.out.println("Loading playlist for member ID - " + currentMember.getId() + " and playlist ID - " + playlist.getPlaylistId());
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<PlaylistSong> playlistSongs = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement("SELECT s.song_id, s.song_name, a.artist_name " +
                    "FROM Playlist p, TABLE(p.song) song " +
                    "LEFT OUTER JOIN Song s ON song.COLUMN_VALUE = s.song_id " +
                    "LEFT OUTER JOIN Artist a ON s.artist_id = a.artist_id " +
                    "WHERE p.playlist_id=?");
            pstmt.setLong(1, playlist.getPlaylistId());
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
                            playSelectedSong(selectedSong);
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

//        loadPlaylists();
    }

//    private void loadPlaylists() {
//        // Implement loading logic for playlists and set items to playlistView
//    }

    private void playSelectedSong(PlaylistSong selectedSong) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("playview.fxml"));
            Parent parent = loader.load();

            PlayViewController controller = loader.getController();
            Queue<Long> songQueue = new ArrayDeque<>();
            songQueue.add(selectedSong.getSongId()); // Assuming PlaylistSong has getSongId method
            controller.setSongQueue(songQueue, "SONG");
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(selectedSong.getSongName() + " - " + selectedSong.getArtistName());
            Image icon = new Image(
	        		getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고 이미지 파일 경로 지정
			stage.getIcons().add(icon);
            stage.setScene(new Scene(parent, 357, 432));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    System.out.println("result = " + song.getSongId());

                    // Fetch the current SONG_ARRAY for the playlist
                    pstmt = conn.prepareStatement("SELECT p.SONG FROM Playlist p WHERE p.playlist_id=? AND p.member_id=?");
                    pstmt.setLong(1, playlist.getPlaylistId());
                    pstmt.setString(2, currentMember.getId());

                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        Array songArray = rs.getArray("SONG");
                        BigDecimal[] songIds = (BigDecimal[]) songArray.getArray();
                        List<Long> songList = new ArrayList<>();

                        for (BigDecimal id : songIds) {
                            songList.add(id.longValue());
                        }

                        // Remove the song ID from the array
                        songList.removeIf(id -> Objects.equals(id, song.getSongId()));

                        // Update the playlist with the modified SONG_ARRAY
                        StringBuilder updateQuery = new StringBuilder("UPDATE PLAYLIST SET SONG = SONG_ARRAY(");
                        for (int i = 0; i < songList.size(); i++) {
                            if (i > 0) {
                                updateQuery.append(", ");
                            }
                            updateQuery.append(songList.get(i));
                        }
                        updateQuery.append(") WHERE MEMBER_ID = ? AND PLAYLIST_ID = ?");

                        pstmt = conn.prepareStatement(updateQuery.toString());
                        pstmt.setString(1, currentMember.getId());
                        pstmt.setLong(2, playlist.getPlaylistId());
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
            pstmt = conn.prepareStatement("DELETE FROM Playlist WHERE playlist_id=?");
            pstmt.setLong(1, playlist.getPlaylistId());
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
