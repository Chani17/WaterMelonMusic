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

    public void savePlaylistToPostAndMpp(Playlist playlist) {
        String memberId = playlist.getMemberId();
        String postSql = "INSERT INTO POSTING (POST_DATE) VALUES (?)";
        String mppSql = "INSERT INTO MPP (PLAYLIST_ID, POST_ID, MEMBER_ID) VALUES (?, POSTING_SEQ.NEXTVAL, ?)"; // POST_ID는 시퀀스로 자동 생성

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement postStmt = conn.prepareStatement(postSql);
             PreparedStatement mppStmt = conn.prepareStatement(mppSql)) {

            // 현재 날짜 구하기
            LocalDate currentDate = LocalDate.now();

            // POST 테이블에 데이터 삽입
            postStmt.setDate(1, Date.valueOf(currentDate)); // 현재 날짜 설정
            postStmt.executeUpdate();

            // MPP 테이블에 데이터 삽입
            mppStmt.setLong(1, playlist.getPlaylistId());
            mppStmt.setString(2, memberId);
            mppStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}