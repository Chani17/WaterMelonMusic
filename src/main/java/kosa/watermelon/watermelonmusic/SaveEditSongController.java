package kosa.watermelon.watermelonmusic;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SaveEditSongController  {

    @FXML private TextField songNameField;
    @FXML private Button saveButton;
    private String songName;

    @FXML
    private void save() {
        this.songName = songNameField.getText();
        close();
    }

    private void close() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public String getSongName() {
        return songName;
    }
}
