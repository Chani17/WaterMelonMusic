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
import javafx.stage.Stage;
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

    @FXML
    private void handleAdminLogin() {
        String id = adminID.getText();
        String pw = adminPW.getText();
        String email = adminEmail.getText();

        // Admin credentials check
        if (validateAdminCredentials(id, pw, email)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("adminSong.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Admin Dashboard");
                stage.setScene(new Scene(root));
                stage.show();
                // Close the login window
                adminID.getScene().getWindow().hide();
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
        // Check if the ID is "admin"
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
}