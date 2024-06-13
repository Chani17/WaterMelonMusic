package kosa.watermelon.watermelonmusic;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Artist 클래스 : 아티스트 데이터를 저장
 */
public class Artist {
	
	private final int artistId;
	private final StringProperty artistName;

	/**
	 * Artist 클래스의 생성자
	 * 
	 * @param artistId   아티스트 ID
	 * @param artistName 아티스트 이름
	 */
	public Artist(int artistId, String artistName) {
		this.artistId = artistId;
		this.artistName = new SimpleStringProperty(artistName);
	}

	/**
	 * 아티스트 ID를 반환
	 * 
	 * @return 아티스트 ID
	 */
	public int getArtistId() {
		return artistId;
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
