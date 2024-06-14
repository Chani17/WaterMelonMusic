package kosa.watermelon.watermelonmusic;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import oracle.jdbc.OracleResultSet;
import oracle.sql.BFILE;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ResourceBundle;

/**
 * LoginController 클래스 : 로그인 화면을 제어
 */
public class LoginController implements Initializable {
	
	// FXML 필드
	@FXML private Button loginBtn;
	@FXML private Button adminLogin_BTN;
	@FXML private TextField userID;
	@FXML private TextField userPW;
	@FXML private ImageView profile_Image;

	/**
	 * 초기화 메서드
	 * 
	 * @param url            URL 객체
	 * @param resourceBundle ResourceBundle 객체
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		adminLogin_BTN.setOnAction(event -> openAdminLogin());

		userID.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
		userPW.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");

		userID.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.isEmpty()) {
				userID.setPromptText("아이디를 입력하세요");
			} else {
				userID.setPromptText("");
			}
		});

		userID.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				userID.setPromptText("아이디를 입력하세요");
			} else if (userID.getText().isEmpty()) {
				userID.setPromptText("아이디를 입력하세요");
			}
		});

		userPW.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.isEmpty()) {
				userPW.setPromptText("비밀번호를 입력하세요");
			} else {
				userPW.setPromptText("");
			}
		});

		userPW.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				userPW.setPromptText("비밀번호를 입력하세요");
			} else if (userPW.getText().isEmpty()) {
				userPW.setPromptText("비밀번호를 입력하세요");
			}
		});

	}

	/**
	 * 로그인 버튼을 눌렀을 때 호출되는 메서드
	 * 
	 * @param event ActionEvent 객체
	 */
	@FXML
	void selectedLoginBtn(ActionEvent event) {
		String id = userID.getText();
		String pw = userPW.getText();

		if (checkIdAndPw(id, pw)) {
			Member member = getMemberById(id);
			SessionManager.getInstance().setCurrentMember(member);

			if (member != null && member.getProfileImage() != null && profile_Image != null) {
				Image image = new Image(new ByteArrayInputStream(member.getProfileImage()));
				profile_Image.setImage(image);
			}

			try {
				Stage newStage = new Stage();
				Stage stage = (Stage) loginBtn.getScene().getWindow();

				FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
				Parent dashBoard = loader.load();
				Scene scene = new Scene(dashBoard);

				// DashboardController 인스턴스를 가져와서 멤버 설정
				DashboardController controller = loader.getController();
				controller.setMember(member);
				newStage.setTitle("메인 화면");
				newStage.setScene(scene);
				Image icon = new Image(
						getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png"));
				newStage.getIcons().add(icon);
				newStage.show();

				stage.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// 로그인 실패 처리
			System.out.println("Invalid ID or Password");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("로그인 실패");
			alert.setHeaderText(null);
			alert.setContentText("로그인에 실패하였습니다.\n없는 아이디이거나 아이디 또는 비밀번호가 일치하지 않습니다.");
			alert.showAndWait();
		}
	}

	/**
	 * 관리자 로그인 창을 여는 메서드
	 */
	private void openAdminLogin() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("adminLogin.fxml"));
			Parent root = loader.load();

			// AdminLoginController 인스턴스를 가져와서 로그인 컨트롤러 스테이지 설정
			AdminLoginController adminLoginController = loader.getController();
			adminLoginController.setLoginControllerStage((Stage) loginBtn.getScene().getWindow());

			Stage newStage = new Stage();
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("관리자 로그인");
			newStage.setScene(new Scene(root));
			Image icon = new Image(
					getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고
																													// 이미지
																													// 파일
																													// 경로
																													// 지정
			newStage.getIcons().add(icon);
			newStage.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 입력한 ID와 PW를 확인하는 메서드
	 * 
	 * @param id 사용자 ID
	 * @param pw 사용자 PW
	 * @return ID와 PW가 유효한지 여부
	 */
	private boolean checkIdAndPw(String id, String pw) {
		boolean isValid = false;

		try (Connection connection = DBUtil.getConnection()) {
			String sql = "SELECT COUNT(*) FROM Member WHERE member_id = ? AND member_pw = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			statement.setString(2, pw);

			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				isValid = resultSet.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isValid;
	}

	/**
	 * ID로 멤버 정보를 가져오는 메서드
	 * 
	 * @param id 사용자 ID
	 * @return Member 객체
	 */
	private Member getMemberById(String id) {
		Member member = null;

		String query = "SELECT MEMBER_ID, MEMBER_PW, EMAIL, NICKNAME, PROFILE_IMAGE, GENDER, BIRTH FROM MEMBER WHERE MEMBER_ID = ?";
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				String memberId = resultSet.getString("MEMBER_ID");
				String memberPw = resultSet.getString("MEMBER_PW");
				String email = resultSet.getString("EMAIL");
				String nickname = resultSet.getString("NICKNAME");
				byte[] profileImage = getProfileImage(resultSet); // BFILE 읽기
				String gender = resultSet.getString("GENDER");
				java.sql.Date birth = resultSet.getDate("BIRTH");
				member = new Member(memberId, memberPw, email, nickname, profileImage, gender, birth.toLocalDate());
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}

		return member;
	}

	/**
	 * 프로필 이미지를 BFILE에서 읽어오는 메서드
	 * 
	 * @param resultSet ResultSet 객체
	 * @return 프로필 이미지 바이트 배열
	 * @throws SQLException
	 * @throws IOException
	 */
	private byte[] getProfileImage(ResultSet resultSet) throws SQLException, IOException {
		BFILE bfile = ((OracleResultSet) resultSet).getBFILE("PROFILE_IMAGE");
		if (bfile == null) {
			return null;
		}

		bfile.openFile();
		try (InputStream inputStream = bfile.getBinaryStream()) {
			return inputStream.readAllBytes();
		} finally {
			bfile.closeFile();
		}
	}
}