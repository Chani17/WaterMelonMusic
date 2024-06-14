package kosa.watermelon.watermelonmusic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * PlaylistSelectionController 클래스 : 플레이리스트 선택 화면을 제어하는 컨트롤러 클래스
 */
public class PlaylistSelectionController {

    @FXML private ComboBox<String> playlistComboBox; // 플레이리스트 선택 콤보박스
    @FXML private TextField newPlaylistName; // 새로운 플레이리스트 이름 입력 필드

    private long selectedSongId; // 선택된 곡의 ID
    private Member currentMember; // 현재 로그인한 멤버

	/**
	 * 초기화 메서드
	 */
	@FXML
	public void initialize() {
		if (currentMember != null) {
			loadPlaylists(); // 멤버가 설정되어 있으면 플레이리스트 로드
		} else {
			// 초기화 코드 추가
			System.out.println("currentMember is null in initialize");
		}

		// ComboBox의 항목 폰트를 설정하는 셀 팩토리 설정
		playlistComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> param) {
				return new ListCell<String>() {
					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setText(null);
						} else {
							setText(item);
							setFont(Font.font("D2Coding", 13));
						}
					}
				};
			}
		});

		// 콤보박스의 버튼 셀 설정
		playlistComboBox.setButtonCell(new ListCell<String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(item);
					setFont(Font.font("D2Coding", 13));
				}
			}
		});
	}

	/**
	 * 곡 ID 설정 메서드
	 *
	 * @param songId 설정할 곡의 ID
	 */
	public void setSongId(long songId) {
		this.selectedSongId = songId;
	}

	/**
	 * 현재 멤버 설정 메서드
	 *
	 * @param member 설정할 멤버 객체
	 */
	public void setCurrentMember(Member member) {
		this.currentMember = member;
		if (currentMember != null) {
			loadPlaylists(); // 멤버가 설정되어 있으면 플레이리스트 로드
		} else {
			System.out.println("currentMember is null in setCurrentMember");
		}
	}

	/**
	 * 데이터베이스에서 플레이리스트를 로드하는 메서드
	 */
	private void loadPlaylists() {
		List<String> playlistNames = new ArrayList<>();
		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn
						.prepareStatement("SELECT playlist_name FROM Playlist WHERE member_id = ?")) {
			pstmt.setString(1, currentMember.getId());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				playlistNames.add(rs.getString("playlist_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ObservableList<String> options = FXCollections.observableArrayList(playlistNames);
		playlistComboBox.setItems(options);
	}

	/**
	 * 확인 버튼 클릭 시 이벤트 처리 메서드
	 *
	 * @param event 이벤트 객체
	 */
	@FXML
	private void handleConfirm(ActionEvent event) {
		String selectedPlaylistName = playlistComboBox.getValue();
		String newPlaylist = newPlaylistName.getText();

		if (selectedPlaylistName != null && !selectedPlaylistName.isEmpty()) {
			// 선택된 기존 플레이리스트에 곡 추가
			addSongToExistingPlaylist(selectedPlaylistName);
		} else if (newPlaylist != null && !newPlaylist.isEmpty()) {
			// 새로운 플레이리스트 생성 및 곡 추가
			createNewPlaylistAndAddSong(newPlaylist);
		}

		// 창 닫기
		Stage stage = (Stage) playlistComboBox.getScene().getWindow();
		stage.close();
	}

	/**
	 * 취소 버튼 클릭 시 이벤트 처리 메서드
	 *
	 * @param event 이벤트 객체
	 */
	@FXML
	private void handleCancel(ActionEvent event) {
		// 창 닫기
		Stage stage = (Stage) playlistComboBox.getScene().getWindow();
		stage.close();
	}

	/**
	 * 기존 플레이리스트에 곡을 추가하는 메서드
	 *
	 * @param playlistName 추가할 플레이리스트 이름
	 */
	private void addSongToExistingPlaylist(String playlistName) {
		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn
						.prepareStatement("UPDATE Playlist SET Song = ? WHERE playlist_name = ? AND member_id = ?")) {

			// 기존 플레이리스트에 곡 추가
			Playlist playlist = getPlaylistByName(playlistName, conn);
			if (playlist != null && !playlist.getSongList().contains(selectedSongId)) {
				playlist.getSongList().add(selectedSongId);
				Long[] newSongs = playlist.getSongList().toArray(new Long[0]);

				ArrayDescriptor desc = ArrayDescriptor.createDescriptor("SONG_ARRAY", conn);
				ARRAY newSongArray = new ARRAY(desc, conn, newSongs);

				pstmt.setArray(1, newSongArray);
				pstmt.setString(2, playlistName);
				pstmt.setString(3, currentMember.getId());
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 새로운 플레이리스트를 생성하고 곡을 추가하는 메서드
	 *
	 * @param playlistName 생성할 플레이리스트 이름
	 */
	private void createNewPlaylistAndAddSong(String playlistName) {
		try (Connection conn = DBUtil.getConnection()) {
			long newPlaylistId = generateNewPlaylistId(conn);

			// 새로운 플레이리스트 생성
			try (PreparedStatement pstmt = conn.prepareStatement(
					"INSERT INTO Playlist (playlist_id, playlist_name, member_id, Song) VALUES (?, ?, ?, ?)")) {
				List<Long> songList = new ArrayList<>();
				songList.add(selectedSongId);
				Long[] newSongs = songList.toArray(new Long[0]);

				ArrayDescriptor desc = ArrayDescriptor.createDescriptor("SONG_ARRAY", conn);
				ARRAY newSongArray = new ARRAY(desc, conn, newSongs);

				pstmt.setLong(1, newPlaylistId);
				pstmt.setString(2, playlistName);
				pstmt.setString(3, currentMember.getId());
				pstmt.setArray(4, newSongArray);
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 플레이리스트 이름으로 플레이리스트를 가져오는 메서드
	 *
	 * @param playlistName 가져올 플레이리스트 이름
	 * @param conn         데이터베이스 연결 객체
	 * @return 플레이리스트 객체
	 * @throws SQLException SQL 예외 발생 시
	 */
	private Playlist getPlaylistByName(String playlistName, Connection conn) throws SQLException {
		try (PreparedStatement pstmt = conn
				.prepareStatement("SELECT * FROM Playlist WHERE playlist_name = ? AND member_id = ?")) {
			pstmt.setString(1, playlistName);
			pstmt.setString(2, currentMember.getId());
			ResultSet rs = pstmt.executeQuery();
			int num = 0;
			if (rs.next()) {
				Array songArray = rs.getArray("Song");
				BigDecimal[] songs = (BigDecimal[]) songArray.getArray();
				List<Long> songList = new ArrayList<>();
				for (BigDecimal bd : songs) {
					songList.add(bd.longValue());
				}
				return new Playlist(rs.getLong("playlist_id"), rs.getString("playlist_name"), songList,
						rs.getString("member_id"), ++num, LocalDate.now());
			} else {
				return null;
			}
		}
	}

	/**
	 * 새로운 플레이리스트 ID를 생성하는 메서드
	 *
	 * @param conn 데이터베이스 연결 객체
	 * @return 새로운 플레이리스트 ID
	 * @throws SQLException SQL 예외 발생 시
	 */
	private Long generateNewPlaylistId(Connection conn) throws SQLException {
		try (PreparedStatement pstmt = conn.prepareStatement("SELECT MAX(playlist_id) FROM Playlist")) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getLong(1) + 1;
			} else {
				return 1L;
			}
		}
	}
}