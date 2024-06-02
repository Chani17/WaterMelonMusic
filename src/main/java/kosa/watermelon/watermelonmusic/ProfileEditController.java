package kosa.watermelon.watermelonmusic;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProfileEditController {
	@FXML
	private TextField userNAME_TextField;

	@FXML
	private TextField userPW_TextField;

	@FXML
	private Button save_BTN;

	@FXML
	private Button goToMypage_BTN;

	private Member member;

	public void setMember(Member member) {
		this.member = member;
		if (member != null) {
			userNAME_TextField.setText(member.getNickname());
			userPW_TextField.setText(member.getPw());
		}
	}

	@FXML
	// 닉네임, 비밀번호 변경 후 저장
	void saveChanges(ActionEvent event) {
		try {
			String newNAME = userNAME_TextField.getText();
			String newPW = userPW_TextField.getText();

			member.setNickname(newNAME);
			member.setPw(newPW);

			// Update the member in the TemporaryDB
			TestDB_mypage.getInstance().updateMember(member);
			// TemporaryDB.getInstance().updateMember(member);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("mypage.fxml"));
			Parent parent = loader.load();
			Stage currentStage = (Stage) save_BTN.getScene().getWindow();
			currentStage.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to save changes", e);
		}
	}

	@FXML
	// 이전 화면으로 뒤로가기 (profileEDIT 화면 → MyPage 화면)
	void goToMypage_Action(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("mypage.fxml"));
			Parent parent = loader.load();
			Stage currentStage = (Stage) goToMypage_BTN.getScene().getWindow();
			currentStage.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed");
		}
	}
}