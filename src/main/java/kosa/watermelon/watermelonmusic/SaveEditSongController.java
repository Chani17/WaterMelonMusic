package kosa.watermelon.watermelonmusic;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * SaveEditSongController 클래스 : 곡 이름을 저장 및 수정하는 컨트롤러 클래스
 * 
 * 작성자 : 김찬희
 */
public class SaveEditSongController {

	// FXML 필드
    @FXML private TextField songNameField;
    @FXML private Button saveButton;
    
    private String songName;

	/**
	 * 곡 이름을 저장하는 메서드
	 */
	@FXML
	private void save() {
		this.songName = songNameField.getText();
		close();
	}

	/**
	 * 현재 창을 닫는 메서드
	 */
	private void close() {
		Stage stage = (Stage) saveButton.getScene().getWindow();
		stage.close();
	}

	/**
	 * 저장된 곡 이름을 반환하는 메서드
	 * 
	 * @return 곡 이름
	 */
	public String getSongName() {
		return songName;
	}
}
