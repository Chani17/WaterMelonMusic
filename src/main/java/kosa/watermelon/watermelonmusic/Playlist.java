package kosa.watermelon.watermelonmusic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Playlist 클래스 : 플레이리스트를 나타냄
 */
public class Playlist {
	
	private Long playlistId;
	private String playlistName;
	private List<Long> songList;
	private String memberId;
	private int number;
	private LocalDate postDate;
	private String ownerName; // 소유자 이름 추가

	/**
	 * 플레이리스트 생성자
	 * 
	 * @param playlistId   플레이리스트 ID
	 * @param playlistName 플레이리스트 이름
	 * @param songList     노래 목록
	 * @param memberId     회원 ID
	 * @param number       회원 플레이리스트 개수
	 * @param postDate     게시 날짜
	 */
	public Playlist(Long playlistId, String playlistName, List<Long> songList, String memberId, int number,
			LocalDate postDate) {
		this.playlistId = playlistId;
		this.playlistName = playlistName;
		this.songList = songList;
		this.memberId = memberId;
		this.number = number;
		this.postDate = postDate;
	}

	/**
	 * 새로운 생성자
	 * 
	 * @param playlistId   플레이리스트 ID
	 * @param playlistName 플레이리스트 이름
	 * @param memberId     회원 ID
	 */
	public Playlist(Long playlistId, String playlistName, String memberId) {
		this.playlistId = playlistId;
		this.playlistName = playlistName;
		this.memberId = memberId;
	}

	/**
	 * 새로운 생성자
	 * 
	 * @param playlistId   플레이리스트 ID
	 * @param playlistName 플레이리스트 이름
	 * @param memberId     회원 ID
	 * @param ownerName    소유자 이름
	 */
	public Playlist(Long playlistId, String playlistName, String memberId, String ownerName) {
		this.playlistId = playlistId;
		this.playlistName = playlistName;
		this.memberId = memberId;
		this.ownerName = ownerName;
	}

	// Getter 및 Setter 메서드
	public String getPlaylistName() {
		return playlistName;
	}

	public List<Long> getSongList() {
		return songList;
	}

	public String getMemberId() {
		return memberId;
	}

	public int getNumber() {
		return number;
	}

	public void addSong(Long songId) {
		this.songList.add(songId);
	}

	public Long getPlaylistId() {
		return playlistId;
	}

	public LocalDate getPostDate() {
		return postDate;
	}

	public void setPostDate(LocalDate postDate) {
		this.postDate = postDate;
	}

	// 소유자 이름을 반환하는 메서드 추가
	public String getOwnerName() {
		return ownerName;
	}
}
