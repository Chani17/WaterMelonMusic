package kosa.watermelon.watermelonmusic;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminPageController implements Initializable {

    @FXML private TableView<Artist> artistTableView;
    @FXML private TableColumn<Artist, String> artistNameColumn;
    @FXML private TableView<Album> albumTableView;
    @FXML private TableColumn<Album, String> albumNameColumn;
    @FXML private TableColumn<Album, String> artistNameForAlbumColumn;
    @FXML private TableView<SongFXModel> songTableView;
    @FXML private TableColumn<SongFXModel, String> songNameColumn;
    @FXML private TableColumn<SongFXModel, String> albumNameForSongColumn;
    @FXML private TableColumn<SongFXModel, String> artistNameForSongColumn;
    @FXML private Button addSongButton;
    @FXML private Button addArtistButton;
    @FXML private Button addAlbumButton;

    private List<Artist> artistList = new ArrayList<>();
    private List<Album> albumList = new ArrayList<>();
    private List<SongFXModel> songList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TableColumn 초기화
        artistNameColumn.setCellValueFactory(cellData -> cellData.getValue().artistNameProperty());
        albumNameColumn.setCellValueFactory(cellData -> cellData.getValue().albumNameProperty());
        artistNameForAlbumColumn.setCellValueFactory(cellData -> cellData.getValue().artistNameProperty());
        songNameColumn.setCellValueFactory(cellData -> cellData.getValue().songNameProperty());
        albumNameForSongColumn.setCellValueFactory(cellData -> cellData.getValue().albumNameProperty());
        artistNameForSongColumn.setCellValueFactory(cellData -> cellData.getValue().artistProperty());

        loadArtists();
        loadAlbums();
        loadSongs();
    }

    private void loadArtists() {
        artistList.clear();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ARTIST");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                artistList.add(new Artist(rs.getInt("ARTIST_ID"), rs.getString("ARTIST_NAME")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        artistTableView.getItems().setAll(artistList);
    }

    private void loadAlbums() {
        albumList.clear();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT a.ALBUM_ID, a.ALBUM_NAME, ar.ARTIST_NAME FROM ALBUM a JOIN ARTIST ar ON a.ARTIST_ID = ar.ARTIST_ID");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                albumList.add(new Album(rs.getInt("ALBUM_ID"), rs.getString("ALBUM_NAME"), rs.getString("ARTIST_NAME")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        albumTableView.getItems().setAll(albumList);
    }

    private void loadSongs() {
        songList.clear();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT s.SONG_ID, s.SONG_NAME, a.ALBUM_NAME, ar.ARTIST_NAME, s.CLICK_COUNT, s.SONG_FILE, a.ALBUM_COVER " +
                     "FROM SONG s JOIN ALBUM a ON s.ALBUM_ID = a.ALBUM_ID JOIN ARTIST ar ON s.ARTIST_ID = ar.ARTIST_ID");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                songList.add(new SongFXModel(
                        rs.getInt("SONG_ID"),
                        rs.getString("ARTIST_NAME"),
                        rs.getString("ALBUM_NAME"),
                        rs.getString("SONG_NAME"),
                        rs.getString("SONG_FILE"),
                        rs.getLong("CLICK_COUNT")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        songTableView.getItems().setAll(songList);
    }

    @FXML
    private void handleAddNewSong() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addSong.fxml"));
            Parent root = loader.load();

            AddSongController controller = loader.getController();
            controller.setArtists(artistList);
            controller.setAlbums(albumList);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Song");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // 사용자가 창을 닫을 때까지 기다림

            // 창이 닫힌 후 데이터 다시 로드
            loadArtists();
            loadAlbums();
            loadSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddNewArtist() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addArtist.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Artist");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // 사용자가 창을 닫을 때까지 기다림

            // 창이 닫힌 후 데이터 다시 로드
            loadArtists();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddNewAlbum() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addAlbum.fxml"));
            Parent root = loader.load();

            AddAlbumController controller = loader.getController();
            controller.setArtists(artistList);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Album");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // 사용자가 창을 닫을 때까지 기다림

            // 창이 닫힌 후 데이터 다시 로드
            loadAlbums();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
