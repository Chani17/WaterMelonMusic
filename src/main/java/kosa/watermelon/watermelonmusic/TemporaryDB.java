package kosa.watermelon.watermelonmusic;

import java.util.ArrayList;
import java.util.List;

public class TemporaryDB {

    private static TemporaryDB instance;
    private List<Member> members;
    private List<Song> songs;
    private List<Song> myPlaylist;

    public TemporaryDB() {
        this.members = new ArrayList<>();
        members.add(new Member("abcd", "1234", "melon"));
        members.add(new Member("qwer", "0101", "flo"));
        members.add(new Member("zxcv", "5678", "genie"));

        this.songs = new ArrayList<>();
        songs.add(new Song(1, "Supernova", "aespa",0 ,0));
        songs.add(new Song(2, "How Sweet", "NewJeans", 0, 0));
        songs.add(new Song(3, "Bubble Gum", "NewJeans", 0, 0));
        songs.add(new Song(4, "SPOT!", "지코 (ZICO)", 0, 0));
        songs.add(new Song(5, "해야(HEYA)", "IVE (아이브)", 0, 0));
        songs.add(new Song(6, "소나기", "이클립스 (ECLIPSE)", 0, 0));
        songs.add(new Song(7, "고민중독", "QWER", 0, 0));
        songs.add(new Song(8, "Magnetic", "아일릿 (ILLIT)", 0, 0));
        songs.add(new Song(9, "나는 아픈 건 딱 질색이니까", "(여자)아이들", 0, 0));
        songs.add(new Song(10, "Armageddon", "aespa", 0, 0));
        songs.add(new Song(11, "미안해 미워해 사랑해", "Crush", 0, 0));
        songs.add(new Song(12, "첫 만남은 계획대로 되지 않아", "TWS (투어스)", 0, 0));
        songs.add(new Song(13, "천상연", "이창섭", 0, 0));
        songs.add(new Song(14, "SHEESH", "BABYMONSTER", 0, 0));
        songs.add(new Song(15, "한 페이지가 될 수 있게", "DAY6 (데이식스)", 0, 0));

        this.myPlaylist = new ArrayList<>();
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

    public List<Song> getSongs() {
        return songs;
    }

    public void setMyPlaylist(Song song) {
        if(!myPlaylist.contains(song)) this.myPlaylist.add(song);
    }
}
