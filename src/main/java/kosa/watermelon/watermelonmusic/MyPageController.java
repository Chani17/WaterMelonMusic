package kosa.watermelon.watermelonmusic;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.application.Platform;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * MyPageController 클래스 : 마이페이지 컨트롤러
 * 회원 정보 및 좋아하는 노래 목록을 관리
 */
public class MyPageController implements Initializable {
	
	// FXML 필드
	@FXML private ImageView profile_Image;
	@FXML private Button profileEdit_BTN;
	@FXML private Button goToDashboard_BTN;
	@FXML private TextField userNAME_TextField;
	@FXML private TextField userID_TextField;
	@FXML private TextField userEMAIL_TextField;
	@FXML private TextField userGender_TextField;
	@FXML private TextField userBirth_TextField;
	@FXML private TilePane playlistImage_TilePane;
	@FXML private Label focusLabel; // 마이페이지 텍스트필드에 커서 깜빡이지 않도록 수정
	@FXML private TableView<Song> likedSongsTableView;
	@FXML private TableColumn<Song, String> songName;
	@FXML private TableColumn<Song, String> artistName;
	@FXML private TableColumn<Song, Void> likebtn;
	@FXML private TableColumn<Song, Void> playBtn;

	// 현재 로그인한 회원 정보
	private Member currentMember;

	/**
	 * 초기화 메서드
	 * 마이페이지 컨트롤러를 초기화하고 회원 정보를 로드
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.currentMember = SessionManager.getInstance().getCurrentMember();
		if (currentMember != null) {
			System.out.println("MyPageController: Member set with ID - " + currentMember.getId());
			loadMemberInfo();
			loadLikedSongs();
		} else {
			System.out.println("Error: currentMember is null.");
		}

		// TextField를 수정 불가능하게 설정
		userNAME_TextField.setEditable(false);
		userID_TextField.setEditable(false);
		userEMAIL_TextField.setEditable(false);
		userGender_TextField.setEditable(false);
		userBirth_TextField.setEditable(false);

		// TextField에 포커스를 제거하고 다른 곳으로 포커스를 설정
		Platform.runLater(() -> focusLabel.requestFocus());

		songName.setCellValueFactory(new PropertyValueFactory<>("name"));
		artistName.setCellValueFactory(new PropertyValueFactory<>("artistName"));

		likedSongsTableView.setStyle("-fx-font-family: 'D2Coding'; -fx-font-size: 10pt;");

		// TableView의 각 행에 대한 폰트 설정
		likedSongsTableView.setRowFactory(tv -> {
			TableRow<Song> row = new TableRow<>();
			row.setStyle("-fx-font-family: 'D2Coding'; -fx-font-size: 10pt;");
			return row;
		});

		initializeTableView();
	}

	/**
	 * TableView 초기화 메서드
	 * 좋아요 버튼과 재생 버튼의 셀 팩토리 설정
	 */
	private void initializeTableView() {
		songName.setCellValueFactory(new PropertyValueFactory<Song, String>("name"));
		artistName.setCellValueFactory(new PropertyValueFactory<Song, String>("artist"));

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

					// 셀 렌더링
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
								FXMLLoader loader = new FXMLLoader(getClass().getResource("playview.fxml"));
								Parent playView = loader.load();
								PlayViewController controller = loader.getController();
								controller.setSongId(selectedSong.getId());

								Scene scene = new Scene(playView);

								newStage.setTitle(selectedSong.getName() + " - " + selectedSong.getArtist());
								Image icon = new Image(getClass().getResourceAsStream(
										"/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고 이미지 파일 경로
																										// 지정
								newStage.getIcons().add(icon);
								newStage.setScene(scene);
								newStage.showAndWait();
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
						Font font = Font.font("D2Coding Bold", 18);
						playButton.setFont(font);
					}

					// 셸 렌더링
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
	 * 좋아하는 노래 목록 로드 메서드
	 * 현재 회원의 좋아하는 노래 목록을 DB에서 불러와 TableView에 설정
	 */
	private void loadLikedSongs() {
		List<Song> likedSongs = new ArrayList<>(); // 좋아하는 노래 목록을 저장할 리스트 생성

		try {
			// DB 연결
			Connection conn = DBUtil.getConnection();

			// 쿼리 준비
			String sql = "SELECT s.SONG_ID, s.SONG_NAME, a.ARTIST_NAME " + "FROM SONG s "
					+ "JOIN LIKES l ON s.SONG_ID = l.SONG_ID " + "JOIN MEMBER m ON l.MEMBER_ID = m.MEMBER_ID "
					+ "JOIN ARTIST a ON s.ARTIST_ID = a.ARTIST_ID " + "WHERE m.MEMBER_ID = ?"
					+ "ORDER BY l.SONG_ID DESC, l.MEMBER_ID DESC";
			PreparedStatement pstmt = conn.prepareStatement(sql);

			// 파라미터 설정
			pstmt.setString(1, currentMember.getId());

			// 쿼리 실행 및 결과 가져오기
			ResultSet rs = pstmt.executeQuery();

			// 결과를 Song 객체로 변환하여 likedSongs 리스트에 추가
			while (rs.next()) {
				Song song = new Song(0, rs.getLong("SONG_ID"), rs.getString("SONG_NAME"), rs.getString("ARTIST_NAME"),
						0);
				likedSongs.add(song);
			}

			// DB 연결 해제
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 가져온 좋아하는 노래 목록을 TableView에 설정
		ObservableList<Song> observableLikedSongs = FXCollections.observableArrayList(likedSongs);
		likedSongsTableView.setItems(observableLikedSongs);
	}

	/**
	 * 현재 회원의 좋아하는 노래 목록 반환 메서드
	 * 
	 * @return 현재 회원의 좋아하는 노래 목록
	 */
	private List<Song> getLikedSongsFromCurrentMember() {
		if (currentMember != null) {
			return currentMember.getLikedSongs();
		} else {
			System.out.println("Error: currentMember is null.");
			return new ArrayList<>(); // 빈 리스트 반환
		}
	}

	/**
	 * 회원 설정 메서드
	 * 
	 * @param member 설정할 회원 객체
	 */
	public void setMember(Member member) {
		this.currentMember = member;
		if (this.currentMember == null) {
			System.out.println("MyPageController: setMember called with null member");
		} else {
			System.out.println("MyPageController: Member set with ID - " + currentMember.getId());
			loadMemberInfo();
			loadLikedSongs(); // 회원의 좋아하는 노래 목록을 설정
		}
	}

	/**
	 * 회원 정보 로드 메서드 현재 회원의 정보를 화면에 설정
	 */
	private void loadMemberInfo() {
		if (currentMember != null) {
			userNAME_TextField.setText(currentMember.getNickname());
			userID_TextField.setText(currentMember.getId());
			userEMAIL_TextField.setText(currentMember.getEmail());
			userGender_TextField.setText(currentMember.getGender());
			userBirth_TextField.setText(currentMember.getBirth().toString());

			// Load profile image from byte array
			byte[] profileImageBytes = currentMember.getProfileImage();
			Image profileImage = new Image(new ByteArrayInputStream(profileImageBytes));
			profile_Image.setImage(profileImage);
		}
	}

	/**
	 * 프로필 편집 페이지 이동 이벤트 처리 메서드
	 * 
	 * @param event 액션 이벤트
	 */
	@FXML
	private void profileEdit_Action(ActionEvent event) {
		try {
			// FXML 파일 로드
			FXMLLoader loader = new FXMLLoader(getClass().getResource("profileEDIT.fxml"));
			Parent parent = loader.load();

			ProfileEditController controller = loader.getController();
			controller.setMember(currentMember);

			Stage newStage = new Stage();

			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("프로필 편집");
			newStage.setScene(new Scene(parent, 300, 200));
			Image icon = new Image(
					getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png"));
			newStage.getIcons().add(icon);
			newStage.showAndWait();

			// 프로필 수정 후 업데이트
			userNAME_TextField.setText(currentMember.getNickname());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * DashBoard 페이지 이동 이벤트 처리 메서드
	 * 
	 * @param event 액션 이벤트
	 */
	@FXML
	private void goToDashboard_Action(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
			Parent parent = loader.load();

			DashboardController controller = loader.getController();
			controller.setMember(currentMember);

			Stage newStage = new Stage();
			Stage currentStage = (Stage) goToDashboard_BTN.getScene().getWindow();

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
	 * 좋아요 취소 메서드
	 * 
	 * @param songId   취소할 노래의 ID
	 * @param memberId 현재 회원의 ID
	 */
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

	/**
	 * 좋아요 추가 메서드
	 * 
	 * @param songId   좋아요할 노래의 ID
	 * @param memberId 현재 회원의 ID
	 */
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

				DBUtil.close(conn, pstmt, rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 좋아요 상태 확인 메서드
	 * 
	 * @param songId   노래의 ID
	 * @param memberId 회원의 ID
	 * @return 좋아요 상태 (true: 좋아요 누름, false: 좋아요 누르지 않음)
	 */
	private boolean isSongLikedByUser(long songId, String memberId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBUtil.getConnection();
			pstmt = conn
					.prepareStatement("SELECT * FROM LIKES WHERE SONG_ID = ? AND MEMBER_ID = ?");
			pstmt.setLong(1, songId);
			pstmt.setString(2, memberId);
			rs = pstmt.executeQuery();
			return rs.next(); // 좋아요가 존재하면 true를 반환
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}
	}
}