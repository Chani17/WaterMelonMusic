package kosa.watermelon.watermelonmusic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import oracle.jdbc.OracleResultSet;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.BFILE;

public class SongChartController implements Initializable {

	@FXML
	private TableView<Song> tableView;

	@FXML
	private TableColumn<Song, Integer> ranking;

	@FXML
	private TableColumn<Song, String> songName;

	@FXML
	private TableColumn<Song, String> artistName;

	@FXML
	private TableColumn<Song, Void> playBtn;

	@FXML
	private TableColumn<Song, Void> addBtn;

	@FXML
	private TableColumn<Song, Void> likeBtn;

	@FXML private TableColumn<Song, Void> editBtn;

	@FXML
	private Button goToDashboard_BTN;

	@FXML
	private GridPane root;

	@FXML
	private HBox searchContainer;

	private Member currentMember;

	// private Playlist playlist;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.currentMember = SessionManager.getInstance().getCurrentMember();
		if (currentMember != null) {
			System.out.println("SongChartController: Member set with ID - " + currentMember.getId());
		} else {
			System.out.println("Error: currentMember is null.");
		}

		ranking.setCellValueFactory(new PropertyValueFactory<>("id"));
		songName.setCellValueFactory(new PropertyValueFactory<>("name"));
		artistName.setCellValueFactory(new PropertyValueFactory<>("artistName"));

		tableView.setStyle("-fx-font-family: 'D2Coding'; -fx-font-size: 10pt;");

		// TableView의 각 행에 대한 폰트 설정
		tableView.setRowFactory(tv -> {
			TableRow<Song> row = new TableRow<>();
			row.setStyle("-fx-font-family: 'D2Coding'; -fx-font-size: 10pt;");
			return row;
		});
		
		
		// 검색 컴포넌트 로드
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("search.fxml"));
			HBox searchBox = loader.load();
			SearchController searchController = loader.getController();
			searchController.setTableView(tableView);
			searchContainer.getChildren().add(searchBox);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setListView();
	}

	public void setMember(Member member) {
		this.currentMember = member;
	}

	@FXML // 인기차트 → DashBoard 페이지 이동 이벤트 처리
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

	private void setListView() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Song> songs = new ArrayList<>();

		try {
			// DBUtil 클래스를 사용하여 데이터베이스 연결
      conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(
				"SELECT " +
		        "ROW_NUMBER() OVER (ORDER BY s.click_count DESC) AS ranking, " +
		        "s.song_id, a.artist_name, s.song_name, s.click_count " +
		        "FROM Song s " +
		        "LEFT OUTER JOIN Artist a " +
		        "ON s.artist_id = a.artist_id " +
		        "ORDER BY s.click_count DESC, s.song_name ASC"
		    );

			rs = pstmt.executeQuery();

			while (rs.next()) {
				System.out.println("id : " + rs.getLong("song_id"));
				BFILE bfile = ((OracleResultSet) rs).getBFILE("album_cover");

				System.out.println("album : " + bfile.getName() );
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
				bfile.closeFile(); // 자원 누수 방지를 위함

				Song song = new Song(
						rs.getInt("ranking"),
						rs.getLong("song_id"),
						rs.getString("song_name"),
						rs.getString("artist_name"),
						imageData,
						rs.getString("song_file"),
						rs.getLong("click_count")
				);
				songs.add(song);
			}
			ObservableList<Song> songList = FXCollections.observableArrayList(songs);
			tableView.setItems(songList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt, rs, conn); // 연결 닫기
		}

		ranking.setCellValueFactory(new PropertyValueFactory<Song, Integer>("ranking"));
		songName.setCellValueFactory(new PropertyValueFactory<Song, String>("name"));
		artistName.setCellValueFactory(new PropertyValueFactory<Song, String>("artist"));

		playBtn.setCellFactory(new Callback<>() {
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
								PreparedStatement pstmt = conn.prepareStatement("SELECT click_count FROM Song WHERE song_id=?");
								pstmt.setLong(1, selectedSong.getId());
								ResultSet rs = pstmt.executeQuery();

								if(rs.next()) {
									PreparedStatement updatePstmt = conn.prepareStatement("UPDATE Song SET click_count=? WHERE song_id=?");
									updatePstmt.setInt(1, rs.getInt("click_count")+1);
									updatePstmt.setLong(2, selectedSong.getId());
									updatePstmt.executeUpdate();
								}

								Stage newStage = new Stage();
								// Stage currentStage = (Stage) playButton.getScene().getWindow();

								FXMLLoader loader = new FXMLLoader(getClass().getResource("playview.fxml"));
								Parent playView = loader.load();
								PlayViewController controller = loader.getController();
								controller.setSongId(selectedSong.getId());

								Scene scene = new Scene(playView);

								newStage.setTitle("Play Music!");
								newStage.setScene(scene);
								newStage.showAndWait();
								// stage.hide();

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

		addBtn.setCellFactory(new Callback<>() {
			@Override
			public TableCell<Song, Void> call(TableColumn<Song, Void> param) {
				return new TableCell<>() {
					private final Button addButton = new Button("+");
					{
						addButton.setOnAction(event -> {
							Song selectedSong = getTableView().getItems().get(getIndex());
		                    try {
		                        FXMLLoader loader = new FXMLLoader(getClass().getResource("playlistSelection.fxml"));
		                        Parent parent = loader.load();

		                        PlaylistSelectionController controller = loader.getController();
		                        controller.setSongId(selectedSong.getId());
		                        controller.setCurrentMember(currentMember);  // 현재 멤버 설정

		                        Stage stage = new Stage();
		                        stage.initModality(Modality.APPLICATION_MODAL);
		                        stage.setTitle("플레이리스트 선택");
		                        Image icon = new Image(
		            	        		getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고 이미지 파일 경로 지정
		                        stage.getIcons().add(icon);
		                        stage.setScene(new Scene(parent));
		                        stage.showAndWait();
		                    } catch (IOException e) {
		                        e.printStackTrace();
		                    }
		                });
		                Font font = Font.font("D2Coding Bold", 18);
		                addButton.setFont(font);
		            }

		            @Override
		            protected void updateItem(Void item, boolean empty) {
		                super.updateItem(item, empty);
		                if (empty) setGraphic(null);
		                else {
		                    setGraphic(addButton);
		                    setAlignment(Pos.CENTER);
		                }
		            }
		        };
		    }
		});

		likebtn.setCellFactory(new Callback<>() {
			@Override
			public TableCell<Song, Void> call(TableColumn<Song, Void> param) {
				return new TableCell<>() {
					private final Button likeButton = new Button();
					{
						// 버튼 클릭 시 이벤트 처리
						likeButton.setOnAction(event -> {
							Song selectedSong = getTableView().getItems().get(getIndex());
							if (currentMember != null) {
								if (likeButton.getText().equals("♡")) {
									likeButton.setText("❤");
									likeSong(selectedSong.getId(), currentMember.getId());
									System.out.println("selectedSong.getName() = " + selectedSong.getName());
								} else {
									likeButton.setText("♡");
									cancelLike(selectedSong.getId(), currentMember.getId());
								}
							} else {
								System.out.println("로그인이 필요합니다.");
							}
						});
						Font font = Font.font("D2Coding Bold", 18);
						likeButton.setFont(font);
					}

					// 셸 Rendering
					@Override
					protected void updateItem(Void item, boolean empty) {
						super.updateItem(item, empty);
						if (empty)
							setGraphic(null);
						else {
							Song selectedSong = getTableView().getItems().get(getIndex());
							if (isSongLikedByUser(selectedSong.getId(), currentMember.getId())) {
								likeButton.setText("❤");
							} else {
								likeButton.setText("♡");
							}
							setGraphic(likeButton);
							setAlignment(Pos.CENTER);
						}
					}
				};
			}
		});

		editBtn.setCellFactory(new Callback<>() {
			@Override
			public TableCell<Song, Void> call(TableColumn<Song, Void> param) {
				return new TableCell<>() {
					private final Button editButton = new Button("✂");
					{
						// 버튼 클릭 시 이벤트 처리
						editButton.setOnAction(event -> {
							Song selectedSong = getTableView().getItems().get(getIndex());
							System.out.println("selectedSong.getName() = " + selectedSong.getName());

							try {
								Stage newStage = new Stage();
								//Stage currentStage = (Stage) playButton.getScene().getWindow();

								FXMLLoader loader = new FXMLLoader(getClass().getResource("editMusic.fxml"));
								Parent playView = loader.load();
								EditMusicController controller = loader.getController();
								controller.setMember(currentMember);
								controller.setSong(selectedSong);

								Scene scene = new Scene(playView);

								newStage.setTitle("Edit Music!");
								newStage.setScene(scene);
								newStage.showAndWait();
								//stage.hide();
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
					}

					// 셸 Rendering
					@Override
					protected void updateItem(Void item, boolean empty) {
						super.updateItem(item, empty);
						if(empty) setGraphic(null);
						else {
							setGraphic(editButton);
							setAlignment(Pos.CENTER);
						}
					}
				};
			}
		});
	}

	// 존재하는 재생목록 가져오기
	private Playlist getCurrentMemberPlaylist(String memberId, Connection conn) throws SQLException {
		PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Playlist WHERE member_id=?");
		pstmt.setString(1, memberId);
		ResultSet rs = pstmt.executeQuery();
		int num = 0;

		if (rs.next()) {
			// 재생 목록이 존재하면 Playlist 객체를 만들어서 반환
			Array songArray = rs.getArray("Song");
			BigDecimal[] songs = (BigDecimal[]) songArray.getArray();
			List<Long> songList = new ArrayList<>();
			for (BigDecimal bd : songs) {
				songList.add(bd.longValue());
			}
			return new Playlist(rs.getLong("playlist_id"), rs.getString("playlist_name"), songList,
					rs.getString("member_id"), ++num, LocalDate.now());
		} else {
			return null;
		}
	}

	// 새로운 재생목록 ID를 생성하는 메서드
	private Long generateNewPlaylistId(Connection conn) throws SQLException {
		// 데이터베이스에서 가장 큰 재생목록 ID를 찾아서 1을 더하여 새로운 ID 생성
		PreparedStatement pstmt = conn.prepareStatement("SELECT MAX(playlist_id) FROM Playlist");

		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			return rs.getLong(1) + 1;
		} else {
			return 1L;
		}
	}

	// 데이터베이스에 재생목록 update
	private void updatePlaylist(Playlist playlist, Song selectedSong, Connection conn) {
		try {
			PreparedStatement pstmt = conn.prepareStatement("UPDATE Playlist SET Song = ? WHERE playlist_id = ?");

			if (!playlist.getSongList().contains(selectedSong.getId())) {
				playlist.getSongList().add(selectedSong.getId());

				Long[] newSongs = playlist.getSongList().toArray(new Long[0]);

				ArrayDescriptor desc = ArrayDescriptor.createDescriptor("SONG_ARRAY", conn);
				ARRAY newSongArray = new ARRAY(desc, conn, newSongs);
				pstmt.setArray(1, newSongArray);
				pstmt.setLong(2, playlist.getPlaylistId());
				pstmt.executeUpdate();
				pstmt.close();
				System.out.println("successful add!");
			}
		} catch (SQLException e) {
			System.out.println("Playlist not updated successfully");
			e.printStackTrace();
		}
	}

	// 데이터베이스에 재생목록 insert
	private void insertPlayList(Playlist playlist, Connection conn) throws SQLException {
		PreparedStatement pstmt = conn
				.prepareStatement("INSERT INTO Playlist(playlist_id, playlist_name, member_id) VALUES (?, ?, ?)");
		pstmt.setLong(1, playlist.getPlaylistId());
		pstmt.setString(2, playlist.getPlaylistName());
		pstmt.setString(3, playlist.getMemberId());
		pstmt.executeQuery();
	}

	// 좋아요 취소 로직 추가
	private void cancelLike(long songId, String memberId) {
		try {
			Connection conn = DBUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement("DELETE FROM LIKES WHERE SONG_ID = ? AND MEMBER_ID = ?");
			pstmt.setLong(1, songId);
			pstmt.setString(2, memberId);
			int deletedRows = pstmt.executeUpdate();

			if (deletedRows > 0) {
				// DB에서 좋아요 정보 삭제 성공
				System.out.println("좋아요 취소 성공");
			} else {
				// DB에서 좋아요 정보 삭제 실패
				System.out.println("좋아요 취소 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void likeSong(long songId, String memberId) {
		try {
			Connection conn = DBUtil.getConnection();
			PreparedStatement pstmt;
			ResultSet rs;

			// 현재 사용자가 해당 곡에 좋아요를 눌렀는지 확인
			pstmt = conn.prepareStatement("SELECT * FROM LIKES WHERE SONG_ID = ? AND MEMBER_ID = ?");
			pstmt.setLong(1, songId);
			pstmt.setString(2, memberId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				// 좋아요를 이미 누른 경우, 좋아요를 취소하고 DB에서 해당 정보 삭제
				cancelLike(songId, memberId);
			} else {
				// 좋아요를 누르지 않은 경우, 좋아요 정보를 추가하고 DB에 저장
				pstmt = conn.prepareStatement("INSERT INTO LIKES (SONG_ID, MEMBER_ID) VALUES (?, ?)");
				pstmt.setLong(1, songId);
				pstmt.setString(2, memberId);
				int insertedRows = pstmt.executeUpdate();

				if (insertedRows > 0) {
					// DB에 좋아요 정보 추가 성공
					System.out.println("좋아요 추가 성공");
				} else {
					// DB에 좋아요 정보 추가 실패
					System.out.println("좋아요 추가 실패");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 좋아요 상태 확인 메서드 추가
	private boolean isSongLikedByUser(long songId, String memberId) {
		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn
						.prepareStatement("SELECT * FROM LIKES WHERE SONG_ID = ? AND MEMBER_ID = ?")) {
			pstmt.setLong(1, songId);
			pstmt.setString(2, memberId);
			ResultSet rs = pstmt.executeQuery();
			return rs.next(); // 좋아요가 존재하면 true를 반환
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}