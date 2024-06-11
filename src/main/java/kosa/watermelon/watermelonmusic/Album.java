package kosa.watermelon.watermelonmusic;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Album {
    private final int albumId;
    private final StringProperty albumName;
    private final StringProperty artistName;

    public Album(int albumId, String albumName, String artistName) {
        this.albumId = albumId;
        this.albumName = new SimpleStringProperty(albumName);
        this.artistName = new SimpleStringProperty(artistName);
    }

    public int getAlbumId() {
        return albumId;
    }

    public String getAlbumName() {
        return albumName.get();
    }

    public StringProperty albumNameProperty() {
        return albumName;
    }

    public String getArtistName() {
        return artistName.get();
    }

    public StringProperty artistNameProperty() {
        return artistName;
    }
}
