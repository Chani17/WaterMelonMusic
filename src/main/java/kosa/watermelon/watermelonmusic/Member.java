package kosa.watermelon.watermelonmusic;

public class Member {
	private String id;
	private String pw;
	private String nickname;
	private String email; // 이메일 추가

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

	public String getEMAIL() {
		return email;
	}

	public void setEMAIL(String email) {
		this.email = email;
	}

	public Member(String id, String pw, String nickname, String email) {
		this.id = id;
		this.pw = pw;
		this.nickname = nickname;
		this.email = email;
	}
}