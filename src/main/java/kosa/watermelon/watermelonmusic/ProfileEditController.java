package kosa.watermelon.watermelonmusic;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * ProfileEditController 클래스 : 프로필 수정 페이지를 관리하는 컨트롤러 클래스
 */
public class ProfileEditController {

	// FXML 필드
	@FXML private TextField userNAME_TextField;
	@FXML private TextField userPW_TextField;
	@FXML private Button save_BTN;
	@FXML private Button goToMypage_BTN;

	private Member member;

	/**
	 * 회원 정보 설정 메서드
	 * 
	 * @param member 설정할 회원 객체
	 */
	public void setMember(Member member) {
		this.member = member;
		if (member != null) {
			userNAME_TextField.setText(member.getNickname());
			userPW_TextField.setText(member.getPw());
		}
	}

	/**
	 * 닉네임과 비밀번호를 변경하고 저장하는 메서드
	 * 
	 * @param event 이벤트 객체
	 */
	@FXML
	void saveChanges(ActionEvent event) {
		try {
			String newNAME = userNAME_TextField.getText();
			String newPW = userPW_TextField.getText();

			// 닉네임이나 비밀번호 필드가 비어 있는지 확인
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

			if (newNAME.length() > 30) {
				System.out.println("닉네임이 최대 길이를 초과했습니다. 변경할 수 없습니다.");
				userNAME_TextField.setText(member.getNickname()); // 길이를 초과한 경우 텍스트 필드를 초기화
				return;
			}

			if (newPW.length() > 40) {
				System.out.println("비밀번호가 최대 길이를 초과했습니다. 변경할 수 없습니다.");
				userPW_TextField.setText(member.getPw()); // 길이를 초과한 경우 텍스트 필드를 초기화
				return;
			}

			member.setNickname(newNAME);
			member.setPw(newPW);

			MemberController.getInstance().updateMember(member);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("mypage.fxml"));
			Parent parent = loader.load();
			Stage currentStage = (Stage) save_BTN.getScene().getWindow();
			currentStage.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to save changes", e);
		}
	}

	/**
	 * 이전 화면으로 돌아가는 메서드 (profileEDIT 화면 → MyPage 화면)
	 * 
	 * @param event 이벤트 객체
	 */
	@FXML
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