package kosa.watermelon.watermelonmusic;

/**
 * PlaylistView 클래스 : 플레이리스트 뷰를 나타내는 클래스
 */
public class PlaylistView {
	
	private String songName; // 곡 이름
	private String artistName; // 아티스트 이름
	private byte[] albumCover; // 앨범 커버 이미지

	/**
	 * PlaylistView 생성자
	 *
	 * @param songName   곡 이름
	 * @param artistName 아티스트 이름
	 * @param albumCover 앨범 커버 이미지
	 */
	public PlaylistView(String songName, String artistName, byte[] albumCover) {
		this.songName = songName;
		this.artistName = artistName;
		this.albumCover = albumCover;
	}

	// Getter와 Setter 메서드들
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
