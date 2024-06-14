package kosa.watermelon.watermelonmusic;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import oracle.jdbc.OracleResultSet;
import oracle.sql.BFILE;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.ResourceBundle;

/**
 * PlayViewController 클래스 : 음악 재생 화면의 컨트롤러 클래스
 */
public class PlayViewController implements Initializable {

	// FXML 필드
    @FXML private Label songTitle;
    @FXML private Label artist;
    @FXML private Button playButton;
    @FXML private Button stopButton;
    @FXML private Button pauseButton;
    @FXML private ImageView albumCover;
    @FXML private Slider playBar;
    @FXML private Label playTimeHour;
    @FXML private Label playTimeMinute;
    @FXML private Label endTimeHour;
    @FXML private Label endTimeMinute;
    
    private MediaPlayer mediaPlayer;
    private Queue<Long> songQueue = new ArrayDeque<>();
    private long songId;
    private long totalTime;
    private long totalTimeHour;
    private long totalTimeMinute;
    private boolean isPlaying = true;
    private boolean isSliderChanging = false;
    private String type = "SONG";
    private Member currentMember;

	/**
	 * 초기화 메서드
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		playBar.setValueChanging(true); // 외부에서 슬라이더바 조작 가능
		playBar.setValue(0);
		playBar.valueProperty().addListener(((observable, oldValue, newValue) -> {
			if (isSliderChanging) {
				double currentTime = (newValue.doubleValue() / 100) * totalTime;
				mediaPlayer.seek(javafx.util.Duration.seconds(currentTime));
			}
		}));

		playBar.setOnMousePressed(event -> isSliderChanging = true);
		playBar.setOnMouseReleased(event -> isSliderChanging = false);

		playButton.setOnAction(event -> togglePlayPause());
		stopButton.setOnAction(event -> stopSong());
		pauseButton.setOnAction(event -> pauseSong());

		// 창 닫힘 이벤트 핸들러 추가
		Platform.runLater(() -> {
			Stage stage = (Stage) playButton.getScene().getWindow();
			stage.setOnCloseRequest(this::handleWindowClose);
		});
	}

	/**
	 * 창 닫힘 이벤트 처리 메서드
	 */
	private void handleWindowClose(WindowEvent event) {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
	}

	/**
	 * 재생할 곡 큐 설정 메서드
	 */
	public void setSongQueue(Queue<Long> songIds, String type) {
		this.songQueue = songIds; // 큐 초기화
		this.type = type;
		playNextSong(); // 첫 번째 노래 재생
	}

	/**
	 * 다음 곡 재생 메서드
	 */
	private void playNextSong() {
		if (songQueue.isEmpty()) {
			System.out.println("No more songs to play.");
			return;
		}

		Long nextSongId = songQueue.poll(); // 큐에서 다음 노래 ID 가져오기
		setSongId(nextSongId); // 다음 노래 재생
	}

	/**
	 * 곡 ID 설정 메서드
	 */
	public void setSongId(long id) {
		this.songId = id;
		setPlayView();
	}

	/**
	 * 회원 정보 설정 메서드
	 */
	public void setMember(Member member) {
		this.currentMember = member;
	}

	/**
	 * 재생 화면 설정 메서드
	 */
	private void setPlayView() {
		Connection conn = null;
		PreparedStatement pstmt_song = null;
		ResultSet res_song = null;

		try {
			conn = DBUtil.getConnection();

			System.out.println("Type = " + this.type);
			if (this.type.equals("EDIT")) {
				pstmt_song = conn.prepareStatement("SELECT "
						+ "e.SONG_NAME, e.SONG_FILE, a.ALBUM_COVER, ar.ARTIST_NAME " + "FROM EDITSONG e "
						+ "LEFT JOIN SONG s ON e.SONG_ID = s.SONG_ID " + "LEFT JOIN ALBUM a ON s.ALBUM_ID = a.ALBUM_ID "
						+ "LEFT JOIN ARTIST ar ON a.ARTIST_ID = ar.ARTIST_ID " + "WHERE e.editsong_id=?");

				pstmt_song.setLong(1, this.songId);
			} else {
				pstmt_song = conn.prepareStatement("SELECT s.song_name, s.song_file, a.album_cover, ar.artist_name "
						+ "FROM Song s " + "LEFT OUTER JOIN Album a " + "ON s.album_id = a.album_id "
						+ "LEFT OUTER JOIN Artist ar " + "ON ar.artist_id = a.artist_id " + "WHERE song_id=?");

				pstmt_song.setLong(1, this.songId);
			}

			res_song = pstmt_song.executeQuery();

			if (res_song.next()) {
				String songName = res_song.getString("song_name");
				String artistName = res_song.getString("artist_name");
				BFILE bfile = ((OracleResultSet) res_song).getBFILE("album_cover");
				System.out.println(bfile.getName());

				songTitle.setText(songName);
				artist.setText(artistName);

				bfile.openFile(); // BFILE 열기
				InputStream inputStream = bfile.getBinaryStream();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				byte[] imageData = outputStream.toByteArray();
				outputStream.close();
				inputStream.close();
				PlaylistView playlistView = new PlaylistView(songName, artistName, imageData);
				bfile.closeFile(); // 자원 누수 방지를 위함

				// 이미지 데이터를 이용하여 Image 객체 생성
				Image image = new Image(new ByteArrayInputStream(playlistView.getAlbumCover()));

				// ImageView에 이미지 설정
				albumCover.setImage(image);

				String songFilePath = res_song.getString("song_file");

				// 미디어 등록
				Media media = new Media(new File(songFilePath).toURI().toString());
				mediaPlayer = new MediaPlayer(media);
				mediaPlayer.setOnReady(() -> {
					totalTime = (long) mediaPlayer.getTotalDuration().toSeconds();
					totalTimeHour = totalTime / 60;
					totalTimeMinute = totalTime % 60;
					endTimeHour.setText(String.format("%02d", totalTimeHour));
					endTimeMinute.setText(String.format("%02d", totalTimeMinute));
				});
				mediaPlayer.setAutoPlay(true);
				mediaPlayer.currentTimeProperty().addListener((observable, oldTime, newTime) -> updatePlayTime());
//                mediaPlayer.setOnEndOfMedia(this::resetMediaPlayer);
				mediaPlayer.setOnEndOfMedia(this::onSongEnd);
			} else {
				System.out.println("No song found with id = " + this.songId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt_song, res_song, conn);
		}
	}

	/**
	 * 곡 재생 종료 시 호출되는 메서드
	 */
	private void onSongEnd() {
		resetMediaPlayer(); // 미디어 플레이어 재설정
		playNextSong(); // 다음 노래 재생
	}

	/**
	 * 재생/일시정지 토글 메서드
	 */
	private void togglePlayPause() {
		if (mediaPlayer == null)
			return;

		if (isPlaying) {
			pauseSong();
		} else {
			playSong();
		}
	}

	/**
	 * 곡 재생 메서드
	 */
	private void playSong() {
		if (mediaPlayer == null)
			return;

		mediaPlayer.play();
		isPlaying = true;

		// UI를 주기적으로 업데이트하는 스레드 생성
		Thread thread = new Thread(() -> {
			while (isPlaying) {
				Platform.runLater(this::updatePlayTime);

				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.setDaemon(true); // 애플리케이션 종료 시 스레드 자동 종료
		thread.start();
	}

	/**
	 * 곡 일시정지 메서드
	 */
	private void pauseSong() {
		if (mediaPlayer == null)
			return;

		mediaPlayer.pause();
		isPlaying = false;
	}

	/**
	 * 곡 정지 메서드
	 */
	private void stopSong() {
		if (mediaPlayer == null)
			return;

		mediaPlayer.stop();
		isPlaying = false;
		playBar.setValue(0);
		playTimeHour.setText("0");
		playTimeMinute.setText("00");
		endTimeHour.setText(String.format("%02d", totalTimeHour));
		endTimeMinute.setText(String.format("%02d", totalTimeMinute));
	}

	/**
	 * 재생 시간 업데이트 메서드
	 */
	private void updatePlayTime() {
		if (mediaPlayer == null || isSliderChanging)
			return;

		long currentTime = (long) mediaPlayer.getCurrentTime().toSeconds();
		long currentTimeHour = currentTime / 60;
		long currentTimeMinute = currentTime % 60;

		playBar.setValue((double) currentTime / totalTime * 100);

		playTimeHour.setText(String.valueOf(currentTimeHour));
		if (currentTimeMinute < 10)
			playTimeMinute.setText("0" + currentTimeMinute);
		else
			playTimeMinute.setText(String.valueOf(currentTimeMinute));

		long remainingTime = totalTime - currentTime;
		long remainingHour = remainingTime / 60;
		long remainingMinute = remainingTime % 60;

		endTimeHour.setText(String.format("%02d", remainingHour));
		endTimeMinute.setText(String.format("%02d", remainingMinute));
	}

	/**
	 * 미디어 플레이어 초기화 메서드
	 */
	private void resetMediaPlayer() {
		isPlaying = false;
		playBar.setValue(0);
		playTimeHour.setText("0");
		playTimeMinute.setText("00");
		endTimeHour.setText(String.format("%02d", totalTimeHour));
		endTimeMinute.setText(String.format("%02d", totalTimeMinute));
		mediaPlayer.seek(javafx.util.Duration.ZERO);
		mediaPlayer.stop();
	}
}