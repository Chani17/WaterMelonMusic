package kosa.watermelon.watermelonmusic;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private Long playlistId;
    private String playlistName;
    private List<Long> songList;
    private String memberId;
    private int number;

    public Playlist(Long playlistId, String playlistName, List<Long> songList, String memberId, int number) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.songList = songList;
        this.memberId = memberId;
        this.number = number;
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

    public int getNumber() {
        return number;
    }
    
    public void addSong(Long songId) {
        this.songList.add(songId);
    }
}
