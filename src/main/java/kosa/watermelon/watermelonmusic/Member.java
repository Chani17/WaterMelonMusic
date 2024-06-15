package kosa.watermelon.watermelonmusic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Member 클래스 : 회원 정보를 나타냄
 */
public class Member {

	private String id;
	private String pw;
	private String email;
	private String nickname;
	private byte[] profileImage; // BFILE 필드를 byte[]로 선언
	private String gender;
	private LocalDate birth;
	private List<Song> likedSongs = new ArrayList<>(); // 좋아하는 노래 목록 초기화

	// Getter와 Setter 메서드들
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public byte[] getProfileImage() {
		return profileImage;
	}

	public String getGender() {
		return gender;
	}

	public LocalDate getBirth() {
		return birth;
	}

	// 좋아하는 노래 목록을 반환하는 메서드
	public List<Song> getLikedSongs() {
		return likedSongs;
	}

	/**
	 * Member 클래스의 생성자
	 * 
	 * @param id           회원 ID
	 * @param pw           회원 비밀번호
	 * @param email        회원 이메일
	 * @param nickname     회원 닉네임
	 * @param profileImage 회원 프로필 이미지
	 * @param gender       회원 성별
	 * @param birth        회원 생일
	 */
	public Member(String id, String pw, String email, String nickname, byte[] profileImage, String gender,
			LocalDate birth) {
		this.id = id;
		this.pw = pw;
		this.email = email;
		this.nickname = nickname;
		this.profileImage = profileImage;
		this.gender = gender;
		this.birth = birth;
	}
}