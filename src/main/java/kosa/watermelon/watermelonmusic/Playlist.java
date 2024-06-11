package kosa.watermelon.watermelonmusic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private Long playlistId;
    private String playlistName;
    private List<Long> songList;
    private String memberId;
    private int number;
    private LocalDate postDate;
    private String ownerName; // 소유자 이름 추가

    public Playlist(Long playlistId, String playlistName, List<Long> songList, String memberId, int number, LocalDate postDate) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.songList = songList;
        this.memberId = memberId;
        this.number = number;
        this.postDate = postDate;
    }

    // 새로운 생성자 추가
    public Playlist(Long playlistId, String playlistName, String memberId) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.memberId = memberId;
    }
    
    
    // 새로운 생성자 추가
    public Playlist(Long playlistId, String playlistName, String memberId, String ownerName) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.memberId = memberId;
        this.ownerName = ownerName;
    }
    
    public String getPlaylistName() {
        return playlistName;
    }

    public List<Long> getSongList() {
        return songList;
    }

    public String getMemberId() {
        return memberId;
    }

    public int getNumber() {
        return number;
    }

    public void addSong(Long songId) {
        this.songList.add(songId);
    }

    public Long getPlaylistId() {
        return playlistId;
    }

    public LocalDate getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDate postDate) {
        this.postDate = postDate;
    }
    
    // 소유자 이름을 반환하는 메서드 추가
    public String getOwnerName() {
        return ownerName;
    }
}
