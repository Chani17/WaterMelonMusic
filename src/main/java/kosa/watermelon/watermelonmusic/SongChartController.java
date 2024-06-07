package kosa.watermelon.watermelonmusic;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

public class SongChartController implements Initializable {
	private final static String ID = "admin";
	private final static String PW = "1234";
	private final static String URL = "jdbc:oracle:thin:@localhost:1521:xe";

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
	private Button detailButton;

	@FXML
	private GridPane root;

	@FXML
	private HBox searchContainer;
	
	private ContextMenu contextMenu;

	private Member currentMember;
	
	//private Playlist playlist;

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
		setUpContextMenu();
		setupMyPlaylistButton();
	}

	public void setMember(Member member) {
		this.currentMember = member;
	}

	private void setupMyPlaylistButton() {
		detailButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				contextMenu.show(detailButton, detailButton.localToScreen(detailButton.getBoundsInLocal()).getMinX(),
						detailButton.localToScreen(detailButton.getBoundsInLocal()).getMinY()
								+ detailButton.getHeight());
			}
		});
	}

	@FXML
	private void setUpContextMenu() {
		contextMenu = new ContextMenu();

		MenuItem myPlaylistItem = new MenuItem("My Playlist");
		MenuItem myPageItem = new MenuItem("My Page");

		myPlaylistItem.setOnAction(event -> moveToMyPlaylistPage(event));
		myPageItem.setOnAction(event -> moveToMyPage(event));

		FXMLLoader loader = new FXMLLoader(getClass().getResource("playlist.fxml"));
		try {
			Parent playlist = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// PlaylistController 인스턴스를 가져와서 멤버 설정
		PlaylistController controller = loader.getController();
		controller.setMember(currentMember);

		contextMenu.getItems().addAll(myPlaylistItem, myPageItem);
	}

	private void moveToMyPage(ActionEvent event) {
		try {
			// FXML 파일 로드
			FXMLLoader loader = new FXMLLoader(getClass().getResource("mypage.fxml"));
			Parent parent = loader.load();

			// MyPageController 인스턴스를 가져와서 멤버 설정
			MyPageController controller = loader.getController();
			controller.setMember(currentMember);

			// 새 Stage 생성 후 기존 Stage 닫기
			Stage newStage = new Stage();
			Stage currentStage = (Stage) detailButton.getScene().getWindow();
			
			newStage.setTitle("마이페이지");
			newStage.setScene(new Scene(parent, 600, 464));
			newStage.show();
			currentStage.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void moveToMyPlaylistPage(ActionEvent event) {
        try {
            // FXML 파일 로드
            FXMLLoader loader = new FXMLLoader(getClass().getResource("playlist.fxml"));
            Parent parent = loader.load();

            // PlaylistController 인스턴스를 가져와서 멤버 설정
            PlaylistController controller = loader.getController();
            controller.setMember(currentMember);

            // 새 Stage 생성 후 기존 Stage 닫기
            Stage newStage = new Stage();
            Stage currentStage = (Stage) detailButton.getScene().getWindow();
            
            newStage.setTitle("My Playlist");
            newStage.setScene(new Scene(parent, 600, 464));
            newStage.show();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private void setListView() {
		Connection conn = DBConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Song> songs = new ArrayList<>();

		try {
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
	        try {
	            if (rs != null) rs.close();
	            if (pstmt != null) pstmt.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
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
					//Connection conn = DBConnection();
					private final Button addButton = new Button("+");
					{
						// 버튼 클릭 시 이벤트 처리
						addButton.setOnAction(event -> {
							Song selectedSong = getTableView().getItems().get(getIndex());
							Connection conn = DBConnection();
							try {
								Playlist playlist = getCurrentMemberPlaylist(currentMember.getId(), conn);
								if (playlist != null) {
									// 재생 목록에 노래 추가
									playlist.addSong(selectedSong.getId());

									// 데이터베이스 update
									updatePlaylist(playlist, conn);
								} else {
									// 새로운 재생목록 생성
									playlist = new Playlist(generateNewPlaylistId(conn), "Default Playlist",
											currentMember.getId());
									insertPlayList(playlist, conn);
									playlist.addSong(selectedSong.getId());

									// 데이터베이스 update
									updatePlaylist(playlist, conn);
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								try {
									if (conn != null) conn.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
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

	private Connection DBConnection() {
		// 드라이버 검색 (db와 연동 준비)
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("Driver search success");
		} catch (ClassNotFoundException e) {
			System.err.println("Driver search fail");
			System.exit(0);
		}

		// 데이터베이스 연결 - 커넥션 만들기
		Connection conn = null;

		try {
			conn = DriverManager.getConnection(URL, ID, PW);
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
//
//	public void setMember(Member member) {
//		this.currentMember = member;
//	}

	// 존재하는 재생목록 가져오기
	private Playlist getCurrentMemberPlaylist(String memberId, Connection conn) throws SQLException {
		PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Playlist WHERE member_id=?");
		pstmt.setString(1, memberId);
		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) {
			// 재생 목록이 존재하면 Playlist 객체를 만들어서 반환
			return new Playlist(rs.getLong("playlist_id"), rs.getString("playlist_name"), rs.getString("member_id"));
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
	private void updatePlaylist(Playlist playlist, Connection conn) throws SQLException {
		// Convert List<Long> to Long[]
		Long[] songArray = playlist.getSongList().toArray(new Long[0]);

		// Create Oracle ArrayDescriptor for SONG_ARRAY
		ArrayDescriptor desc = ArrayDescriptor.createDescriptor("SONG_ARRAY", conn);

		// Convert Long[] to oracle.sql.ARRAY
		ARRAY oracleArray = new ARRAY(desc, conn, songArray);

		PreparedStatement pstmt = conn.prepareStatement("UPDATE Playlist SET song=? WHERE playlist_id=?");
		pstmt.setArray(1, oracleArray);
		pstmt.setLong(2, playlist.getPlaylistID());
		pstmt.executeUpdate();
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