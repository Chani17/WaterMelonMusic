package kosa.watermelon.watermelonmusic;
	
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SongChartController implements Initializable {
    @FXML
    private TableView<Song> tableView;

    @FXML
    private TableColumn<Song, Integer> ranking;

    @FXML
    private TableColumn<Song, String> songName;

    @FXML
    private TableColumn<Song, String> artistName;

    @FXML
    private TableColumn<Song, Void> playBtn;

    @FXML
    private TableColumn<Song, Void> addBtn;

    @FXML
    private TableColumn<Song, Void> likebtn;

    @FXML
    private Button myPlaylistBtn;

    private TemporaryDB temporaryDB;

    private ContextMenu contextMenu;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        temporaryDB = TemporaryDB.getInstance();
        setListView();
        setUpContextMenu();
        setupMyPlaylistButton();
    }

    private void setupMyPlaylistButton() {
        myPlaylistBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                contextMenu.show(myPlaylistBtn,
                        myPlaylistBtn.localToScreen(myPlaylistBtn.getBoundsInLocal()).getMinX(),
                                myPlaylistBtn.localToScreen(myPlaylistBtn.getBoundsInLocal()).getMinY()+myPlaylistBtn.getHeight());
            }
        });
    }

    @FXML
    private void setUpContextMenu() {
        contextMenu = new ContextMenu();

        MenuItem myPlaylistItem = new MenuItem("My Playlist");
        MenuItem myPageItem = new MenuItem("My Page");

        myPlaylistItem.setOnAction(event -> moveToMyPlaylistPage(event));

        contextMenu.getItems().addAll(myPlaylistItem, myPageItem);
//        myPlaylistBtn.setContextMenu(contextMenu);

    }

    private void moveToMyPlaylistPage(ActionEvent event) {
        try {
            Stage newStage = new Stage();
            Stage stage = (Stage)myPlaylistBtn.getScene().getWindow();

            Parent playlist = FXMLLoader.load(getClass().getResource("playlist.fxml"));

            Scene scene = new Scene(playlist);

            newStage.setTitle("My Playlist");
            newStage.setScene(scene);
            newStage.show();

            stage.hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setListView() {
        ObservableList<Song> songList = FXCollections.observableArrayList(temporaryDB.getSongs());
        ranking.setCellValueFactory(new PropertyValueFactory<Song, Integer>("id"));
        songName.setCellValueFactory(new PropertyValueFactory<Song, String>("name"));
        artistName.setCellValueFactory(new PropertyValueFactory<Song, String>("artist"));
        tableView.setItems(songList);

        playBtn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Song, Void> call(TableColumn<Song, Void> param) {
                return new TableCell<>() {
                    private final Button playButton = new Button("▶");
                    {
                        // 버튼 클릭 시 이벤트 처리
                        playButton.setOnAction(event -> {
                            Song selectedSong = getTableView().getItems().get(getIndex());
                            selectedSong.setClickCnt();
                            System.out.println("selectedSong.getName() = " + selectedSong.getName());
                            System.out.println("selectedSong.getClickCnt() = " + selectedSong.getClickCnt());
                        });
                    }

                    // 셸 Rendering
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) setGraphic(null);
                        else {
                            setGraphic(playButton);
                            setAlignment(Pos.CENTER);
                        }
                    }
                };
            }
        });

        addBtn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Song, Void> call(TableColumn<Song, Void> param) {
                return new TableCell<>() {
                    private final Button addButton = new Button("+");
                    {
                        // 버튼 클릭 시 이벤트 처리
                        addButton.setOnAction(event -> {
                            Song selectedSong = getTableView().getItems().get(getIndex());
                            temporaryDB.setMyPlaylist(selectedSong);
                        });
                    }

                    // 셸 Rendering
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) setGraphic(null);
                        else {
                            setGraphic(addButton);
                            setAlignment(Pos.CENTER);
                        }
                    }
                };
            }
        });

        likebtn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Song, Void> call(TableColumn<Song, Void> param) {
                return new TableCell<>() {
                    private final Button likeButton = new Button("❤");
                    {
                        // 버튼 클릭 시 이벤트 처리
                        likeButton.setOnAction(event -> {
                            Song selectedSong = getTableView().getItems().get(getIndex());
                            selectedSong.setLikeCnt();
                            System.out.println("selectedSong.getName() = " + selectedSong.getName());
                            System.out.println("selectedSong.getLikeCnt() = " + selectedSong.getLikeCnt());
                        });
                    }

                    // 셸 Rendering
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) setGraphic(null);
                        else {
                            setGraphic(likeButton);
                            setAlignment(Pos.CENTER);
                        }
                    }
                };
            }
        });
    }
}
