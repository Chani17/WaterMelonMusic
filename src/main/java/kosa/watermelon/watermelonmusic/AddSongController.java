package kosa.watermelon.watermelonmusic;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AddSongController : 노래 추가 화면의 컨트롤러
 */
public class AddSongController {

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

		try {
			Connection conn = DBUtil.getConnection();

			PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM SONG");
			ResultSet rs = pstmt.executeQuery();

			long newSongId = 0L;
			if (rs.next())
				newSongId = rs.getInt(1) + 1;

			PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO SONG (SONG_ID, ARTIST_ID, SONG_NAME, CLICK_COUNT, SONG_FILE, ALBUM_ID) VALUES (?, ?, ?, 0, ?, ?)");
			stmt.setLong(1, newSongId + 1);
			stmt.setInt(2, selectedArtist.getArtistId());
			stmt.setString(3, songName);
			stmt.setString(4, songFile);
			stmt.setInt(5, selectedAlbum.getAlbumId());
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Stage stage = (Stage) songNameField.getScene().getWindow();
		stage.close();
	}
}
