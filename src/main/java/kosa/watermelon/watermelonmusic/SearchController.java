package kosa.watermelon.watermelonmusic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class SearchController {

    @FXML private TextField search_TextField;

    @FXML private Button search_BTN;

    private TemporaryDB temporaryDB = TemporaryDB.getInstance();

    private TableView<Song> tableView;

    public void setTableView(TableView<Song> tableView) {
        this.tableView = tableView;
    }

    @FXML
    void search_Action(ActionEvent event) {
        String searchSong = search_TextField.getText();
        ObservableList<Song> searchResults = FXCollections.observableArrayList(temporaryDB.searchSongs(searchSong));
        tableView.setItems(searchResults);
		System.out.println("검색된 단어 : " + searchSong);
	}
}