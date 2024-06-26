package kosa.watermelon.watermelonmusic;

public class PlaylistSong {

    private Long songId;
    private String songName;
    private String artistName;

    public PlaylistSong(Long songId, String song_name, String artistName) {
        this.songId = songId;
        this.songName = song_name;
        this.artistName = artistName;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setSongName(String song_name) {
        this.songName = song_name;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Long getSongId() {
        return songId;
    }
}
