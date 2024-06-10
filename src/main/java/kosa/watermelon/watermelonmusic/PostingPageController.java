package kosa.watermelon.watermelonmusic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PostingPageController {
	@FXML
	private Button goToDashboard_BTN;
	
	@FXML
    private Button addPlaylist_BTN;
	
	@FXML
    private TableView<Playlist> playlistTableView;
    @FXML
    private TableColumn<Playlist, String> nameColumn;
    @FXML
    private TableColumn<Playlist, String> memberColumn;
    @FXML
    private TableColumn<Playlist, String> dateColumn;

    private ObservableList<Playlist> playlists;

	@FXML
    public void initialize() {
		playlists = FXCollections.observableArrayList();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("playlistName"));
        memberColumn.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("postDate"));

        playlistTableView.setItems(playlists);
    }
	
	@FXML
    private void handleAddPlaylist(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("playlistDialog.fxml"));
            Parent parent = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("플레이리스트 선택");
            stage.setScene(new Scene(parent, 400, 300));
            stage.showAndWait();

            PlaylistDialogController controller = loader.getController();
            Playlist selectedPlaylist = controller.getSelectedPlaylist();
            if (selectedPlaylist != null) {
            	playlists.add(selectedPlaylist);
                savePlaylistToPostAndMpp(selectedPlaylist); // 플레이리스트를 POST와 MPP 테이블에 저장
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private void savePlaylistToPostAndMpp(Playlist playlist) {
	    Member currentMember = SessionManager.getInstance().getCurrentMember();
	    if (currentMember == null) {
	        return; // 현재 사용자가 없으면 저장하지 않음
	    }

	    String memberId = currentMember.getId();
	    String postSql = "INSERT INTO POSTING (POST_ID, POST_DATE) VALUES (?, ?)";
	    String mppSql = "INSERT INTO MPP (PLAYLIST_ID, POST_ID, MEMBER_ID) VALUES (?, ?, ?)";

	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement postStmt = conn.prepareStatement(postSql);
	         PreparedStatement mppStmt = conn.prepareStatement(mppSql)) {

	        // POST 테이블에 데이터 삽입
	        postStmt.setLong(1, generateNewId());
	        postStmt.setDate(2, Date.valueOf(LocalDate.now())); // 현재 날짜 설정
	        postStmt.executeUpdate();

	        // MPP 테이블에 데이터 삽입
	        mppStmt.setLong(1, playlist.getPlaylistId());
	        mppStmt.setLong(2, generateNewId());
	        mppStmt.setString(3, memberId);
	        mppStmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}



    private Long generateNewId() {
    	Long newId = null;
        String sql = "SELECT POSTING_SEQ.NEXTVAL FROM DUAL";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
        	
            if (rs.next()) {
                newId = rs.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(newId);
        return newId;
    }

	@FXML // 포스팅 → DashBoard 페이지 이동 이벤트 처리
	private void goToDashboard_Action(ActionEvent event)  {
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
            Parent parent = loader.load();
            
            Stage newStage = new Stage();
			Stage currentStage = (Stage) goToDashboard_BTN.getScene().getWindow();
			
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("메인 화면");
			newStage.setScene(new Scene(parent, 800, 600));
			Image icon = new Image(
	        		getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고 이미지 파일 경로 지정
			newStage.getIcons().add(icon);
			newStage.show();
			currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
