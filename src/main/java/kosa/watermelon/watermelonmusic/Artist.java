package kosa.watermelon.watermelonmusic;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Artist {
    private final int artistId;
    private final StringProperty artistName;

    public Artist(int artistId, String artistName) {
        this.artistId = artistId;
        this.artistName = new SimpleStringProperty(artistName);
    }

    public int getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName.get();
    }

    public StringProperty artistNameProperty() {
        return artistName;
    }
}
