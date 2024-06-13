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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * EditSongPlaylistController 클래스 : 편집된 곡의 플레이리스트를 관리하고 관련된 UI 이벤트를 처리함
 */
public class EditSongPlaylistController implements Initializable {

    @FXML private TableView<EditSongPlaylist> editSongPlaylistTableView;
    @FXML private TableColumn<EditSongPlaylist, Boolean> check;
    @FXML private TableColumn<EditSongPlaylist, String> songName;
    @FXML private TableColumn<EditSongPlaylist, String> artist;
    @FXML private TableColumn<EditSongPlaylist, Void> playBtn;
    @FXML private Button delete;
    @FXML private Button deleteAll;
    @FXML private Button goToDashboard;
    @FXML private Button playAllButton;
    private SessionManager sessionManager;
    private Member currentMember;
    private EditSongPlaylist editSongPlaylist;
    private final Map<EditSongPlaylist, Boolean> selectedSongs = new HashMap<>();

	/**
	 * 컨트롤러 초기화 메서드
	 * 
	 * @param url            URL 객체
	 * @param resourceBundle 리소스 번들 객체
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.currentMember = SessionManager.getInstance().getCurrentMember();
		delete.setOnAction(this::handleDeleteAction);
		deleteAll.setOnAction(this::handleDeleteAllAction);
		playAllButton.setOnAction(this::playAllSongs);
		sessionManager = SessionManager.getInstance();
	}

	/**
	 * 현재 멤버를 설정
	 * 
	 * @param member 현재 멤버 객체
	 */
	public void setMember(Member member) {
		this.currentMember = member;
		System.out.println("PlaylistController: Member set with ID - " + currentMember.getId());

		setListView();
	}

	/**
	 * 현재 플레이리스트를 설정
	 * 
	 * @param playlist 현재 플레이리스트 객체
	 */
	public void setPlaylist(EditSongPlaylist playlist) {
		this.editSongPlaylist = playlist;
		setListView();
	}

	/**
	 * 플레이리스트를 설정하고 테이블 뷰에 표시
	 */
	private void setListView() {
		if (currentMember == null) {
			System.out.println("Current member is null. Cannot load playlist.");
			return;
		}

		System.out.println("Loading playlist for member ID - " + currentMember.getId());

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<EditSongPlaylist> playlistSongs = new ArrayList<>();

		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement("SELECT e.editsong_id, e.song_id, e.song_name, e.song_file, a.artist_name "
					+ "FROM EditSong e " + "LEFT OUTER JOIN Song s ON e.song_id = s.song_id "
					+ "LEFT OUTER JOIN Artist a ON s.artist_id = a.artist_id " + "WHERE e.member_id=?");
			pstmt.setString(1, currentMember.getId());
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Long id = rs.getLong("editsong_id");
				Long songId = rs.getLong("song_id");
				String editSongName = rs.getString("song_name");
				String artistName = rs.getString("artist_name");
				String songFile = rs.getString("song_file");
				EditSongPlaylist song = new EditSongPlaylist(id, songId, editSongName, artistName, songFile);
				playlistSongs.add(song);
				selectedSongs.put(song, false);
			}
			ObservableList<EditSongPlaylist> playlist = FXCollections.observableArrayList(playlistSongs);
			songName.setCellValueFactory(new PropertyValueFactory<>("editSongName"));
			artist.setCellValueFactory(new PropertyValueFactory<>("artistName"));
			editSongPlaylistTableView.setItems(playlist);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt, rs, conn);
		}

		check.setCellValueFactory(data -> {
			EditSongPlaylist song = data.getValue();
			SimpleBooleanProperty property = new SimpleBooleanProperty(selectedSongs.getOrDefault(song, false));
			property.addListener((observable, oldValue, newValue) -> selectedSongs.put(song, newValue));
			return property;
		});

		check.setCellFactory(new Callback<>() {
			@Override
			public TableCell<EditSongPlaylist, Boolean> call(TableColumn<EditSongPlaylist, Boolean> param) {
				return new TableCell<>() {
					private final CheckBox checkBox = new CheckBox();

					{
						checkBox.setOnAction(event -> {
							EditSongPlaylist song = getTableView().getItems().get(getIndex());
							selectedSongs.put(song, checkBox.isSelected());
						});
					}

					@Override
					protected void updateItem(Boolean item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							EditSongPlaylist song = getTableView().getItems().get(getIndex());
							checkBox.setSelected(selectedSongs.getOrDefault(song, false));
							setGraphic(checkBox);
						}
					}
				};
			}
		});

		playBtn.setCellFactory(new Callback<>() {
			@Override
			public TableCell<EditSongPlaylist, Void> call(TableColumn<EditSongPlaylist, Void> param) {
				return new TableCell<>() {
					private final Button playButton = new Button();

					{
						Image btnImg = new Image(
								getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/playButton.png"));
						ImageView imageView = new ImageView(btnImg);
						imageView.setFitHeight(20);
						imageView.setFitWidth(20);
						playButton.setGraphic(imageView);
						playButton.setOnAction(event -> {
							EditSongPlaylist selectedSong = getTableView().getItems().get(getIndex());
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

		// Remove loadPlaylists() if it's not implemented
		// loadPlaylists();
	}

	/**
	 * 선택된 곡을 재생
	 * 
	 * @param selectedSong 선택된 곡
	 */
	private void playSelectedSong(EditSongPlaylist selectedSong) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("playview.fxml"));
			Parent parent = loader.load();

			PlayViewController controller = loader.getController();
			Queue<Long> songQueue = new ArrayDeque<>();
			songQueue.add(selectedSong.getEditId());
			controller.setMember(currentMember);
			controller.setSongQueue(songQueue, "EDIT");

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Playing Song");
			stage.setScene(new Scene(parent, 357, 432));
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 선택된 곡들을 삭제
	 * 
	 * @param event 이벤트 객체
	 */
	@FXML
	private void handleDeleteAction(ActionEvent event) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBUtil.getConnection();
			conn.setAutoCommit(false); // Start transaction

			for (Map.Entry<EditSongPlaylist, Boolean> entry : selectedSongs.entrySet()) {
				if (entry.getValue()) {
					EditSongPlaylist song = entry.getKey();
					pstmt = conn.prepareStatement("DELETE FROM EditSong WHERE editsong_id=? AND member_id=?");
					pstmt.setLong(1, song.getEditId());
					pstmt.setString(2, currentMember.getId());
					pstmt.executeUpdate();
				}
			}
			conn.commit(); // Commit transaction
			setListView(); // Refresh the list view after deletion
		} catch (Exception e) {
			if (conn != null) {
				try {
					conn.rollback(); // Rollback transaction on error
				} catch (Exception rollbackEx) {
					rollbackEx.printStackTrace();
				}
			}
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt, null, conn);
		}
	}

	/**
	 * 모든 곡을 삭제
	 * 
	 * @param event 이벤트 객체
	 */
	@FXML
	private void handleDeleteAllAction(ActionEvent event) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBUtil.getConnection();
			conn.setAutoCommit(false); // Start transaction

			pstmt = conn.prepareStatement("DELETE FROM EditSong WHERE member_id=?");
			pstmt.setString(1, currentMember.getId());
			pstmt.executeUpdate();

			conn.commit(); // Commit transaction
			setListView(); // Refresh the list view after deletion
		} catch (Exception e) {
			if (conn != null) {
				try {
					conn.rollback(); // Rollback transaction on error
				} catch (Exception rollbackEx) {
					rollbackEx.printStackTrace();
				}
			}
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt, null, conn);
		}
	}

	/**
	 * 대시보드로 이동하는 이벤트를 처리
	 * 
	 * @param event 이벤트 객체
	 */
	@FXML
	private void goToDashboard_Action(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
			Parent parent = loader.load();

			DashboardController controller = loader.getController();
			controller.setMember(currentMember);

			Stage newStage = new Stage();
			Stage currentStage = (Stage) goToDashboard.getScene().getWindow();

			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("메인 화면");
			newStage.setScene(new Scene(parent, 800, 600));
			newStage.show();
			Image icon = new Image(
					getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고
																													// 이미지
																													// 파일
																													// 경로
																													// 지정
			newStage.getIcons().add(icon);
			currentStage.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 모든 곡을 재생하는 이벤트를 처리
	 * 
	 * @param event 이벤트 객체
	 */
	@FXML
	private void playAllSongs(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("playview.fxml"));
			Parent parent = loader.load();

			PlayViewController controller = loader.getController();
			Queue<Long> songQueue = new ArrayDeque<>();

			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				conn = DBUtil.getConnection();
				pstmt = conn.prepareStatement("SELECT song_id FROM EditSong WHERE member_id=?");
				pstmt.setString(1, currentMember.getId());
				rs = pstmt.executeQuery();

				while (rs.next()) {
					songQueue.add(rs.getLong("song_id"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(pstmt, rs, conn);
			}

			controller.setSongQueue(songQueue, "EDIT");

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Playing All Songs");
			stage.setScene(new Scene(parent, 357, 432));
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
