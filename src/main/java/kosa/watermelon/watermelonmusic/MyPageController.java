package kosa.watermelon.watermelonmusic;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MyPageController implements Initializable {

	@FXML private ImageView profile_Image;
	@FXML private Button profileEdit_BTN;
	@FXML private Button goToDashboard_BTN;
	@FXML private Button logout_BTN;
	@FXML private TextField userNAME_TextField;
	@FXML private TextField userID_TextField;
	@FXML private TextField userEMAIL_TextField;
	@FXML private TextField userGender_TextField;
	@FXML private TextField userBirth_TextField;
	@FXML private TilePane playlistImage_TilePane;
	@FXML private Label focusLabel; // 마이페이지 텍스트필드에 커서 깜빡이지 않도록 수정
	
	private String[] playlist_ImageUrls = {
			"https://i.pinimg.com/564x/35/23/86/352386ce038dd4de00f3fb832785dbb4.jpg",
			"https://i.pinimg.com/564x/0a/9e/69/0a9e69096943ed19407cf02991957cd1.jpg",
			"https://i.pinimg.com/564x/9b/11/6c/9b116c0ec240443e5937bf782fa117aa.jpg",
			"https://i.pinimg.com/564x/46/e5/8a/46e58af50c57bda8bc9ce3cf15746628.jpg",
			"https://i.pinimg.com/564x/a3/17/d9/a317d91b9e10272dbae7c07180a8bdcd.jpg",
			"https://i.pinimg.com/564x/65/d5/71/65d5718995458b549eacae3c40153168.jpg",
			"https://i.pinimg.com/564x/19/00/4d/19004df230620eada6d5b0726ac035ca.jpg",
			"https://i.pinimg.com/564x/a5/da/a6/a5daa6a5133355111ecdee0c7e67b729.jpg",
			"https://i.pinimg.com/564x/11/48/9b/11489b8f2ac4e1dc98e876b445af93d6.jpg",
			"https://i.pinimg.com/564x/bb/37/b8/bb37b8da81fadf1f14637d9e475198cc.jpg"
			// 추가 URL을 여기에 넣기
	};

	private Member currentMember;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		this.currentMember = SessionManager.getInstance().getCurrentMember();
        if (currentMember != null) {
            System.out.println("MyPageController: Member set with ID - " + currentMember.getId());
            loadMemberInfo();
        } else {
            System.out.println("Error: currentMember is null.");
        }
        
		// TextField를 수정 불가능하게 설정
		userNAME_TextField.setEditable(false);
		userID_TextField.setEditable(false);
		userEMAIL_TextField.setEditable(false);
		userGender_TextField.setEditable(false);
		userBirth_TextField.setEditable(false);
	    
		loadPlaylistImage();
		
        // TextField에 포커스를 제거하고 다른 곳으로 포커스를 설정
		Platform.runLater(() -> focusLabel.requestFocus());
	}

	public void setMember(Member member) {
		this.currentMember = member;
        if (this.currentMember == null) {
            System.out.println("MyPageController: setMember called with null member");
        } else {
            System.out.println("MyPageController: Member set with ID - " + currentMember.getId());
            loadMemberInfo();
        }
    }

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
	
	// 만든 플레이리스트 항목을 넣고 싶은데 일단 임시로 외부 이미지 파일을 연결
	private void loadPlaylistImage() {
		for (String playlist_ImageUrl : playlist_ImageUrls) {
			try {
				Image playlist_Image = new Image(playlist_ImageUrl);
                ImageView playlist_ImageView = new ImageView(playlist_Image);
                playlist_ImageView.setFitWidth(110); // 필요한 크기로 조정
                playlist_ImageView.setFitHeight(110); // 필요한 크기로 조정
                playlist_ImageView.setPreserveRatio(true);

                playlistImage_TilePane.getChildren().add(playlist_ImageView);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Failed to load image from URL : " + playlist_ImageUrl);
			}
		}
	}

	@FXML // 마이페이지 → 프로필 편집 페이지 생성 이벤트 처리
	private void profileEdit_Action(ActionEvent event) {
		try {
			// FXML 파일 로드
			FXMLLoader loader = new FXMLLoader(getClass().getResource("profileEDIT.fxml"));
			Parent parent = loader.load();

			// ProfileEditController 인스턴스를 가져와서 멤버 설정
			ProfileEditController controller = loader.getController();
			controller.setMember(currentMember);

			Stage newStage = new Stage();
		
			newStage.initModality(Modality.APPLICATION_MODAL); // 새로운 Stage를 모달로 설정
			newStage.setTitle("프로필 편집");
			newStage.setScene(new Scene(parent, 300, 200));
			newStage.showAndWait();

			// 프로필 수정 후 업데이트
			userNAME_TextField.setText(currentMember.getNickname());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML // 마이페이지 → DashBoard 페이지 이동 이벤트 처리
	private void goToDashboard_Action(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
			Parent parent = loader.load();
			
			// DashboardController 인스턴스를 가져와서 멤버 설정
			DashboardController controller = loader.getController();
			controller.setMember(currentMember);
			
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
}