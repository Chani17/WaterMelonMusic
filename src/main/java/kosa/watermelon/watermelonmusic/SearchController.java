package kosa.watermelon.watermelonmusic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class SearchController {

    @FXML private TextField search_TextField;

    @FXML private Button search_BTN;
    
    @FXML private ImageView search_ImageView;

    private TemporaryDB temporaryDB = TemporaryDB.getInstance();

    private TableView<Song> tableView;

    public void setTableView(TableView<Song> tableView) {
        this.tableView = tableView;
    }

    @FXML
    public void initialize() {
        search_TextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");

        search_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                search_TextField.setPromptText("검색어를 입력하세요");
            } else {
                search_TextField.setPromptText("");
            }
        });

        search_TextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                search_TextField.setPromptText("검색어를 입력하세요");
            } else if (search_TextField.getText().isEmpty()) {
                search_TextField.setPromptText("검색어를 입력하세요");
            }
        });
    }
    
    @FXML
    void search_Action(ActionEvent event) {
        String searchSong = search_TextField.getText();
        ObservableList<Song> searchResults = FXCollections.observableArrayList(temporaryDB.searchSongs(searchSong));
        tableView.setItems(searchResults);
		System.out.println("검색된 단어 : " + searchSong);
	}
}