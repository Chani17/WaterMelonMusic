package kosa.watermelon.watermelonmusic;

public class Song {
	private int ranking;
    private long id;
    private String name;
    private String artist;
    private long clickCnt;

    public Song(int ranking, long id, String name, String artist, long clickCnt) {
        this.ranking = ranking;
    	this.id = id;
        this.name = name;
        this.artist = artist;
        this.clickCnt = clickCnt;
    }

    public int getRanking() {
    	return ranking;
    }
    
    public void setRanking(int ranking) {
    	this.ranking = ranking;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
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

//    public long getLikeCnt() {
//        return likeCnt;
//    }
//
//    public void setLikeCnt() {
//        this.likeCnt++;
//    }
}