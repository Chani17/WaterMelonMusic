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
			
			// Check if nickname or password fields are empty
	        if (newNAME.isEmpty()) {
	            System.out.println("닉네임을 입력해야 합니다.");
	            userNAME_TextField.setText(member.getNickname()); // 비어있는 경우 텍스트 필드를 초기화
	            return;
	        }

	        if (newPW.isEmpty()) {
	            System.out.println("비밀번호를 입력해야 합니다.");
	            userPW_TextField.setText(member.getPw()); // 비어있는 경우 텍스트 필드를 초기화
	            return;
	        }
			
			if (newNAME.length() > 10) {
	            System.out.println("닉네임이 최대 길이를 초과했습니다. 변경할 수 없습니다.");
	            userNAME_TextField.setText(member.getNickname()); // 길이를 초과한 경우 텍스트 필드를 초기화
	            return;
	        }

	        if (newPW.length() > 20) {
	            System.out.println("비밀번호가 최대 길이를 초과했습니다. 변경할 수 없습니다.");
	            userPW_TextField.setText(member.getPw()); // 길이를 초과한 경우 텍스트 필드를 초기화
	            return;
	        }
	        
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