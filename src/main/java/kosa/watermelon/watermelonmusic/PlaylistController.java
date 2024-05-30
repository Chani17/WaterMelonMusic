package kosa.watermelon.watermelonmusic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class PlaylistController implements Initializable {

    private TemporaryDB temporaryDB;

    @FXML private TableView<Song> playlistView;
    @FXML private TableColumn<Song, Boolean> check;
    @FXML private TableColumn<Song, String> songName;
    @FXML private TableColumn<Song, String> artist;
    @FXML private TableColumn<Song, Void> playBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        temporaryDB = TemporaryDB.getInstance();
        setListView();
    }

    private void setListView() {
        ObservableList<Song> songList = FXCollections.observableArrayList(temporaryDB.getMyPlaylist());
        check.setCellValueFactory(new PropertyValueFactory<Song, Boolean>("checkBox"));
        songName.setCellValueFactory(new PropertyValueFactory<Song, String>("name"));
        artist.setCellValueFactory(new PropertyValueFactory<Song, String>("artist"));
        playlistView.setItems(songList);

        playBtn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Song, Void> call(TableColumn<Song, Void> param) {
                return new TableCell<>() {
                    private final Button playButton = new Button();
                    {
                        Image btnImg = new Image(getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/playButton.png"));
                        ImageView imageView = new ImageView(btnImg);
                        imageView.setFitHeight(20);
                        imageView.setFitWidth(20);
                        playButton.setGraphic(imageView);
                        playButton.setOnAction(event -> {
                            Song selectedSong = getTableView().getItems().get(getIndex());
                            System.out.println(selectedSong.getName() + "를 재생합니다.");
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(playButton);
                        }
                    }
                };
            }
        });

        playlistView.setItems(songList);
    }
}
