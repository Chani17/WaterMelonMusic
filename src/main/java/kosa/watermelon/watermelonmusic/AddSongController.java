package kosa.watermelon.watermelonmusic;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AddSongController : 노래 추가 화면의 컨트롤러
 */
public class AddSongController {
	
	// FXML 필드
	@FXML private ComboBox<String> artistComboBox;
	@FXML private ComboBox<String> albumComboBox;
	@FXML private TextField songNameField;
	@FXML private TextField songFilePathField;

	private List<Artist> artistList;
	private List<Album> albumList;

	/**
	 * 아티스트 목록을 설정하고 ComboBox에 추가
	 * 
	 * @param artists 아티스트 목록
	 */
	public void setArtists(List<Artist> artists) {
		this.artistList = artists;
		artistComboBox.getItems().setAll(artists.stream().map(Artist::getArtistName).collect(Collectors.toList()));
	}

	/**
	 * 앨범 목록을 설정하고 ComboBox에 추가
	 * 
	 * @param albums 앨범 목록
	 */
	public void setAlbums(List<Album> albums) {
		this.albumList = albums;
		albumComboBox.getItems().setAll(albums.stream().map(Album::getAlbumName).collect(Collectors.toList()));
	}

	/**
	 * 노래 저장 버튼 클릭 시 호출되는 메서드
	 */
	@FXML
	private void handleSaveSong() {
		String songName = songNameField.getText();
		String songFile = songFilePathField.getText();
		String selectedArtistName = artistComboBox.getValue();
		String selectedAlbumName = albumComboBox.getValue();

		if (songName.isEmpty() || songFile.isEmpty() || selectedArtistName == null || selectedAlbumName == null) {
			// 입력 값 검증
			System.out.println("All fields are required.");
			return;
		}

		Artist selectedArtist = artistList.stream().filter(artist -> artist.getArtistName().equals(selectedArtistName))
				.findFirst().orElse(null);

		Album selectedAlbum = albumList.stream().filter(album -> album.getAlbumName().equals(selectedAlbumName))
				.findFirst().orElse(null);

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBUtil.getConnection();

			pstmt = conn.prepareStatement(
					"INSERT INTO SONG (SONG_ID, ARTIST_ID, SONG_NAME, CLICK_COUNT, SONG_FILE, ALBUM_ID) VALUES (SONG_SEQ.NEXTVAL, ?, ?, 0, ?, ?)");
            pstmt.setInt(1, selectedArtist.getArtistId());
			pstmt.setString(2, songName);
			pstmt.setString(3, songFile);
			pstmt.setInt(4, selectedAlbum.getAlbumId());
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt);
		}

		Stage stage = (Stage) songNameField.getScene().getWindow();
		stage.close();
	}
}
