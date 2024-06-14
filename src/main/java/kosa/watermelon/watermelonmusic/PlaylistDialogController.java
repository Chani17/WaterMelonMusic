package kosa.watermelon.watermelonmusic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * PlaylistDialogController 클래스 : 플레이리스트 다이얼로그의 컨트롤러 클래스
 */
public class PlaylistDialogController {

	// FXML 필드
    @FXML private ListView<Playlist> playlistListView;

    private ObservableList<Playlist> playlists;

    private PlaylistDAO playlistDAO;
    private Playlist selectedPlaylist;
    private PostingPageController postingPageController; // 부모 컨트롤러에 대한 참조

	/**
	 * 초기화 메서드, FXML 로드 후 호출됨
	 */
	@FXML
	public void initialize() {
		playlistDAO = new PlaylistDAO();
		playlists = FXCollections.observableArrayList();
		loadPlaylists();
		playlistListView.setItems(playlists);
		playlistListView.setCellFactory(param -> new ListCell<Playlist>() {
			@Override
			protected void updateItem(Playlist playlist, boolean empty) {
				super.updateItem(playlist, empty);
				if (empty || playlist == null || playlist.getPlaylistName() == null) {
					setText(null);
				} else {
					setText(playlist.getPlaylistName());
				}
			}
		});
	}

	/**
	 * 부모 컨트롤러를 설정하는 메서드
	 * 
	 * @param postingPageController 부모 컨트롤러
	 */
	public void setPostingPageController(PostingPageController postingPageController) {
		this.postingPageController = postingPageController;
	}

	/**
	 * 플레이리스트를 로드하는 메서드
	 */
	private void loadPlaylists() {
		Member currentMember = SessionManager.getInstance().getCurrentMember();
		if (currentMember != null) {
			playlists.setAll(playlistDAO.getPlaylistsByMemberId(currentMember.getId()));
		}
	}

	/**
	 * 확인 버튼 클릭 시 호출되는 메서드
	 */
	@FXML
	private void handleOk() {
		selectedPlaylist = playlistListView.getSelectionModel().getSelectedItem();
		if (selectedPlaylist != null) {
			// 선택한 플레이리스트를 부모 컨트롤러에 추가
			postingPageController.addSelectedPlaylist(selectedPlaylist);
		}
		closeDialog();
	}

	/**
	 * 취소 버튼 클릭 시 호출되는 메서드
	 */
	@FXML
	private void handleCancel() {
		selectedPlaylist = null;
		closeDialog();
	}

	/**
	 * 선택한 플레이리스트를 반환하는 메서드
	 * 
	 * @return 선택한 플레이리스트
	 */
	public Playlist getSelectedPlaylist() {
		return selectedPlaylist;
	}

	/**
	 * 다이얼로그를 닫는 메서드
	 */
	private void closeDialog() {
		Stage stage = (Stage) playlistListView.getScene().getWindow();
		stage.close();
	}
}
