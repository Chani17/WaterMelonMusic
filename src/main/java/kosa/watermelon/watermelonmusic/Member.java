package kosa.watermelon.watermelonmusic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Member {
	private String id;
	private String pw;
	private String email;
	private String nickname;
	private byte[] profileImage; // BFILE 필드를 byte[]로 선언
	private String gender;
	private LocalDate birth;

	private List<Song> likedSongs = new ArrayList<>(); // 좋아하는 노래 목록 초기화
	
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	public String getPw() { return pw; }
	public void setPw(String pw) { this.pw = pw; }

	public String getNickname() { return nickname; }
	public void setNickname(String nickname) { this.nickname = nickname; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email;}
	
	public byte[] getProfileImage() { return profileImage; }
	public void setProfileImage(byte[] profileImage) { this.profileImage = profileImage; }
	
	public String getGender() { return gender; }
	public void setGender(String gender) {this.gender = gender; }
	
	public LocalDate getBirth() { return birth; }
	public void setBirth(LocalDate birth) { this.birth = birth; }

	// 좋아하는 노래 목록을 반환하는 메서드 추가
    public List<Song> getLikedSongs() {
        return likedSongs;
    }

    // 좋아하는 노래 목록을 설정하는 메서드 추가
    public void setLikedSongs(List<Song> likedSongs) {
        this.likedSongs = likedSongs;
    }
	
	public Member(String id, String pw, String email, String nickname, byte[] profileImage, String gender, LocalDate birth) {
		this.id = id;
		this.pw = pw;
		this.email = email;
		this.nickname = nickname;
		this.profileImage = profileImage;
		this.gender = gender;
		this.birth = birth;
	}
}