package kosa.watermelon.watermelonmusic;

import javafx.scene.image.Image;
import javafx.scene.media.Media;

/**
 * Song 클래스 : 곡 정보를 담는 클래스
 */
public class Song {
	
	private int ranking;
	private long id;
	private String name;
	private String artist;
	private byte[] albumCover;
	private String mediaSource;
	private long clickCnt;

	/**
	 * 생성자: 앨범 커버가 있는 경우
	 */
	public Song(int ranking, long id, String name, String artist, byte[] albumCover, long clickCnt) {
		this.ranking = ranking;
		this.id = id;
		this.name = name;
		this.artist = artist;
		this.albumCover = albumCover;
		this.clickCnt = clickCnt;
	}

	/**
	 * 생성자: 앨범 커버가 없는 경우
	 */
	public Song(int ranking, long id, String name, String artist, long clickCnt) {
		this.ranking = ranking;
		this.id = id;
		this.name = name;
		this.artist = artist;
		this.clickCnt = clickCnt;
	}

	/**
	 * 생성자: 앨범 커버와 미디어 소스가 있는 경우
	 */
	public Song(int ranking, long id, String name, String artist, byte[] albumCover, String mediaSource,
			long clickCnt) {
		this.ranking = ranking;
		this.id = id;
		this.name = name;
		this.artist = artist;
		this.albumCover = albumCover;
		this.mediaSource = mediaSource;
		this.clickCnt = clickCnt;
	}

	// Getter와 Setter 메서드들
	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public long getClickCnt() {
		return clickCnt;
	}

	public void setClickCnt() {
		this.clickCnt++;
	}

	public byte[] getAlbumCover() {
		return albumCover;
	}

	public String getMediaSource() {
		return mediaSource;
	}

	public void setMediaSource(String mediaSource) {
		this.mediaSource = mediaSource;
	}
}