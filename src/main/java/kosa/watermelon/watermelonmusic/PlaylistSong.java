package kosa.watermelon.watermelonmusic;

/**
 * PlaylistSong 클래스 : 플레이리스트에 포함된 곡을 나타내는 클래스
 * 
 * 작성자 : 김찬희, 김효정
 */
public class PlaylistSong {

	private Long songId; // 곡 ID
	private String songName; // 곡 이름
	private String artistName; // 아티스트 이름

	/**
	 * PlaylistSong 생성자
	 *
	 * @param songId     곡 ID
	 * @param song_name  곡 이름
	 * @param artistName 아티스트 이름
	 */
	public PlaylistSong(Long songId, String song_name, String artistName) {
		this.songId = songId;
		this.songName = song_name;
		this.artistName = artistName;
	}

	// Getter와 Setter 메서드들
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

	public Long getSongId() {
		return songId;
	}
}
