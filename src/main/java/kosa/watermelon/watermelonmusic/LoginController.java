package kosa.watermelon.watermelonmusic;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import oracle.jdbc.OracleResultSet;
import oracle.sql.BFILE;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

	@FXML
	private Button loginBtn;

	@FXML
	private TextField userID;

	@FXML
	private TextField userPW;
	
	@FXML
	private ImageView profile_Image;

	// private TemporaryDB temporaryDB;

	private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
	private static final String USER = "admin";
	private static final String PASSWORD = "1234";

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// temporaryDB = TemporaryDB.getInstance();
	}

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

				FXMLLoader loader = new FXMLLoader(getClass().getResource("songChart.fxml"));
				Parent songChart = loader.load();

				// SongChartController 인스턴스를 가져와서 멤버 설정
				SongChartController controller = loader.getController();
				controller.setMember(member);
				Scene scene = new Scene(songChart);

				newStage.setTitle("인기 차트!");
				newStage.setScene(scene);
				newStage.show();

				stage.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// 로그인 실패 처리
			System.out.println("Invalid ID or Password");
		}
	}

	private boolean checkIdAndPw(String id, String pw) {
		boolean isValid = false;

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
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

	private Member getMemberById(String id) {
		Member member = null;
		
		String query = "SELECT MEMBER_ID, MEMBER_PW, EMAIL, NICKNAME, PROFILE_IMAGE, GENDER, BIRTH FROM MEMBER WHERE MEMBER_ID = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String memberId = resultSet.getString("MEMBER_ID");
                String memberPw = resultSet.getString("MEMBER_PW");
                String email = resultSet.getString("EMAIL");
                String nickname = resultSet.getString("NICKNAME");
                byte[] profileImage = getProfileImage(resultSet);  // BFILE 읽기
                String gender = resultSet.getString("GENDER");
                java.sql.Date birth = resultSet.getDate("BIRTH");
                member = new Member(memberId, memberPw, email, nickname, profileImage, gender, birth.toLocalDate());
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return member;
    }
	
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