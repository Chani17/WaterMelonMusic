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
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button loginBtn;

    @FXML
    private TextField userID;

    @FXML
    private TextField userPW;

    private TemporaryDB temporaryDB;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        temporaryDB = TemporaryDB.getInstance();
    }

    @FXML
    void selectedLoginBtn(ActionEvent event) {
        String id = userID.getText();
        String pw = userPW.getText();
        temporaryDB.checkIdAndPw(id, pw);

        try {
            Stage newStage = new Stage();
            Stage stage = (Stage)loginBtn.getScene().getWindow();

            Parent songChart = FXMLLoader.load(getClass().getResource("songChart.fxml"));

            Scene scene = new Scene(songChart);

            newStage.setTitle("인기 차트!");
            newStage.setScene(scene);
            newStage.show();

            stage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
