package kosa.watermelon.watermelonmusic;

import java.time.LocalDate;

public class Member {
	private String id;
	private String pw;
	private String nickname;
	private String email;
	private String gender;
	private LocalDate birth;


	public Member(String id, String pw, String nickname, String email, String gender, LocalDate birth) {
		this.id = id;
		this.pw = pw;
		this.nickname = nickname;
		this.email = email;
		this.gender = gender;
		this.birth = birth;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}