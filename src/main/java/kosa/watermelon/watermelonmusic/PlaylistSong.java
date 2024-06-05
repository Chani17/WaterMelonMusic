package kosa.watermelon.watermelonmusic;

public class PlaylistSong {

    private String songName;
    private String artistName;

    public PlaylistSong(String song_name, String artistName) {
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
}
