package kosa.watermelon.watermelonmusic;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private int playlistId;
    private String playlistName;
    private List<Song> songList;
    private String memberId;

    public Playlist(int playlistId, String playlistName, String memberId) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.songList = new ArrayList<>();
        this.memberId = memberId;
    }

    public int getPlaylistID() {
    	return playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public String getMemberId() {
        return memberId;
    }

    public void addSong(Song song) {
        this.songList.add(song);
    }
}
