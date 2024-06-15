package kosa.watermelon.watermelonmusic;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * PlaylistUserController 클래스 : 사용자 플레이리스트를 관리하는 컨트롤러 클래스
 */
public class PlaylistUserController implements Initializable {

	// FXML 필드
    @FXML private TableView<Playlist> playlistTable;
    @FXML private TableColumn<Playlist, Integer> numberColumn; // 플레이리스트 순번 컬럼
    @FXML private TableColumn<Playlist, Long> idColumn;
    @FXML private TableColumn<Playlist, String> nameColumn;
    @FXML private TableColumn<Playlist, String> memberColumn;
    @FXML private TableColumn<Playlist, String> playColumn; // 재생 버튼 컬럼
    @FXML private TableColumn<Playlist, String> deleteColumn; // 삭제 버튼 컬럼
    @FXML private Button deleteButton;
    @FXML private Button goToDashboard_BTN;
    
    private SessionManager sessionManager;
    private Member currentMember;
    private final Map<Playlist, Boolean> selectedPlaylist = new HashMap<>();

	/**
	 * 컨트롤러 초기화 메서드
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		numberColumn.setCellValueFactory(new PropertyValueFactory<>("number")); // 플레이리스트 순번 컬럼 설정
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("playlistName"));
		memberColumn.setCellValueFactory(new PropertyValueFactory<>("memberId"));

		playlistTable.setStyle("-fx-font-family: 'D2Coding'; -fx-font-size: 10pt;");

		// TableView의 각 행에 대한 폰트 설정
		playlistTable.setRowFactory(tv -> {
			TableRow<Playlist> row = new TableRow<>();
			row.setStyle("-fx-font-family: 'D2Coding'; -fx-font-size: 10pt;");
			return row;
		});

		sessionManager = SessionManager.getInstance();

		// 사용자가 로그인한 경우에만 플레이리스트를 로드합니다.
		if (sessionManager.getCurrentMember() != null) {
			loadPlaylists(sessionManager.getCurrentMember().getId());
		}

		// 행을 더블 클릭하면 상세 화면으로 이동
		playlistTable.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				Playlist selectedPlaylist = playlistTable.getSelectionModel().getSelectedItem();
				if (selectedPlaylist != null) {
					goToPlaylistDetail(selectedPlaylist);
				}
			}
		});

		deleteColumn.setCellValueFactory(data -> {
			Playlist playlist = data.getValue();
			SimpleBooleanProperty property = new SimpleBooleanProperty(selectedPlaylist.getOrDefault(playlist, false));
			property.addListener((observable, oldValue, newValue) -> selectedPlaylist.put(playlist, newValue));
			return property.asString();
		});

		deleteColumn.setCellFactory(new Callback<TableColumn<Playlist, String>, TableCell<Playlist, String>>() {
			@Override
			public TableCell<Playlist, String> call(TableColumn<Playlist, String> playlistStringTableColumn) {
				return new TableCell<>() {
					private final CheckBox checkBox = new CheckBox();

					{
						checkBox.setOnAction(event -> {
							Playlist playlist = getTableView().getItems().get(getIndex());
							selectedPlaylist.put(playlist, checkBox.isSelected());
						});
					}

					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							Playlist playlist = getTableView().getItems().get(getIndex());
							checkBox.setSelected(selectedPlaylist.getOrDefault(playlist, false));
							setGraphic(checkBox);
						}
					}
				};
			}
		});

		// 삭제 버튼 클릭 이벤트 설정
		deleteButton.setOnAction(this::deletePlaylist);

		playColumn.setCellFactory(new Callback<TableColumn<Playlist, String>, TableCell<Playlist, String>>() {
			@Override
			public TableCell<Playlist, String> call(TableColumn<Playlist, String> playlistStringTableColumn) {
				return new TableCell<>() {
					private final Button playButton = new Button("▶");
					{
						playButton.setOnAction(event -> {
							Playlist selectedPlaylist = getTableView().getItems().get(getIndex());
							List<Long> songList = new ArrayList<>();

							try {
								Connection conn = DBUtil.getConnection();
								PreparedStatement pstmt = conn.prepareStatement(
										"SELECT song.COLUMN_VALUE FROM Playlist p, TABLE(p.song) song WHERE p.playlist_id=?");
								pstmt.setLong(1, selectedPlaylist.getPlaylistId());
								ResultSet rs = pstmt.executeQuery();

								while (rs.next()) {
									BigDecimal songId = rs.getBigDecimal("COLUMN_VALUE");
									System.out.println(songId.longValue());
									songList.add(songId.longValue());
								}
								DBUtil.close(conn, pstmt, rs);
							} catch (Exception e) {
								e.printStackTrace();
							}
							System.out.println(songList.size());
							playSongs(songList);
						});
					}

					@Override
					protected void updateItem(String item, boolean empty) {
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
		deleteButton.setOnAction(this::deletePlaylist);
	}

	/**
	 * DashBoard 페이지 이동 이벤트 처리 메서드
	 */
	@FXML
	private void goToDashboard_Action(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
			Parent parent = loader.load();

			Stage newStage = new Stage();
			Stage currentStage = (Stage) goToDashboard_BTN.getScene().getWindow();

			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("메인 화면");
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

	/**
	 * 곡을 재생하는 메서드
	 *
	 * @param songIds 재생할 곡 ID 목록
	 */
	private void playSongs(List<Long> songIds) {
		if (songIds == null || songIds.isEmpty()) {
			System.out.println("No songs to play.");
			return;
		}

		try {
			// PlayView.fxml을 로드
			FXMLLoader loader = new FXMLLoader(getClass().getResource("playview.fxml"));
			Parent parent = loader.load();

			// PlayViewController 인스턴스를 가져와서 재생할 노래 목록을 설정
			PlayViewController playViewController = loader.getController();
			playViewController.setSongQueue(new ArrayDeque<>(songIds), "SONG");

			// 새로운 스테이지를 생성하고 PlayView.fxml을 설정
			Stage stage = new Stage();
			stage.setTitle("플레이리스트 재생");
			stage.setScene(new Scene(parent));
			Image icon = new Image(
					getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고
																													// 이미지
																													// 파일
																													// 경로
																													// 지정
			stage.getIcons().add(icon);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 선택된 플레이리스트를 삭제하는 메서드
	 */
	@FXML
	private void deletePlaylist(ActionEvent event) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBUtil.getConnection();
			for (Map.Entry<Playlist, Boolean> entry : selectedPlaylist.entrySet()) {
				if (entry.getValue()) {
					Playlist playlist = entry.getKey();
					pstmt = conn.prepareStatement("DELETE FROM PLAYLIST WHERE PLAYLIST_ID = ? AND MEMBER_ID = ?");
					pstmt.setLong(1, playlist.getPlaylistId());
					pstmt.setString(2, currentMember.getId());
					pstmt.executeUpdate();
				}
			}
			loadPlaylists(currentMember.getId()); // 삭제 후 리스트 새로 고침
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt, null, conn);
		}
	}

	/**
	 * 현재 로그인된 사용자를 설정하는 메서드
	 *
	 * @param member 로그인된 사용자
	 */
	public void setMember(Member member) {
		this.currentMember = member;
		if (this.currentMember == null) {
			System.out.println("PlaylistUserController: setMember called with null member");
		} else {
			System.out.println("PlaylistUserController: Member set with ID - " + currentMember.getId());
			loadPlaylists(sessionManager.getCurrentMember().getId());
		}
	}

	/**
	 * 플레이리스트를 로드하여 TableView에 설정하는 메서드
	 *
	 * @param memberId 사용자 ID
	 */
	private void loadPlaylists(String memberId) {
		ObservableList<Playlist> playlists = FXCollections.observableArrayList();

		// 데이터베이스에서 특정 사용자가 만든 플레이리스트를 가져오는 쿼리를 실행
		String query = "SELECT PLAYLIST_ID, PLAYLIST_NAME, MEMBER_ID FROM PLAYLIST WHERE MEMBER_ID = ?";
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, memberId);
			try (ResultSet resultSet = statement.executeQuery()) {
				// 결과 반복문으로 처리
				int number = 1; // 순번 초기화
				while (resultSet.next()) {
					Long playlistId = resultSet.getLong("PLAYLIST_ID");
					String playlistName = resultSet.getString("PLAYLIST_NAME");
					String member = resultSet.getString("MEMBER_ID");

					// Playlist 객체 생성 후 ObservableList에 추가
					Playlist playlist = new Playlist(playlistId, playlistName, new ArrayList<>(), member, number,
							LocalDate.now());
					number++; // 순번 증가
					playlists.add(playlist);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// TableView에 데이터 설정
		playlistTable.setItems(playlists);
	}

	/**
	 * 선택된 플레이리스트의 상세 화면으로 이동하는 메서드
	 *
	 * @param playlist 선택된 플레이리스트
	 */
	private void goToPlaylistDetail(Playlist playlist) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("playlist.fxml"));
			Parent parent = loader.load();

			// PlaylistController 인스턴스를 가져와서 플레이리스트 설정
			PlaylistController controller = loader.getController();
			controller.setMember(currentMember);
			controller.setPlaylist(playlist);
			Playlist selectedPlaylist = playlistTable.getSelectionModel().getSelectedItem();
			Stage newStage = new Stage();
			Stage currentStage = (Stage) goToDashboard_BTN.getScene().getWindow();
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("플레이리스트 상세 화면 - " + selectedPlaylist.getPlaylistName());
			newStage.setScene(new Scene(parent, 800, 600));
			Image icon = new Image(
					getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고
			newStage.getIcons().add(icon);
			newStage.show();
			currentStage.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
