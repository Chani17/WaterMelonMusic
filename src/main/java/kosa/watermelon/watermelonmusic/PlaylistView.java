package kosa.watermelon.watermelonmusic;

public class PlaylistView {
    private String songName;
    private String artistName;
    private byte[] albumCover;

    public PlaylistView(String songName, String artistName, byte[] albumCover) {
        this.songName = songName;
        this.artistName = artistName;
        this.albumCover = albumCover;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public byte[] getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(byte[] albumCover) {
        this.albumCover = albumCover;
    }
}
