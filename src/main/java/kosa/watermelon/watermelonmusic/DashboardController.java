package kosa.watermelon.watermelonmusic;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class DashboardController implements Initializable {
	// 버튼 모아둔 페이지 (로그인 후 이동되는 페이지)
	@FXML private ImageView logo_ImageView;
	@FXML private ImageView logoText_ImageView;
	@FXML private ImageView SongChart_ImageView;
	@FXML private ImageView Search_ImageView;
	@FXML private ImageView PostingPage_ImageView;
	@FXML private ImageView MusicEdit_ImageView;
	@FXML private ImageView MyPage_ImageView;
	@FXML private ImageView Playlist_ImageView;
	@FXML private TextField userNAME_TextField;
	@FXML private Label focusLabel; // 대시보드 텍스트필드에 커서 깜빡이지 않도록 수정
	@FXML private Button logout_BTN;

	@FXML private TableView<Song> tableView; // tableView 변수 정의
	private Member currentMember;

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
		userNAME_TextField.setEditable(false);

		// TextField에 포커스를 제거하고 다른 곳으로 포커스를 설정
		Platform.runLater(() -> focusLabel.requestFocus());
	}

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
		MusicEdit_ImageView.setImage(MusicEdit_Icon);
		MyPage_ImageView.setImage(MyPage_Icon);
		Playlist_ImageView.setImage(Playlist_Icon);
	}

	private void setOnMouseClickEvents() {
		SongChart_ImageView.setOnMouseClicked(event -> goToPage("songChart.fxml", SongChart_ImageView));
		Search_ImageView.setOnMouseClicked(event -> goToPage("songChartwithSearch.fxml", Search_ImageView));
		PostingPage_ImageView.setOnMouseClicked(event -> goToPage("postingPage.fxml", PostingPage_ImageView));
//	    MusicEdit_ImageView.setOnMouseClicked(event -> goToPage("musicEdit.fxml", MusicEdit_ImageView));
		MyPage_ImageView.setOnMouseClicked(event -> goToPage("mypage.fxml", MyPage_ImageView));
		Playlist_ImageView.setOnMouseClicked(event -> goToPage("playlist.fxml", Playlist_ImageView));
	}
	
	private void goToPage(String fxmlFile, ImageView sourceImageView) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			Stage newStage = (Stage) sourceImageView.getScene().getWindow();
			Scene scene = new Scene(loader.load());

			// 컨트롤러에 현재 회원 정보 전달
			if (loader.getController() instanceof SongChartController) {
				if (fxmlFile.equals("songChart.fxml")) {
					newStage.setTitle("인기 차트");
				} else {
					newStage.setTitle("검색");
				}
				SongChartController controller = loader.getController();
				controller.setMember(currentMember);
			} else if (loader.getController() instanceof SearchController) {
	            if (fxmlFile.equals("songChartwithSearch.fxml")) {
	            	newStage.setTitle("검색"); // 검색 페이지로 이동할 때 제목 설정
	            } else {
	            	newStage.setTitle("인기 차트"); // 다른 페이지인 경우 인기차트로 설정
	            }
	            SearchController controller = loader.getController();
	            controller.setTableView(tableView); // 예시로 tableView를 전달하는 코드
	            controller.setMember(currentMember);
			} else if (loader.getController() instanceof MyPageController) {
				newStage.setTitle("마이페이지");
			} else if(loader.getController() instanceof PlaylistController) {
				newStage.setTitle("플레이리스트");
				PlaylistController controller = loader.getController();
				controller.setMember(currentMember);
			} else if (loader.getController() instanceof PostingPageController) {
				newStage.setTitle("게시판");
			}
			newStage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML // 로그아웃 이벤트 처리
	private void logout_Action(ActionEvent event) {
		// 세션 초기화
		SessionManager.getInstance().clearSession();

		// 로그인 창 열기
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
			Scene scene = new Scene(loader.load(), 800, 600);

			// 현재 Stage 찾기
			Stage currentStage = (Stage) logout_BTN.getScene().getWindow();

			// MainApplicatin의 Scene 설정
			currentStage.setScene(scene);
			Image icon = new Image(
	        		getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고 이미지 파일 경로 지정
	        currentStage.getIcons().add(icon);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setMember(Member member) {
		this.currentMember = member;
		if (this.currentMember == null) {
			System.out.println("DashboardController: setMember called with null member");
		} else {
			System.out.println("DashboardController: Member set with ID - " + currentMember.getId());
			loadMemberInfo();
		}
	}

	private void loadMemberInfo() {
		if (currentMember != null) {
			userNAME_TextField.setText(currentMember.getNickname());
		}
	}
}