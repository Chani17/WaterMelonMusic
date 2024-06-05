package kosa.watermelon.watermelonmusic;

public class SessionManager {
    private static SessionManager instance;
    private Member currentMember;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public Member getCurrentMember() {
        return currentMember;
    }

    public void setCurrentMember(Member currentMember) {
        this.currentMember = currentMember;
    }

    public void clearSession() {
        currentMember = null;
    }
}