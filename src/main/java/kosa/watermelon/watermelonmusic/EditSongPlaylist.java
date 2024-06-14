package kosa.watermelon.watermelonmusic;

/**
 * EditSongPlaylist 클래스 : 편집된 곡의 플레이리스트 정보를 담고 있음
 */
public class EditSongPlaylist {

	private Long editId;
	private Long songId;
	private String editSongName;
	private String artistName;
	private String songFile;

	/**
	 * EditSongPlaylist의 생성자
	 * 
	 * @param editId       편집된 곡 ID
	 * @param songId       곡 ID
	 * @param editSongName 편집된 곡 이름
	 * @param artistName   아티스트 이름
	 * @param songFile     곡 파일
	 */
	public EditSongPlaylist(Long editId, Long songId, String editSongName, String artistName, String songFile) {
		this.editId = editId;
		this.songId = songId;
		this.editSongName = editSongName;
		this.artistName = artistName;
		this.songFile = songFile;
	}

	// Getter 및 Setter 메서드
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
