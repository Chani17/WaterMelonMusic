package kosa.watermelon.watermelonmusic;

import javafx.beans.property.*;

/**
 * SongFXModel 클래스 : JavaFX와 함께 사용되는 곡 정보를 담는 모델 클래스
 */
public class SongFXModel {

	private final LongProperty id;
	private final StringProperty artist;
	private final StringProperty albumName;
	private final StringProperty songName;
	private final LongProperty clickCnt;
	private final StringProperty mediaSource;

	/**
	 * 생성자: SongFXModel 객체 생성
	 */
	public SongFXModel(long id, String artist, String albumName, String songName, String mediaSource, long clickCnt) {
		this.id = new SimpleLongProperty(id);
		this.artist = new SimpleStringProperty(artist);
		this.albumName = new SimpleStringProperty(albumName);
		this.songName = new SimpleStringProperty(songName);
		this.clickCnt = new SimpleLongProperty(clickCnt);
		this.mediaSource = new SimpleStringProperty(mediaSource);
	}

	// Getter와 Setter 메서드들
	public String getAlbumName() {
		return albumName.get();
	}

	public StringProperty albumNameProperty() {
		return albumName;
	}

	public long getId() {
		return id.get();
	}

	public LongProperty idProperty() {
		return id;
	}

	public String getArtist() {
		return artist.get();
	}

	public StringProperty artistProperty() {
		return artist;
	}

	public long getClickCnt() {
		return clickCnt.get();
	}

	public LongProperty clickCntProperty() {
		return clickCnt;
	}

	public String getMediaSource() {
		return mediaSource.get();
	}

	public StringProperty mediaSourceProperty() {
		return mediaSource;
	}

	public void setClickCnt(long clickCnt) {
		this.clickCnt.set(clickCnt);
	}

	public void setId(long id) {
		this.id.set(id);
	}

	public String getSongName() {
		return songName.get();
	}

	public StringProperty songNameProperty() {
		return songName;
	}

	public void setArtist(String artist) {
		this.artist.set(artist);
	}

	public void setMediaSource(String mediaSource) {
		this.mediaSource.set(mediaSource);
	}

	public void setAlbumName(String albumName) {
		this.albumName.set(albumName);
	}
}
