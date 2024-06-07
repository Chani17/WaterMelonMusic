package kosa.watermelon.watermelonmusic;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

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
	private TableColumn<Song, Void> likebtn;

	@FXML
	private Button goToDashboard_BTN;

	@FXML
	private GridPane root;

	@FXML
	private HBox searchContainer;

	private Member currentMember;
	
	private Playlist playlist;

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
	private void goToDashboard_Action(ActionEvent event)  {
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
            Parent parent = loader.load();
            
            Stage newStage = new Stage();
			Stage currentStage = (Stage) goToDashboard_BTN.getScene().getWindow();
			
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("메인 화면");
			newStage.setScene(new Scene(parent, 600, 464));
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
		        "ORDER BY click_count DESC"
		    );
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Song song = new Song(
					rs.getInt("ranking"),
		            rs.getLong("song_id"),
		            rs.getString("song_name"),
		            rs.getString("artist_name"),
		            rs.getLong("click_count")
		        );
				songs.add(song);
			}
			ObservableList<Song> songList = FXCollections.observableArrayList(songs);
			tableView.setItems(songList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs, pstmt, conn); // 연결 닫기
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
								Stage newStage = new Stage();
								//Stage currentStage = (Stage) playButton.getScene().getWindow();

								FXMLLoader loader = new FXMLLoader(getClass().getResource("playview.fxml"));
								Parent playView = loader.load();
								PlayViewController controller = loader.getController();
								controller.setSongId(selectedSong.getId());

								Scene scene = new Scene(playView);

								newStage.setTitle("Play Music!");
								newStage.setScene(scene);
								newStage.showAndWait();
                                //stage.hide();

							} catch (IOException e) {
								e.printStackTrace();
							}
						});
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
						// 버튼 클릭 시 이벤트 처리
						addButton.setOnAction(event -> {
							Song selectedSong = getTableView().getItems().get(getIndex());
							Connection conn = null;
							try {
								conn = DBUtil.getConnection();
								Playlist playlist = getCurrentMemberPlaylist(currentMember.getId(), conn);

								if(playlist==null) System.out.println("null");
								else System.out.println("not null");

								if (playlist != null) {
									// 재생 목록에 노래 추가
//									playlist.addSong(selectedSong.getId());

									// 데이터베이스 update
									updatePlaylist(playlist, selectedSong, conn);
								} else {
									// 새로운 재생목록 생성
									playlist = new Playlist(generateNewPlaylistId(conn), "Default Playlist", new ArrayList<Long>(),
											currentMember.getId());

									insertPlayList(playlist, conn);
//									playlist.addSong(selectedSong.getId());

									// 데이터베이스 update
									updatePlaylist(playlist, selectedSong, conn);
									DBUtil.close(conn);
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								DBUtil.close(conn); // 연결 종료
	                        }
						});
					}

					// 셸 Rendering
					@Override
					protected void updateItem(Void item, boolean empty) {
						super.updateItem(item, empty);
						if (empty)
							setGraphic(null);
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
                    private final Button likeButton = new Button("❤");
                    {
                        // 버튼 클릭 시 이벤트 처리
                        likeButton.setOnAction(event -> {
                            Song selectedSong = getTableView().getItems().get(getIndex());
                            System.out.println("selectedSong.getName() = " + selectedSong.getName());
                        });
                    }

                    // 셸 Rendering
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) setGraphic(null);
                        else {
                            setGraphic(likeButton);
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

		if (rs.next()) {
			// 재생 목록이 존재하면 Playlist 객체를 만들어서 반환
			Array songArray = rs.getArray("Song");
			BigDecimal[] songs = (BigDecimal[]) songArray.getArray();
			List<Long> songList = new ArrayList<>();
			for (BigDecimal bd : songs) {
				songList.add(bd.longValue());
			}
			return new Playlist(rs.getLong("playlist_id"), rs.getString("playlist_name"), songList, rs.getString("member_id"));
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

			if(!playlist.getSongList().contains(selectedSong.getId())) {
				playlist.getSongList().add(selectedSong.getId());

				Long[] newSongs = playlist.getSongList().toArray(new Long[0]);

				ArrayDescriptor desc = ArrayDescriptor.createDescriptor("SONG_ARRAY", conn);
				ARRAY newSongArray = new ARRAY(desc, conn, newSongs);
				pstmt.setArray(1, newSongArray);
				pstmt.setLong(2, playlist.getPlaylistID());
				pstmt.executeUpdate();
				pstmt.close();
				System.out.println("successful add!");
			}
		} catch(SQLException e) {
			System.out.println("Playlist not updated successfully");
			e.printStackTrace();
		}
	}

	// 데이터베이스에 재생목록 insert
	private void insertPlayList(Playlist playlist, Connection conn) throws SQLException {
		PreparedStatement pstmt = conn
				.prepareStatement("INSERT INTO Playlist(playlist_id, playlist_name, member_id) VALUES (?, ?, ?)");
		pstmt.setLong(1, playlist.getPlaylistID());
		pstmt.setString(2, playlist.getPlaylistName());
		pstmt.setString(3, playlist.getMemberId());
		pstmt.executeQuery();
	}
}