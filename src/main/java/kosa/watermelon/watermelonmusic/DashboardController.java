package kosa.watermelon.watermelonmusic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class DashboardController implements Initializable {
	// 버튼 모아둔 페이지 (로그인 후 이동되는 페이지)
	@FXML
	private ImageView logo_ImageView;
	@FXML
    private ImageView SongChart_ImageView;
    @FXML
    private ImageView Search_ImageView;
    @FXML
    private ImageView PostingPage_ImageView;
    @FXML
    private ImageView MusicEdit_ImageView;
    @FXML
    private ImageView MyPage_ImageView;
    @FXML
    private ImageView LikeSongs_ImageView;
    @FXML
    private ImageView Playlist_ImageView;
    @FXML
    private ImageView AdminLogin_ImageView;
    @FXML
    private TextField userNAME_TextField;
    @FXML
    private Label focusLabel; // 마이페이지 텍스트필드에 커서 깜빡이지 않도록 수정
    @FXML
    private Button logout_BTN;
    
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
    	
		Image logo_Icon = new Image(getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo.png"));
    	Image SongChart_Icon = new Image(getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/image/dashBoardIcon/songChart_icon.png"));
    	Image Search_Icon = new Image(getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/image/dashBoardIcon/search_icon.png"));
    	Image PostingPage_Icon = new Image(getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/image/dashBoardIcon/postingPage_icon.png"));
    	Image MusicEdit_Icon = new Image(getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/image/dashBoardIcon/editSong_icon.png"));
    	Image MyPage_Icon = new Image(getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/image/dashBoardIcon/myPage_icon.png"));
    	Image LikeSongs_Icon = new Image(getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/image/dashBoardIcon/likeSong_icon.png"));
    	Image Playlist_Icon = new Image(getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/image/dashBoardIcon/playlist_icon.png"));
    	Image AdminLogin_Icon = new Image(getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/image/dashBoardIcon/admin_icon.png"));
	    
		logo_ImageView.setImage(logo_Icon);
	    SongChart_ImageView.setImage(SongChart_Icon);
	    Search_ImageView.setImage(Search_Icon);
	    PostingPage_ImageView.setImage(PostingPage_Icon);
	    MusicEdit_ImageView.setImage(MusicEdit_Icon);
	    MyPage_ImageView.setImage(MyPage_Icon);
	    LikeSongs_ImageView.setImage(LikeSongs_Icon);
	    Playlist_ImageView.setImage(Playlist_Icon);
	    AdminLogin_ImageView.setImage(AdminLogin_Icon);
	    
	    SongChart_ImageView.setOnMouseClicked(event -> goToPage("songChart.fxml", SongChart_ImageView));
	    Search_ImageView.setOnMouseClicked(event -> goToPage("songChart.fxml", Search_ImageView));
	    PostingPage_ImageView.setOnMouseClicked(event -> goToPage("postingPage.fxml", PostingPage_ImageView));
//	    MusicEdit_ImageView.setOnMouseClicked(event -> goToPage("musicEdit.fxml", MusicEdit_ImageView));
	    MyPage_ImageView.setOnMouseClicked(event -> goToPage("mypage.fxml", MyPage_ImageView));
//	    LikeSongs_ImageView.setOnMouseClicked(event -> goToPage("likeSongs.fxml", LikeSongs_ImageView));
	    Playlist_ImageView.setOnMouseClicked(event -> goToPage("playlist.fxml", Playlist_ImageView));
//	    AdminLogin_ImageView.setOnMouseClicked(event -> goToPage("adminLogin.fxml", AdminLogin_ImageView));
	    
	    // TextField를 수정 불가능하게 설정
	 	userNAME_TextField.setEditable(false);
	 	
	    // TextField에 포커스를 제거하고 다른 곳으로 포커스를 설정
	 	Platform.runLater(() -> focusLabel.requestFocus());
    }
    
    private void goToPage(String fxmlFile, ImageView sourceImageView) {
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
    		Stage newStage = (Stage) sourceImageView.getScene().getWindow();
    		Scene scene = new Scene(loader.load());
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
			Scene scene = new Scene(loader.load(), 600, 464);
			
			// 현재 Stage 찾기
			Stage currentStage = (Stage) logout_BTN.getScene().getWindow();
			
			// MainApplicatin의 Scene 설정
			currentStage.setScene(scene);
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