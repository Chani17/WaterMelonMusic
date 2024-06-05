package kosa.watermelon.watermelonmusic;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private Long playlistId;
    private String playlistName;
    private List<Long> songList;
    private String memberId;

    public Playlist(Long playlistId, String playlistName, String memberId) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.songList = new ArrayList<>();
        this.memberId = memberId;
    }

    public Long getPlaylistID() {
    	return playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public List<Long> getSongList() {
        return songList;
    }

    public String getMemberId() {
        return memberId;
    }

    public void addSong(Long songId) {
        this.songList.add(songId);
    }
}
