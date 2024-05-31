package kosa.watermelon.watermelonmusic;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    @FXML private Button delete;
    @FXML private Button deleteAll;
    @FXML private Button back;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        temporaryDB = TemporaryDB.getInstance();
        selectedSong = new HashMap<>();
        setListView();

        delete.setOnAction(this::handleDeleteAction);
        deleteAll.setOnAction(this::handDeleteAllAction);
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
                    System.out.println(selectedSong.get(song));
                }

                @Override
                public void removeListener(ChangeListener<? super Boolean> changeListener) {
                    selectedSong.put(song, !selectedSong.get(song));
                    System.out.println(selectedSong.get(song));
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

    @FXML
    private void handleDeleteAction(ActionEvent event) {
        ObservableList<Song> songs = playlistView.getItems();
        songs.removeIf(song -> selectedSong.get(song));
        selectedSong.keySet().removeIf(song -> !songs.contains(song));
    }

    @FXML
    private void handDeleteAllAction(ActionEvent event) {
        ObservableList<Song> songs = playlistView.getItems();
        songs.clear();
        selectedSong.clear();
    }

    // 페이지 되돌아가기(My playlist -> 인기 차트)
    @FXML
    private void backToPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("songChart.fxml"));
            Parent parent = loader.load();

            Stage newStage = new Stage();
            Stage currentStage = (Stage) back.getScene().getWindow();
            newStage.setTitle("인기 차트!");
            newStage.setScene(new Scene(parent, 600, 464));
            newStage.show();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
