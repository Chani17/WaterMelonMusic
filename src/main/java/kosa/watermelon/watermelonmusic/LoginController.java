package kosa.watermelon.watermelonmusic;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button loginBtn;

    @FXML
    private TextField userID;

    @FXML
    private TextField userPW;

    private final static String ID = "admin";
    private final static String PW = "1234";
    private final static String URL = "jdbc:oracle:thin:@localhost:1521:xe";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    void selectedLoginBtn(ActionEvent event) {
        String id = userID.getText();
        String pw = userPW.getText();

        if (checkIdAndPw(id, pw)) {
            try {
                Stage newStage = new Stage();
                Stage stage = (Stage) loginBtn.getScene().getWindow();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("songChart.fxml"));
                Parent songChart = loader.load();

                // SongChartController 인스턴스를 가져와서 멤버 설정
                SongChartController controller = loader.getController();
                Member member = getMemberById(id);
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

        try (Connection connection = DriverManager.getConnection(URL, ID, PW)) {
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

        try (Connection connection = DriverManager.getConnection(URL, ID, PW)) {
            String sql = "SELECT member_id, member_pw, email, nickname, gender, birth FROM Member WHERE member_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String memberId = resultSet.getString("member_id");
                String memberPw = resultSet.getString("member_pw");
                String email = resultSet.getString("email");
                String nickname = resultSet.getString("nickname");
                String gender = resultSet.getString("gender");
                java.sql.Date birth = resultSet.getDate("birth");

                member = new Member(memberId, memberPw, nickname, email, gender, birth.toLocalDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return member;
    }
}
