package kosa.watermelon.watermelonmusic;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;

/**
 * AddArtistController : 아티스트 추가 화면의 컨트롤러
 */
public class AddArtistController implements Initializable {

	@FXML private TextField artistNameField;

	/**
	 * 초기화 메서드로, FXML 파일이 로드된 후 호출됨
	 * 
	 * @param url            초기화 URL
	 * @param resourceBundle 초기화 ResourceBundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

	/**
	 * 아티스트 저장 버튼 클릭 시 호출되는 메서드
	 */
	@FXML
	private void handleSaveArtist() {
		String artistName = artistNameField.getText();

		if (artistName.isEmpty()) {
			// 입력 값 검증
			System.out.println("Artist name is required.");
			return;
		}

		try (Connection conn = DBUtil.getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"INSERT INTO ARTIST (ARTIST_ID, ARTIST_NAME) VALUES (ARTIST_SEQ.NEXTVAL, ?)")) {
			stmt.setString(1, artistName);
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Stage stage = (Stage) artistNameField.getScene().getWindow();
		stage.close();
	}
}
