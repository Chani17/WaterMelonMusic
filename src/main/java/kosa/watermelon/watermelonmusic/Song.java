package kosa.watermelon.watermelonmusic;

public class Song {
    private int id;
    private String name;
    private String artist;
    private long clickCnt;
    private long likeCnt;

    public Song(int id, String name, String artist, long clickCnt, long likeCnt) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.clickCnt = clickCnt;
        this.likeCnt = likeCnt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getClickCnt() {
        return clickCnt;
    }

    public void setClickCnt() {
        this.clickCnt++;
    }

    public long getLikeCnt() {
        return likeCnt;
    }

    public void setLikeCnt() {
        this.likeCnt++;
    }
}