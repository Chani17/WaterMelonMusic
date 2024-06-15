package kosa.watermelon.watermelonmusic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * PostingPageController 클래스 : 포스팅 페이지를 관리하는 컨트롤러 클래스
 * 
 * 작성자 : 김효정
 */
public class PostingPageController {

	// FXML 필드
    @FXML private Button goToDashboard_BTN;
    @FXML private Button addPlaylist_BTN;
    @FXML private TableView<Playlist> playlistTableView;
    @FXML private TableColumn<Playlist, String> nameColumn;
    @FXML private TableColumn<Playlist, String> memberColumn;
    @FXML private TableColumn<Playlist, String> dateColumn;

    private ObservableList<Playlist> playlists;
    private Member currentMember;


	/**
	 * 회원 설정 메서드
	 * 
	 * @param member 설정할 회원 객체
	 */
	public void setMember(Member member) {
		this.currentMember = member;
		System.out.println("PostingPageController: Member set with ID - " + currentMember.getId());
	}

	/**
	 * 컨트롤러 초기화 메서드
	 */
	@FXML
	public void initialize() {
		playlists = FXCollections.observableArrayList();
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("playlistName"));
		memberColumn.setCellValueFactory(new PropertyValueFactory<>("memberId"));
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("postDate"));

		playlistTableView.setItems(playlists);

		playlistTableView.setStyle("-fx-font-family: 'D2Coding'; -fx-font-size: 10pt;");

		// TableView의 각 행에 대한 폰트 설정
		playlistTableView.setRowFactory(tv -> {
			TableRow<Playlist> row = new TableRow<>();
			row.setStyle("-fx-font-family: 'D2Coding'; -fx-font-size: 10pt;");
			return row;
		});

		// 행을 더블 클릭하면 상세 화면으로 이동
		playlistTableView.setRowFactory(tv -> {
			TableRow<Playlist> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Playlist rowData = row.getItem();
					openPlaylistDetail(rowData);
				}
			});
			return row;
		});

		this.currentMember = SessionManager.getInstance().getCurrentMember();
		if (this.currentMember != null) {
			System.out.println("PostingPageController: Member set with ID - " + currentMember.getId());
		} else {
			System.out.println("PostingPageController: No member set");
		}
		// 데이터베이스에서 플레이리스트 불러오기
		loadPlaylistsFromDatabase();
	}

	/**
	 * 선택된 플레이리스트를 추가하는 메서드
	 * 
	 * @param selectedPlaylist 추가할 플레이리스트 객체
	 */
	public void addSelectedPlaylist(Playlist selectedPlaylist) {
		if (selectedPlaylist != null) {
			selectedPlaylist.setPostDate(LocalDate.now());
			playlists.add(selectedPlaylist);
			savePlaylistToPostAndMpp(selectedPlaylist); // 플레이리스트를 POST와 MPP 테이블에 저장
		}
	}

	/**
	 * 플레이리스트 추가 버튼 이벤트 핸들러
	 * 
	 * @param event 이벤트 객체
	 */
	@FXML
	private void handleAddPlaylist(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("playlistDialog.fxml"));
			Parent parent = loader.load();

			PlaylistDialogController controller = loader.getController();
			controller.setPostingPageController(this); // Set the reference to this controller

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("업로드할 플레이리스트 선택");
			stage.setScene(new Scene(parent, 400, 300));
			Image icon = new Image(
					getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png"));
			stage.getIcons().add(icon);
			stage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 플레이리스트를 POST와 MPP 테이블에 저장하는 메서드
	 * 
	 * @param playlist 저장할 플레이리스트 객체
	 */
	private void savePlaylistToPostAndMpp(Playlist playlist) {
		Member currentMember = SessionManager.getInstance().getCurrentMember();
		if (currentMember == null) {
			return; // 현재 사용자가 없으면 저장하지 않음
		}

		String memberId = currentMember.getId();
		String postSql = "INSERT INTO POSTING (POST_ID, POST_DATE) VALUES (?, ?)";
		String mppSql = "INSERT INTO MPP (PLAYLIST_ID, POST_ID, MEMBER_ID) VALUES (?, ?, ?)";

		Connection conn = null;
		PreparedStatement postStmt = null;
		PreparedStatement mppStmt = null;

		try {
			conn = DBUtil.getConnection();
			conn.setAutoCommit(false); // 트랜잭션 시작

			long newPostId = generateNewId(conn); // 새로운 ID 생성
			System.out.println("newPostId = " + newPostId);

			// POST 테이블에 데이터 삽입
			postStmt = conn.prepareStatement(postSql);
			postStmt.setLong(1, newPostId);
			postStmt.setDate(2, Date.valueOf(LocalDate.now())); // 현재 날짜 설정
			postStmt.executeUpdate();
			conn.commit();

			// POST 테이블에 삽입이 완료된 후 MPP 테이블에 데이터 삽입
			mppStmt = conn.prepareStatement(mppSql);
			mppStmt.setLong(1, playlist.getPlaylistId());
			System.out.println("after newPostId = " + newPostId);
			mppStmt.setLong(2, newPostId);
			mppStmt.setString(3, memberId);
			mppStmt.executeUpdate();

			conn.commit(); // 트랜잭션 커밋

		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback(); // 트랜잭션 롤백
				} catch (SQLException rollbackEx) {
					rollbackEx.printStackTrace();
				}
			}
			e.printStackTrace();
		} finally {
			DBUtil.close(postStmt, null, null);
			DBUtil.close(mppStmt, null, conn);
		}
	}

	/**
	 * 새로운 ID를 생성하는 메서드
	 * 
	 * @param conn 데이터베이스 연결 객체
	 * @return 생성된 ID
	 */
	private Long generateNewId(Connection conn) {
		Long newId = null;
		String nextValSql = "SELECT POSTING_SEQ.NEXTVAL FROM DUAL";

		try (PreparedStatement nextValStmt = conn.prepareStatement(nextValSql);
				ResultSet nextValRs = nextValStmt.executeQuery()) {

			if (nextValRs.next()) {
				newId = nextValRs.getLong(1);
				System.out.println("newId = " + newId);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return newId;
	}

	/**
	 * 데이터베이스에서 플레이리스트를 불러오는 메서드
	 */
	private void loadPlaylistsFromDatabase() {
		String sql = "SELECT p.PLAYLIST_ID, p.PLAYLIST_NAME, m.NICKNAME, po.POST_DATE FROM PLAYLIST p "
				+ "JOIN MPP mp ON p.PLAYLIST_ID = mp.PLAYLIST_ID " + "JOIN POSTING po ON mp.POST_ID = po.POST_ID "
				+ "JOIN MEMBER m ON mp.MEMBER_ID = m.MEMBER_ID";

		try (Connection conn = DBUtil.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				long playlistId = rs.getLong("PLAYLIST_ID");
				String playlistName = rs.getString("PLAYLIST_NAME");
				String memberId = rs.getString("NICKNAME");
				LocalDate postDate = rs.getDate("POST_DATE").toLocalDate();

				Playlist playlist = new Playlist(playlistId, playlistName, new ArrayList<>(), memberId, 0, postDate);
				playlists.add(playlist);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 포스팅 → DashBoard 페이지 이동 이벤트 처리
	 * 
	 * @param event 이벤트 객체
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
	 * 플레이리스트 상세보기 화면을 여는 메서드
	 * 
	 * @param playlist 상세보기 할 플레이리스트 객체
	 */
	private void openPlaylistDetail(Playlist playlist) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("playlistDetail.fxml"));
			Parent parent = loader.load();

			PlaylistDetailController controller = loader.getController();
			controller.setPlaylistId(playlist.getPlaylistId());

			Stage newStage = new Stage();
			Stage currentStage = (Stage) goToDashboard_BTN.getScene().getWindow();
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("플레이리스트 상세보기 - " + playlist.getPlaylistName()); // 플레이리스트 이름을 추가하여 설정
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