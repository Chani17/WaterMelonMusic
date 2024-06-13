package kosa.watermelon.watermelonmusic;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Album 클래스 : 앨범 데이터를 저장
 */
public class Album {
	
	private final int albumId;
	private final StringProperty albumName;
	private final StringProperty artistName;

	/**
	 * Album 클래스의 생성자
	 * 
	 * @param albumId    앨범 ID
	 * @param albumName  앨범 이름
	 * @param artistName 아티스트 이름
	 */
	public Album(int albumId, String albumName, String artistName) {
		this.albumId = albumId;
		this.albumName = new SimpleStringProperty(albumName);
		this.artistName = new SimpleStringProperty(artistName);
	}

	/**
	 * 앨범 ID를 반환
	 * 
	 * @return 앨범 ID
	 */
	public int getAlbumId() {
		return albumId;
	}

	/**
	 * 앨범 이름을 반환
	 * 
	 * @return 앨범 이름
	 */
	public String getAlbumName() {
		return albumName.get();
	}

	/**
	 * 앨범 이름의 StringProperty를 반환
	 * 
	 * @return 앨범 이름의 StringProperty
	 */
	public StringProperty albumNameProperty() {
		return albumName;
	}

	/**
	 * 아티스트 이름을 반환
	 * 
	 * @return 아티스트 이름
	 */
	public String getArtistName() {
		return artistName.get();
	}

	/**
	 * 아티스트 이름의 StringProperty를 반환
	 * 
	 * @return 아티스트 이름의 StringProperty
	 */
	public StringProperty artistNameProperty() {
		return artistName;
	}
}
