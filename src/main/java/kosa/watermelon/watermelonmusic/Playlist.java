package kosa.watermelon.watermelonmusic;

import java.util.List;

public class Playlist {
    private int playlistId;
    private String playlistName;
    private List<String> songList;

    public Playlist(int playlistId, String playlistName, List<String> songList) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.songList = songList;
    }
}
