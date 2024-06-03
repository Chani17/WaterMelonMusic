package kosa.watermelon.watermelonmusic;
	
import java.io.IOException;
import java.net.URL;
import java.sql.*;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SongChartController implements Initializable {
    private final static String id = "";
    private final static String pw = "";
    private final static String url = "";

    @FXML private TableView<Song> tableView;

    @FXML private TableColumn<Song, Integer> ranking;

    @FXML private TableColumn<Song, String> songName;

    @FXML private TableColumn<Song, String> artistName;

    @FXML private TableColumn<Song, Void> playBtn;

    @FXML private TableColumn<Song, Void> addBtn;

    @FXML private TableColumn<Song, Void> likebtn;

    @FXML private Button detailButton;

    @FXML private GridPane root;
    
    @FXML private HBox searchContainer;
    
    private TemporaryDB temporaryDB;

    private ContextMenu contextMenu;
    
    private Member currentMember;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        temporaryDB = TemporaryDB.getInstance();
        
        ranking.setCellValueFactory(new PropertyValueFactory<>("id"));
        songName.setCellValueFactory(new PropertyValueFactory<>("name"));
        artistName.setCellValueFactory(new PropertyValueFactory<>("artistName"));
        
        ObservableList<Song> allSongs = FXCollections.observableArrayList(temporaryDB.getSongs());
        tableView.setItems(allSongs);
        
        // Load the search component
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("search.fxml"));
            HBox searchBox = loader.load();
            SearchController searchController = loader.getController();
            searchController.setTableView(tableView);
            searchContainer.getChildren().add(searchBox);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setListView();
        setUpContextMenu();
        setupMyPlaylistButton();
    }

    public void setMember(Member member) {
    	this.currentMember = member;
    }
    
    private void setupMyPlaylistButton() {
        detailButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                contextMenu.show(detailButton,
                        detailButton.localToScreen(detailButton.getBoundsInLocal()).getMinX(),
                                detailButton.localToScreen(detailButton.getBoundsInLocal()).getMinY()+ detailButton.getHeight());
            }
        });
    }

    @FXML
    private void setUpContextMenu() {
        contextMenu = new ContextMenu();

        MenuItem myPlaylistItem = new MenuItem("My Playlist");
        MenuItem myPageItem = new MenuItem("My Page");

        myPlaylistItem.setOnAction(event -> moveToMyPlaylistPage(event));
        myPageItem.setOnAction(event -> moveToMyPage(event));

        contextMenu.getItems().addAll(myPlaylistItem, myPageItem);
//        myPlaylistBtn.setContextMenu(contextMenu);

    }

    private void moveToMyPage(ActionEvent event) {
        try {
            Stage newStage = new Stage();
            Stage stage = (Stage) detailButton.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("mypage.fxml"));
            Parent myPage = loader.load();
            // MyPageController 인스턴스를 가져와서 멤버 설정
            MyPageController controller = loader.getController();
            controller.setMember(currentMember);
            
            Scene scene = new Scene(myPage);

            newStage.setTitle("My Page");
            newStage.setScene(scene);
            newStage.show();

            stage.hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveToMyPlaylistPage(ActionEvent event) {
        try {
            Stage newStage = new Stage();
            Stage stage = (Stage) detailButton.getScene().getWindow();

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
                            Connection conn = DBConnection();

                            try(PreparedStatement pstmt = conn.prepareStatement("SELECT song_file FROM Song WHERE song_id=?")) {
                                pstmt.setLong(1, selectedSong.getId());
                                try(ResultSet res = pstmt.executeQuery()) {
                                    if(res.next()) {
                                        System.out.println("selectedSong.getName() = " + res.getString("song_file"));
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                DBClose(conn);
                            }
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

    private Connection DBConnection() {
        //드라이버 검색 (db와 연동 준비)
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver search success");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver search fail");
            System.exit(0);
        }

        //데이터베이스 연결 - 커넥션 만들기
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url, id, pw);
            System.out.println("Sucess");
        } catch (SQLException e) {
            System.err.println("Fail");
            System.exit(0);
        }
        return conn;
    }

    private void DBClose(Connection conn) {
        try {
            if(conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}