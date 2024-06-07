package kosa.watermelon.watermelonmusic;

import java.io.IOException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PostingPageController {
	@FXML
	private Button goToDashboard_BTN;
	
	@FXML
    private Button addPlaylist_BTN;
	
	@FXML
    private VBox playlistContainer;
	
	private List<Playlist> playlists; // 사용자가 올린 플레이리스트 목록
	
	@FXML
    public void initialize() {
        // 초기화 코드 (필요한 경우)
		//loadPlaylists();
    }
	
	@FXML // 포스팅 → DashBoard 페이지 이동 이벤트 처리
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
}
