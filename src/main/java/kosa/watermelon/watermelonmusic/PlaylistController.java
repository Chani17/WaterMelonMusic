package kosa.watermelon.watermelonmusic;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    @FXML private TableColumn<Song, Boolean> check;
    @FXML private TableColumn<PlaylistSong, String> songName;
    @FXML private TableColumn<PlaylistSong, String> artist;
    @FXML private TableColumn<Song, Void> playBtn;
    @FXML private Button delete;
    @FXML private Button deleteAll;
    @FXML private Button goToDashboard_BTN;
    private Member currentMember;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        delete.setOnAction(this::handleDeleteAction);
//        deleteAll.setOnAction(this::handDeleteAllAction);
    }

    public void setMember(Member member) {
        this.currentMember = member;
        setListView();
    }

    private void setListView() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<PlaylistSong> playlistSongs = new ArrayList<>();

        try {
        	conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement("SELECT s.song_name, a.artist_name \n" +
                    "FROM Playlist p, TABLE(p.song) song \n" +
                    "LEFT OUTER JOIN Song s ON song.COLUMN_VALUE = s.song_id \n" +
                    "LEFT OUTER JOIN Artist a ON s.artist_id = a.artist_id \n" +
                    "WHERE p.member_id=?");
            pstmt.setString(1, currentMember.getId());
            rs = pstmt.executeQuery();

            while(rs.next()) {
               String name = rs.getString("song_name");
               String artist = rs.getString("artist_name");
               System.out.println(name);
               System.out.println(artist);
               playlistSongs.add(new PlaylistSong(name, artist));
           }
            ObservableList<PlaylistSong> playlist = FXCollections.observableArrayList(playlistSongs);
            songName.setCellValueFactory(new PropertyValueFactory<PlaylistSong, String>("songName"));
            artist.setCellValueFactory(new PropertyValueFactory<PlaylistSong, String>("artistName"));
            playlistView.setItems(playlist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	DBUtil.close(pstmt, rs, conn);
        }

//        check.setCellValueFactory(data -> {
//            Song song = data.getValue();
//            return new ObservableValue<Boolean>() {
//                @Override
//                public void addListener(ChangeListener<? super Boolean> changeListener) {
//                    selectedSong.put(song, !selectedSong.get(song));
//                    System.out.println(selectedSong.get(song));
//                }
//
//                @Override
//                public void removeListener(ChangeListener<? super Boolean> changeListener) {
//                    selectedSong.put(song, !selectedSong.get(song));
//                    System.out.println(selectedSong.get(song));
//                }
//
//                @Override
//                public Boolean getValue() {
//                    return selectedSong.get(song);
//                }
//
//                @Override
//                public void addListener(InvalidationListener invalidationListener) {
//
//                }
//
//                @Override
//                public void removeListener(InvalidationListener invalidationListener) {
//
//                }
//            };
//        });

//        check.setCellFactory(new Callback<TableColumn<Song, Boolean>, TableCell<Song, Boolean>>() {
//            @Override
//            public TableCell<Song, Boolean> call(TableColumn<Song, Boolean> param) {
//                return new TableCell<Song, Boolean>() {
//                    private final CheckBox checkBox = new CheckBox();
//
//                    {
//                        checkBox.setOnAction(event -> {
//                            Song song = getTableView().getItems().get(getIndex());
//                            selectedSong.put(song, checkBox.isSelected());
//                        });
//                    }
//
//                    @Override
//                    protected void updateItem(Boolean item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (empty) {
//                            setGraphic(null);
//                        } else {
//                            Song song = getTableView().getItems().get(getIndex());
//                            checkBox.setSelected(selectedSong.get(song));
//                            setGraphic(checkBox);
//                        }
//                    }
//                };
//            }
//        });



//        playlistView.setItems(playlist);

        playBtn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Song, Void> call(TableColumn<Song, Void> param) {
                return new TableCell<>() {
                    private final Button playButton = new Button();
                    {
                        Image btnImg = new Image(getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/playButton.png"));
                        ImageView imageView = new ImageView(btnImg);
                        imageView.setFitHeight(20);
                        imageView.setFitWidth(20);
                        playButton.setGraphic(imageView);
                        playButton.setOnAction(event -> {
                            Song selectedSong = getTableView().getItems().get(getIndex());
                            System.out.println(selectedSong.getName() + "를 재생합니다.");
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

//        playlistView.setItems(songList);
    }

//    @FXML
//    private void handleDeleteAction(ActionEvent event) {
//        ObservableList<Song> songs = playlistView.getItems();
//        songs.removeIf(song -> selectedSong.get(song));
//        temporaryDB.getMyPlaylist().removeIf(song -> !songs.contains(song));
//        selectedSong.keySet().removeIf(song -> !songs.contains(song));
//        temporaryDB.updateMyPlaylist(songs);
//    }
//
//    @FXML
//    private void handDeleteAllAction(ActionEvent event) {
//        ObservableList<Song> songs = playlistView.getItems();
//
//        songs.clear();
//        selectedSong.clear();
//        temporaryDB.clearMyPlaylist();
//    }

    private List<Song> getMyPlaylist(String memberId) {
        try {
            Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            pstmt = conn.prepareStatement("SELECT * FROM Playlist WHERE member_id=?");
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            return (List<Song>) rs.getArray("song");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML // My Playlist → DashBoard 페이지 이동 이벤트 처리
	private void goToDashboard_Action(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
			Parent parent = loader.load();
			
			// DashboardController 인스턴스를 가져와서 멤버 설정
			DashboardController controller = loader.getController();
			controller.setMember(currentMember);
			
			Stage newStage = new Stage();
			Stage currentStage = (Stage) goToDashboard_BTN.getScene().getWindow();
			
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("메인 화면");
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
