package kosa.watermelon.watermelonmusic;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * DashboardController : 대시보드 페이지 컨트롤러
 * 로그인 후 이동되는 페이지로, 다양한 기능으로의 접근을 제공함
 * 
 * 작성자 : 김효정
 */
public class DashboardController implements Initializable {
	
	// FXML 필드
	@FXML private TableView<Song> tableView; // tableView 변수 정의
	@FXML private ImageView logo_ImageView;
	@FXML private ImageView logoText_ImageView;
	@FXML private ImageView SongChart_ImageView;
	@FXML private ImageView Search_ImageView;
	@FXML private ImageView PostingPage_ImageView;
	@FXML private ImageView EditSongPlaylist_ImageView;
	@FXML private ImageView MyPage_ImageView;
	@FXML private ImageView Playlist_ImageView;
	@FXML private Label username;
	@FXML private Button logout_BTN;

	private Member currentMember;

	/**
	 * 초기화 메서드 : FXML 파일이 로드된 후 호출됨
	 * 
	 * @param url            초기화 URL
	 * @param resourceBundle 초기화 ResourceBundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.currentMember = SessionManager.getInstance().getCurrentMember();
		if (currentMember != null) {
			System.out.println("DashboardController: Member set with ID - " + currentMember.getId());
			loadMemberInfo();
		} else {
			System.out.println("Error: currentMember is null.");
		}

		loadImages();
		setOnMouseClickEvents();
	}

	/**
	 * 대시보드의 이미지를 로드하는 메서드
	 */
	private void loadImages() {
		Image logo_Icon = new Image(
				getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png"));
		Image logoText_Icon = new Image(
				getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_text.png"));
		Image SongChart_Icon = new Image(getClass()
				.getResourceAsStream("/kosa/watermelon/watermelonmusic/image/dashBoardIcon/songChart_icon.png"));
		Image Search_Icon = new Image(
				getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/image/dashBoardIcon/search_icon.png"));
		Image PostingPage_Icon = new Image(getClass()
				.getResourceAsStream("/kosa/watermelon/watermelonmusic/image/dashBoardIcon/postingPage_icon.png"));
		Image MusicEdit_Icon = new Image(getClass()
				.getResourceAsStream("/kosa/watermelon/watermelonmusic/image/dashBoardIcon/editSong_icon.png"));
		Image MyPage_Icon = new Image(
				getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/image/dashBoardIcon/myPage_icon.png"));
		Image Playlist_Icon = new Image(getClass()
				.getResourceAsStream("/kosa/watermelon/watermelonmusic/image/dashBoardIcon/playlist_icon.png"));

		logo_ImageView.setImage(logo_Icon);
		logoText_ImageView.setImage(logoText_Icon);
		SongChart_ImageView.setImage(SongChart_Icon);
		Search_ImageView.setImage(Search_Icon);
		PostingPage_ImageView.setImage(PostingPage_Icon);
		EditSongPlaylist_ImageView.setImage(MusicEdit_Icon);
		MyPage_ImageView.setImage(MyPage_Icon);
		Playlist_ImageView.setImage(Playlist_Icon);
	}

	/**
	 * 각 이미지에 클릭 이벤트를 설정하는 메서드
	 */
	private void setOnMouseClickEvents() {
		SongChart_ImageView.setOnMouseClicked(event -> goToPage("songChart.fxml", SongChart_ImageView));
		Search_ImageView.setOnMouseClicked(event -> goToPage("songChartwithSearch.fxml", Search_ImageView));
		PostingPage_ImageView.setOnMouseClicked(event -> goToPage("postingPage.fxml", PostingPage_ImageView));
		EditSongPlaylist_ImageView
				.setOnMouseClicked(event -> goToPage("editSongPlaylist.fxml", EditSongPlaylist_ImageView));
		MyPage_ImageView.setOnMouseClicked(event -> goToPage("mypage.fxml", MyPage_ImageView));
		Playlist_ImageView.setOnMouseClicked(event -> goToPage("playlistUser.fxml", Playlist_ImageView));
	}

	/**
	 * 페이지 전환을 처리하는 메서드
	 * 
	 * @param fxmlFile        이동할 FXML 파일의 이름
	 * @param sourceImageView 이벤트가 발생한 ImageView
	 */
	private void goToPage(String fxmlFile, ImageView sourceImageView) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			Stage newStage = (Stage) sourceImageView.getScene().getWindow();
			Scene scene = new Scene(loader.load(), 800, 600);

			Object controller = loader.getController();
			// 컨트롤러에 현재 회원 정보 전달
			if (controller instanceof SongChartController) {
				newStage.setTitle(fxmlFile.equals("songChart.fxml") ? "인기 차트" : "검색");
				((SongChartController) controller).setMember(currentMember);
			} else if (controller instanceof SearchController) {
				newStage.setTitle(fxmlFile.equals("songChartwithSearch.fxml") ? "검색" : "인기 차트");
				((SearchController) controller).setTableView(tableView);
				((SearchController) controller).setMember(currentMember);
			} else if (controller instanceof EditSongPlaylistController) {
				((EditSongPlaylistController) controller).setMember(currentMember);
				newStage.setTitle("음악 편집 리스트");
			} else if (controller instanceof MyPageController) {
				newStage.setTitle("마이페이지");
				((MyPageController) controller).setMember(currentMember);
			} else if (controller instanceof PlaylistUserController) {
				newStage.setTitle("플레이리스트");
				((PlaylistUserController) controller).setMember(currentMember);
			} else if (controller instanceof PostingPageController) {
				newStage.setTitle("게시판");
			}

			Image icon = new Image(
					getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png"));
			newStage.getIcons().add(icon);
			newStage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 로그아웃 이벤트를 처리하는 메서드
	 * 
	 * @param event 로그아웃 액션 이벤트
	 */
	@FXML
	private void logout_Action(ActionEvent event) {
		// 세션 초기화
		SessionManager.getInstance().clearSession();

		// 로그인 창 열기
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
			Scene scene = new Scene(loader.load(), 800, 600);

			// 현재 Stage 찾기
			Stage currentStage = (Stage) logout_BTN.getScene().getWindow();
			currentStage.setTitle("WaterMelon Music!");

			// MainApplicatin의 Scene 설정
			currentStage.setScene(scene);
			Image icon = new Image(
					getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png"));
			currentStage.getIcons().add(icon);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 현재 로그인된 회원 정보를 설정하는 메서드
	 * 
	 * @param member 현재 로그인된 회원 객체
	 */
	public void setMember(Member member) {
		this.currentMember = member;
		if (this.currentMember == null) {
			System.out.println("DashboardController: setMember called with null member");
		} else {
			System.out.println("DashboardController: Member set with ID - " + currentMember.getId());
			loadMemberInfo();
		}
	}

	/**
	 * 현재 로그인된 회원 정보를 로드하여 표시하는 메서드
	 */
	private void loadMemberInfo() {
		if (currentMember != null) {
			username.setText(currentMember.getNickname());
		}
	}
}