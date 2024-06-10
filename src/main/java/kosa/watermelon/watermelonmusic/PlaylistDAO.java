package kosa.watermelon.watermelonmusic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO {
	// 데이터베이스에서 플레이리스트 데이터를 가져오는 DAO 클래스
	
    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT PLAYLIST_ID, PLAYLIST_NAME, MEMBER_ID FROM PLAYLIST";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Long playlistID = rs.getLong("PLAYLIST_ID");
                String playlistName = rs.getString("PLAYLIST_NAME");
                String memberId = rs.getString("MEMBER_ID");
                playlists.add(new Playlist(playlistID, playlistName, memberId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playlists;
    }
    
    public List<Playlist> getPlaylistsByMemberId(String memberId) {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT PLAYLIST_ID, PLAYLIST_NAME, MEMBER_ID FROM PLAYLIST WHERE MEMBER_ID = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Long playlistID = rs.getLong("PLAYLIST_ID");
                String playlistName = rs.getString("PLAYLIST_NAME");
                playlists.add(new Playlist(playlistID, playlistName, memberId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playlists;
    }
}