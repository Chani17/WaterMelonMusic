package kosa.watermelon.watermelonmusic;

/**
 * SessionManager 클래스 : 현재 세션 정보를 관리
 * 
 * 작성자 : 김효정
 */
public class SessionManager {

	private static SessionManager instance;
	private Member currentMember;

	// Singleton 패턴을 위한 private 생성자
	private SessionManager() {
	}

	/**
	 * SessionManager의 인스턴스를 반환함
	 * 
	 * @return SessionManager 인스턴스
	 */
	public static SessionManager getInstance() {
		if (instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}

	/**
	 * 현재 세션의 멤버를 반환함
	 * 
	 * @return 현재 세션의 멤버
	 */
	public Member getCurrentMember() {
		return currentMember;
	}

	/**
	 * 현재 세션의 멤버를 설정함
	 * 
	 * @param currentMember 현재 세션의 멤버
	 */
	public void setCurrentMember(Member currentMember) {
		this.currentMember = currentMember;
	}

	/**
	 * 현재 세션을 초기화함
	 */
	public void clearSession() {
		currentMember = null;
	}
}