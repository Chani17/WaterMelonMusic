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

public class AdminLoginController {
    @FXML
    private TextField adminID;

    @FXML
    private PasswordField adminPW;

    @FXML
    private TextField adminEmail;

    private Stage loginControllerStage;
    
    public void setLoginControllerStage(Stage stage) {
        this.loginControllerStage = stage;
    }
    
    @FXML
    private void handleAdminLogin() {
        String id = adminID.getText();
        String pw = adminPW.getText();
        String email = adminEmail.getText();

        // Admin credentials check
        if (validateAdminCredentials(id, pw, email)) {
        	// Admin 로그인 정보를 세션에 설정
        	Member adminMember = getAdminMember(id);
        	SessionManager.getInstance().setCurrentMember(adminMember);
            
        	try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("adminSong.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("관리자 페이지");
                stage.setScene(new Scene(root));
                Image icon = new Image(
    	        		getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고 이미지 파일 경로 지정
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

    private boolean validateAdminCredentials(String id, String pw, String email) {
    	if (!"admin".equals(id)) {
    		return false;
    	}
    	
    	String query = "SELECT MEMBER_ID FROM MEMBER WHERE MEMBER_ID = ? AND MEMBER_PW = ? AND EMAIL = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
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
    
    private Member getAdminMember(String id) {
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
                byte[] profileImage = null;
                
                BFILE bfile = ((OracleResultSet) resultSet).getBFILE("PROFILE_IMAGE");
                if (bfile != null) {
                    bfile.open();
                    profileImage = bfile.getBytes();
                    bfile.close();
                }

                String gender = resultSet.getString("GENDER");
                java.sql.Date birth = resultSet.getDate("BIRTH");
                member = new Member(memberId, memberPw, email, nickname, profileImage, gender, birth.toLocalDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return member;
    }
}