package kosa.watermelon.watermelonmusic;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PlaylistController implements Initializable {

    private TemporaryDB temporaryDB;
    private Map<Song, Boolean> selectedSong;

    @FXML private TableView<Song> playlistView;
    @FXML private TableColumn<Song, Boolean> check;
    @FXML private TableColumn<Song, String> songName;
    @FXML private TableColumn<Song, String> artist;
    @FXML private TableColumn<Song, Void> playBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        temporaryDB = TemporaryDB.getInstance();
        selectedSong = new HashMap<>();
        setListView();
    }

    private void setListView() {
        ObservableList<Song> songList = FXCollections.observableArrayList(temporaryDB.getMyPlaylist());

        for (Song song : songList) {
            selectedSong.put(song, false);
        }

        check.setCellValueFactory(data -> {
            Song song = data.getValue();
            return new ObservableValue<Boolean>() {
                @Override
                public void addListener(ChangeListener<? super Boolean> changeListener) {
                    selectedSong.put(song, !selectedSong.get(song));
                }

                @Override
                public void removeListener(ChangeListener<? super Boolean> changeListener) {
                    selectedSong.put(song, !selectedSong.get(song));
                }

                @Override
                public Boolean getValue() {
                    return selectedSong.get(song);
                }

                @Override
                public void addListener(InvalidationListener invalidationListener) {

                }

                @Override
                public void removeListener(InvalidationListener invalidationListener) {

                }
            };
        });

        check.setCellFactory(new Callback<TableColumn<Song, Boolean>, TableCell<Song, Boolean>>() {
            @Override
            public TableCell<Song, Boolean> call(TableColumn<Song, Boolean> param) {
                return new TableCell<Song, Boolean>() {
                    private final CheckBox checkBox = new CheckBox();

                    {
                        checkBox.setOnAction(event -> {
                            Song song = getTableView().getItems().get(getIndex());
                            selectedSong.put(song, checkBox.isSelected());
                        });
                    }

                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Song song = getTableView().getItems().get(getIndex());
                            checkBox.setSelected(selectedSong.get(song));
                            setGraphic(checkBox);
                        }
                    }
                };
            }
        });

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
