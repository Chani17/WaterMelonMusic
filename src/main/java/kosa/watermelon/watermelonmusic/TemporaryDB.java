package kosa.watermelon.watermelonmusic;

import kosa.watermelon.watermelonmusic.entity.Member;

import java.util.ArrayList;
import java.util.List;

public class TemporaryDB {

    private static TemporaryDB instance;
    private List<Member> members;

    public TemporaryDB() {
        this.members = new ArrayList<>();
        members.add(new Member("abcd", "1234", "melon"));
        members.add(new Member("qwer", "0101", "flo"));
        members.add(new Member("zxcv", "5678", "genie"));
    }

    public Member checkIdAndPw(String id, String pw) {
        for(Member member : members) {
            if(member.getId().equals(id) && member.getPw().equals(pw)) return member;
        }
        throw new IllegalArgumentException("Please check your id/pw");
    }

    public static TemporaryDB getInstance() {
        if(instance == null) instance = new TemporaryDB();
        return instance;
    }
}
