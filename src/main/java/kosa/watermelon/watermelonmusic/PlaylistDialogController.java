package kosa.watermelon.watermelonmusic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;


public class PlaylistDialogController {
    @FXML
    private ListView<Playlist> playlistListView;

    private ObservableList<Playlist> playlists;

    private PlaylistDAO playlistDAO;
    private Playlist selectedPlaylist;

    @FXML
    public void initialize() {
        playlistDAO = new PlaylistDAO();
        playlists = FXCollections.observableArrayList();
        loadPlaylists();
        playlistListView.setItems(playlists);
        playlistListView.setCellFactory(param -> new ListCell<Playlist>() {
            @Override
            protected void updateItem(Playlist playlist, boolean empty) {
                super.updateItem(playlist, empty);
                if (empty || playlist == null || playlist.getPlaylistName() == null) {
                    setText(null);
                } else {
                    setText(playlist.getPlaylistName());
                }
            }
        });
    }

    private void loadPlaylists() {
        Member currentMember = SessionManager.getInstance().getCurrentMember();
        if (currentMember != null) {
            playlists.setAll(playlistDAO.getPlaylistsByMemberId(currentMember.getId()));
        }
    }

    @FXML
    private void handleOk() {
        selectedPlaylist = playlistListView.getSelectionModel().getSelectedItem();
        // POST와 MPP 테이블에 저장하는 로직 추가
        if (selectedPlaylist != null) {
            playlistDAO.savePlaylistToPostAndMpp(selectedPlaylist);
        }
        closeDialog();
    }

    @FXML
    private void handleCancel() {
        selectedPlaylist = null;
        closeDialog();
    }

    public Playlist getSelectedPlaylist() {
        return selectedPlaylist;
    }

    private void closeDialog() {
        Stage stage = (Stage) playlistListView.getScene().getWindow();
        stage.close();
    }
}