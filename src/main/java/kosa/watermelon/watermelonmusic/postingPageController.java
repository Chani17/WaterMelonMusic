package kosa.watermelon.watermelonmusic;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class postingPageController {
	@FXML
	private Button goToMyPage_BTN;
	
	@FXML
    public void initialize() {
        // 초기화 코드 (필요한 경우)
    }
	
	@FXML // 포스팅 → 마이페이지 이동 이벤트 처리
	private void goToMyPage_Action(ActionEvent event)  {
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mypage.fxml"));
            Parent parent = loader.load();
            
            Stage newStage = new Stage();
			Stage currentStage = (Stage) goToMyPage_BTN.getScene().getWindow();
			
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("마이페이지");
			newStage.setScene(new Scene(parent, 600, 464));
			newStage.show();
			currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
