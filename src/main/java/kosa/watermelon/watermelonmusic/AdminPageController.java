package kosa.watermelon.watermelonmusic;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * AdminPageController : 관리자 페이지 화면의 컨트롤러
 */
public class AdminPageController implements Initializable {

	@FXML private TableView<Artist> artistTableView;
	@FXML private TableColumn<Artist, String> artistNameColumn;
	@FXML private TableView<Album> albumTableView;
	@FXML private TableColumn<Album, String> albumNameColumn;
	@FXML private TableColumn<Album, String> artistNameForAlbumColumn;
	@FXML private TableView<SongFXModel> songTableView;
	@FXML private TableColumn<SongFXModel, String> songNameColumn;
	@FXML private TableColumn<SongFXModel, String> albumNameForSongColumn;
	@FXML private TableColumn<SongFXModel, String> artistNameForSongColumn;
	@FXML private Button logoutButton;

	private List<Artist> artistList = new ArrayList<>();
	private List<Album> albumList = new ArrayList<>();
	private List<SongFXModel> songList = new ArrayList<>();

	/**
	 * 초기화 메서드로, FXML 파일이 로드된 후 호출됨
	 * 
	 * @param url            초기화 URL
	 * @param resourceBundle 초기화 ResourceBundle
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// TableColumn 초기화
		artistNameColumn.setCellValueFactory(cellData -> cellData.getValue().artistNameProperty());
		albumNameColumn.setCellValueFactory(cellData -> cellData.getValue().albumNameProperty());
		artistNameForAlbumColumn.setCellValueFactory(cellData -> cellData.getValue().artistNameProperty());
		songNameColumn.setCellValueFactory(cellData -> cellData.getValue().songNameProperty());
		albumNameForSongColumn.setCellValueFactory(cellData -> cellData.getValue().albumNameProperty());
		artistNameForSongColumn.setCellValueFactory(cellData -> cellData.getValue().artistProperty());

		loadArtists();
		loadAlbums();
		loadSongs();
	}

	/**
	 * 데이터베이스에서 아티스트 목록을 로드함
	 */
	private void loadArtists() {
		artistList.clear();
		try (Connection conn = DBUtil.getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ARTIST");
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				artistList.add(new Artist(rs.getInt("ARTIST_ID"), rs.getString("ARTIST_NAME")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		artistTableView.getItems().setAll(artistList);
	}

	/**
	 * 데이터베이스에서 앨범 목록을 로드함
	 */
	private void loadAlbums() {
		albumList.clear();
		try (Connection conn = DBUtil.getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"SELECT a.ALBUM_ID, a.ALBUM_NAME, ar.ARTIST_NAME FROM ALBUM a JOIN ARTIST ar ON a.ARTIST_ID = ar.ARTIST_ID");
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				albumList
						.add(new Album(rs.getInt("ALBUM_ID"), rs.getString("ALBUM_NAME"), rs.getString("ARTIST_NAME")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		albumTableView.getItems().setAll(albumList);
	}

	/**
	 * 데이터베이스에서 노래 목록을 로드함
	 */
	private void loadSongs() {
		songList.clear();
		try (Connection conn = DBUtil.getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"SELECT s.SONG_ID, s.SONG_NAME, a.ALBUM_NAME, ar.ARTIST_NAME, s.CLICK_COUNT, s.SONG_FILE, a.ALBUM_COVER "
								+ "FROM SONG s JOIN ALBUM a ON s.ALBUM_ID = a.ALBUM_ID JOIN ARTIST ar ON s.ARTIST_ID = ar.ARTIST_ID");
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				songList.add(
						new SongFXModel(rs.getInt("SONG_ID"), rs.getString("ARTIST_NAME"), rs.getString("ALBUM_NAME"),
								rs.getString("SONG_NAME"), rs.getString("SONG_FILE"), rs.getLong("CLICK_COUNT")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		songTableView.getItems().setAll(songList);
	}

	/**
	 * 새로운 노래 추가 버튼 클릭 시 호출되는 메서드
	 */
	@FXML
	private void handleAddNewSong() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("addSong.fxml"));
			Parent root = loader.load();

			AddSongController controller = loader.getController();
			controller.setArtists(artistList);
			controller.setAlbums(albumList);

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Add New Song");
			stage.setScene(new Scene(root));
			stage.showAndWait(); // 사용자가 창을 닫을 때까지 기다림

			// 창이 닫힌 후 데이터 다시 로드
			loadArtists();
			loadAlbums();
			loadSongs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 새로운 아티스트 추가 버튼 클릭 시 호출되는 메서드
	 */
	@FXML
	private void handleAddNewArtist() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("addArtist.fxml"));
			Parent root = loader.load();

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Add New Artist");
			stage.setScene(new Scene(root));
			stage.showAndWait(); // 사용자가 창을 닫을 때까지 기다림

			// 창이 닫힌 후 데이터 다시 로드
			loadArtists();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 새로운 앨범 추가 버튼 클릭 시 호출되는 메서드
	 */
	@FXML
	private void handleAddNewAlbum() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("addAlbum.fxml"));
			Parent root = loader.load();

			AddAlbumController controller = loader.getController();
			controller.setArtists(artistList);

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Add New Album");
			stage.setScene(new Scene(root));
			stage.showAndWait(); // 사용자가 창을 닫을 때까지 기다림

			// 창이 닫힌 후 데이터 다시 로드
			loadAlbums();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 로그아웃 버튼 클릭 시 호출되는 메서드
	 * 
	 * @param event 이벤트 객체
	 */
	@FXML
	private void logoutAction(ActionEvent event) {
		// 세션 초기화
		SessionManager.getInstance().clearSession();

		// 로그인 창 열기
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
			Scene scene = new Scene(loader.load(), 800, 600);

			// 현재 Stage 찾기
			Stage currentStage = (Stage) logoutButton.getScene().getWindow();

			// MainApplicatin의 Scene 설정
			currentStage.setScene(scene);
			Image icon = new Image(
					getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png"));
			currentStage.getIcons().add(icon);
			currentStage.setTitle("WaterMelon Music!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
