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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button loginBtn;

    @FXML
    private TextField userID;

    @FXML
    private TextField userPW;

    //private TemporaryDB temporaryDB;

    private static final String URL = "";
    private static final String USER = "";
    private static final String PASSWORD = "";
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //temporaryDB = TemporaryDB.getInstance();
    }

    @FXML
    void selectedLoginBtn(ActionEvent event) {
        String id = userID.getText();
        String pw = userPW.getText();
        
        if (checkIdAndPw(id, pw)) {
            try {
                Stage newStage = new Stage();
                Stage stage = (Stage) loginBtn.getScene().getWindow();

                Parent songChart = FXMLLoader.load(getClass().getResource("songChart.fxml"));

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
}