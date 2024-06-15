package kosa.watermelon.watermelonmusic;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * AddAlbumController : 앨범 추가 컨트롤러
 */
public class AddAlbumController implements Initializable {

	// FXML 필드
	@FXML private TextField albumNameField;
	@FXML private TextField albumCoverPathField;
	@FXML private ComboBox<String> artistComboBox;

	private List<Artist> artistList;

	/**
	 * 초기화 메서드로 FXML 파일이 로드된 후 호출됨
	 * 
	 * @param url            초기화 URL
	 * @param resourceBundle 초기화 ResourceBundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// 초기화 작업이 필요하다면 여기에 추가
	}

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
	 * 앨범 저장 버튼 클릭 시 호출되는 메서드
	 */
	@FXML
	private void handleSaveAlbum() {
		String albumName = albumNameField.getText();
		String albumCoverPath = albumCoverPathField.getText();
		String selectedArtistName = artistComboBox.getValue();

		if (albumName.isEmpty() || albumCoverPath.isEmpty() || selectedArtistName == null) {
			// 입력 값 검증
			System.out.println("All fields are required.");
			return;
		}

		// 선택한 아티스트 객체를 artistList에서 찾아서 가져오기
		Artist selectedArtist = artistList.stream().filter(artist -> artist.getArtistName().equals(selectedArtistName))
				.findFirst().orElse(null);

		if (selectedArtist == null) {
			System.out.println("Selected artist not found.");
			return;
		}

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBUtil.getConnection();

			long albumId = generateNewId(conn);		// 새로운 ALBUM_ID 생성

			// 앨범을 추가하는 쿼리 실행
			pstmt = conn
					.prepareStatement("INSERT INTO ALBUM (ALBUM_ID, ALBUM_NAME, ARTIST_ID) VALUES (?, ?, ?)");
			pstmt.setLong(1, albumId);
			pstmt.setString(2, albumName);
			pstmt.setInt(3, selectedArtist.getArtistId());
			pstmt.executeUpdate();

			// BFILE을 설정하는 쿼리 실행
			String sql = "DECLARE " + "  lobloc BFILE := BFILENAME('IMAGE_DIR_ALBUMCOVER', ?); " + "BEGIN "
					+ "  UPDATE ALBUM SET ALBUM_COVER = lobloc WHERE ALBUM_ID = ?; " + "END;";
			try (PreparedStatement bfileStmt = conn.prepareStatement(sql)) {
				bfileStmt.setString(1, albumCoverPath);
				bfileStmt.setLong(2, albumId);
				bfileStmt.executeUpdate();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt);
		}

		Stage stage = (Stage) albumNameField.getScene().getWindow();
		stage.close();
	}

	/**
	 * 새로운 ID를 생성하는 메서드
	 *
	 * @param conn 데이터베이스 연결 객체
	 * @return 생성된 ID
	 */
	private Long generateNewId(Connection conn) {
		Long newId = null;
		String nextValSql = "SELECT ALBUM_SEQ.NEXTVAL FROM DUAL";

		try (PreparedStatement nextValStmt = conn.prepareStatement(nextValSql);
			 ResultSet nextValRs = nextValStmt.executeQuery()) {

			if (nextValRs.next()) {
				newId = nextValRs.getLong(1);
				System.out.println("newId = " + newId);
			}
			DBUtil.close(null, nextValStmt, nextValRs);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return newId;
	}

}
