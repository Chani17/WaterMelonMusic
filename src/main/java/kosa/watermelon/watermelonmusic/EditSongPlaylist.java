package kosa.watermelon.watermelonmusic;

public class EditSongPlaylist {
    private Long editId;
    private Long songId;
    private String editSongName;
    private String artistName;
    private String songFile;

    public EditSongPlaylist(Long editId, Long songId, String editSongName, String artistName, String songFile) {
        this.editId = editId;
        this.songId = songId;
        this.editSongName = editSongName;
        this.artistName = artistName;
        this.songFile = songFile;
    }

    public Long getEditId() {
        return editId;
    }

    public void setEditId(Long editId) {
        this.editId = editId;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public String getEditSongName() {
        return editSongName;
    }

    public void setEditSongName(String editSongName) {
        this.editSongName = editSongName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongFile() {
        return songFile;
    }

    public void setSongFile(String songFile) {
        this.songFile = songFile;
    }
}
