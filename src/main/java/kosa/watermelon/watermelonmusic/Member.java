package kosa.watermelon.watermelonmusic;

import java.time.LocalDate;

public class Member {
	private String id;
	private String pw;
	private String nickname;
	private String email;
	private String gender; // 성별 추가
	private LocalDate birth; // 생일 추가 

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	public String getPw() { return pw; }
	public void setPw(String pw) { this.pw = pw; }

	public String getNickname() { return nickname; }
	public void setNickname(String nickname) { this.nickname = nickname; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email;}
	
	public String getGender() { return gender; }
	public void setGender(String gender) {this.gender = gender; }
	
	public LocalDate getBirth() { return birth; }
	public void setBirth(LocalDate birth) { this.birth = birth; }

	public Member(String id, String pw, String nickname, String email, String gender, LocalDate birth) {
		this.id = id;
		this.pw = pw;
		this.nickname = nickname;
		this.email = email;
		this.gender = gender;
		this.birth = birth;
	}
}