package kosa.watermelon.watermelonmusic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistSelectionController {

    @FXML
    private ComboBox<String> playlistComboBox;

    @FXML
    private TextField newPlaylistName;

    private long selectedSongId;
    private Member currentMember;

    @FXML
    public void initialize() {
        if (currentMember != null) {
            loadPlaylists();
        } else {
            // 초기화 코드 추가
            System.out.println("currentMember is null in initialize");
        }
        
        
        // ComboBox의 항목 폰트를 설정하는 셀 팩토리 설정
        playlistComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item);
                            setFont(Font.font("D2Coding", 15));
                        }
                    }
                };
            }
        });

        playlistComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item);
                    setFont(Font.font("D2Coding", 15));
                }
            }
        });
    }

    public void setSongId(long songId) {
        this.selectedSongId = songId;
    }

    public void setCurrentMember(Member member) {
        this.currentMember = member;
        if (currentMember != null) {
            loadPlaylists();
        } else {
            System.out.println("currentMember is null in setCurrentMember");
        }
    }

    private void loadPlaylists() {
        List<String> playlistNames = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT playlist_name FROM Playlist WHERE member_id = ?")) {
            pstmt.setString(1, currentMember.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                playlistNames.add(rs.getString("playlist_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObservableList<String> options = FXCollections.observableArrayList(playlistNames);
        playlistComboBox.setItems(options);
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        String selectedPlaylistName = playlistComboBox.getValue();
        String newPlaylist = newPlaylistName.getText();

        if (selectedPlaylistName != null && !selectedPlaylistName.isEmpty()) {
            // 선택된 기존 플레이리스트에 곡 추가
            addSongToExistingPlaylist(selectedPlaylistName);
        } else if (newPlaylist != null && !newPlaylist.isEmpty()) {
            // 새로운 플레이리스트 생성 및 곡 추가
            createNewPlaylistAndAddSong(newPlaylist);
        }

        // 창 닫기
        Stage stage = (Stage) playlistComboBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        // 창 닫기
        Stage stage = (Stage) playlistComboBox.getScene().getWindow();
        stage.close();
    }

    private void addSongToExistingPlaylist(String playlistName) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE Playlist SET Song = ? WHERE playlist_name = ? AND member_id = ?")) {

            // 기존 플레이리스트에 곡 추가
            Playlist playlist = getPlaylistByName(playlistName, conn);
            if (playlist != null && !playlist.getSongList().contains(selectedSongId)) {
                playlist.getSongList().add(selectedSongId);
                Long[] newSongs = playlist.getSongList().toArray(new Long[0]);

                ArrayDescriptor desc = ArrayDescriptor.createDescriptor("SONG_ARRAY", conn);
                ARRAY newSongArray = new ARRAY(desc, conn, newSongs);

                pstmt.setArray(1, newSongArray);
                pstmt.setString(2, playlistName);
                pstmt.setString(3, currentMember.getId());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createNewPlaylistAndAddSong(String playlistName) {
        try (Connection conn = DBUtil.getConnection()) {
            long newPlaylistId = generateNewPlaylistId(conn);

            // 새로운 플레이리스트 생성
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Playlist (playlist_id, playlist_name, member_id, Song) VALUES (?, ?, ?, ?)")) {
                List<Long> songList = new ArrayList<>();
                songList.add(selectedSongId);
                Long[] newSongs = songList.toArray(new Long[0]);

                ArrayDescriptor desc = ArrayDescriptor.createDescriptor("SONG_ARRAY", conn);
                ARRAY newSongArray = new ARRAY(desc, conn, newSongs);

                pstmt.setLong(1, newPlaylistId);
                pstmt.setString(2, playlistName);
                pstmt.setString(3, currentMember.getId());
                pstmt.setArray(4, newSongArray);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Playlist getPlaylistByName(String playlistName, Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Playlist WHERE playlist_name = ? AND member_id = ?")) {
            pstmt.setString(1, playlistName);
            pstmt.setString(2, currentMember.getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Array songArray = rs.getArray("Song");
                BigDecimal[] songs = (BigDecimal[]) songArray.getArray();
                List<Long> songList = new ArrayList<>();
                for (BigDecimal bd : songs) {
                    songList.add(bd.longValue());
                }
                return new Playlist(rs.getLong("playlist_id"), rs.getString("playlist_name"), songList, rs.getString("member_id"));
            } else {
                return null;
            }
        }
    }

    private Long generateNewPlaylistId(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT MAX(playlist_id) FROM Playlist")) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1) + 1;
            } else {
                return 1L;
            }
        }
    }
}