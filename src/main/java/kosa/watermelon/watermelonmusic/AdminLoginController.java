package kosa.watermelon.watermelonmusic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import oracle.jdbc.OracleResultSet;
import oracle.sql.BFILE;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

/**
 * AdminLoginController : 관리자 로그인 처리
 * 
 * 작성자 : 김효정
 */
public class AdminLoginController {
	
	// FXML 필드
	@FXML private TextField adminID;
	@FXML private PasswordField adminPW;
	@FXML private TextField adminEmail;

	private Stage loginControllerStage;

	/**
	 * 로그인 컨트롤러의 Stage 설정
	 * 
	 * @param stage 현재 로그인 컨트롤러의 Stage
	 */
	public void setLoginControllerStage(Stage stage) {
		this.loginControllerStage = stage;
	}

	/**
	 * 관리자 로그인 처리 메서드 관리자 인증에 성공하면 관리자 페이지로 이동
	 */
	@FXML
	private void handleAdminLogin() {
		String id = adminID.getText();
		String pw = adminPW.getText();
		String email = adminEmail.getText();

		// 관리자 확인
		if (validateAdminCredentials(id, pw, email)) {
			// admin 로그인 정보를 세션에 설정
			Member adminMember = getAdminMember(id);
			SessionManager.getInstance().setCurrentMember(adminMember);

			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("adminPage.fxml"));
				Parent root = loader.load();
				Stage stage = new Stage();
				stage.setTitle("관리자 페이지");
				stage.setScene(new Scene(root));
				Image icon = new Image(
						getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png"));
				stage.getIcons().add(icon);
				stage.show();

				adminID.getScene().getWindow().hide();

				if (loginControllerStage != null) {
					loginControllerStage.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("로그인 실패");
			alert.setHeaderText(null);
			alert.setContentText("관리자 권한이 없습니다.");
			alert.showAndWait();
		}
	}

	/**
	 * 관리자 자격 증명을 검증하는 메서드
	 * 
	 * @param id    관리자 ID
	 * @param pw    관리자 PW
	 * @param email 관리자 이메일
	 * @return 관리자 자격이 있는 경우 true, 자격이 없다면 false
	 */
	private boolean validateAdminCredentials(String id, String pw, String email) {
		if (!"admin".equals(id)) {
			return false;
		}

		String query = "SELECT MEMBER_ID FROM MEMBER WHERE MEMBER_ID = ? AND MEMBER_PW = ? AND EMAIL = ?";
		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, id);
			pstmt.setString(2, pw);
			pstmt.setString(3, email);
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 관리자 계정의 Member 객체를 반환하는 메서드
	 * 
	 * @param id 관리자 ID
	 * @return 해당 ID의 Member 객체
	 */
	private Member getAdminMember(String id) {
		Member member = null;
		String query = "SELECT MEMBER_ID, MEMBER_PW, EMAIL, NICKNAME, PROFILE_IMAGE, GENDER, BIRTH FROM MEMBER WHERE MEMBER_ID = ?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				String memberId = rs.getString("MEMBER_ID");
				String memberPw = rs.getString("MEMBER_PW");
				String email = rs.getString("EMAIL");
				String nickname = rs.getString("NICKNAME");
				byte[] profileImage = null;

				BFILE bfile = ((OracleResultSet) rs).getBFILE("PROFILE_IMAGE");
				if (bfile != null) {
					bfile.open();
					profileImage = bfile.getBytes();
					bfile.close();
				}

				String gender = rs.getString("GENDER");
				java.sql.Date birth = rs.getDate("BIRTH");
				member = new Member(memberId, memberPw, email, nickname, profileImage, gender, birth.toLocalDate());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

		return member;
	}
}