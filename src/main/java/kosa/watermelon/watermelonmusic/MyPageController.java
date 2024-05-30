package kosa.watermelon.watermelonmusic;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MyPageController implements Initializable {

	@FXML
	private ImageView profile_Image;

	@FXML
	private Button profileEdit_BTN;

	@FXML
	private Button goToChart_BTN;

	@FXML
	private TextField userNAME_TextField;

	@FXML
	private TextField userID_TextField;

	@FXML
	private TextField userPW_TextField;

	@FXML
	private TextField userEMAIL_TextField;

	@FXML
	private TableView<Song> tableView;

	@FXML
	private TableColumn<Song, Integer> ranking;

	@FXML
	private TableColumn<Song, String> songName;

	@FXML
	private TableColumn<Song, String> artistName;

	private TemporaryDB temporaryDB;

	private Member currentMember;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		temporaryDB = TemporaryDB.getInstance();
		setListView();

		// 임시로 선택된 사용자 (abcd)
		currentMember = temporaryDB.getMemberById("abcd");
		String[] albums = temporaryDB.getSongs().stream().map(song -> song.getName() + " - " + song.getArtist())
				.toArray(String[]::new);

		// 가져온 데이터를 UI에 설정
		userNAME_TextField.setText(currentMember.getNickname());
		userID_TextField.setText(currentMember.getId());
		userEMAIL_TextField.setText(currentMember.getEMAIL());

		// TextField를 수정 불가능하게 설정
		userNAME_TextField.setEditable(false);
		userID_TextField.setEditable(false);
		userEMAIL_TextField.setEditable(false);

	}

	// 만든 플레이리스트 항목을 넣고 싶은데 아직 구현하지 못하여 인기차트를 대신 넣음
	// 내가 플레이리스트 모아보는 화면 구현 구상 중....

	// 임시 인기차트
	private void setListView() {
		ObservableList<Song> songList = FXCollections.observableArrayList(temporaryDB.getSongs());
		ranking.setCellValueFactory(new PropertyValueFactory<Song, Integer>("id"));
		songName.setCellValueFactory(new PropertyValueFactory<Song, String>("name"));
		artistName.setCellValueFactory(new PropertyValueFactory<Song, String>("artist"));
		tableView.setItems(songList);
	}

	@FXML
	void profileEdit_Action(ActionEvent event) {
		try {
			// FXML 파일 로드
			FXMLLoader loader = new FXMLLoader(getClass().getResource("profileEDIT.fxml"));
			Parent parent = loader.load();

			// ProfileEditController 인스턴스를 가져와서 멤버 설정
			ProfileEditController controller = loader.getController();
			controller.setMember(currentMember);

			// 새 stage 생성 후 기존 스테이지 닫기
			Stage newStage = new Stage();
			Stage currentStage = (Stage) profileEdit_BTN.getScene().getWindow();

			newStage.initModality(Modality.APPLICATION_MODAL); // 새로운 Stage를 모달로 설정
			newStage.setTitle("프로필 편집");
			newStage.setScene(new Scene(parent, 600, 410));
			newStage.show();
			currentStage.close();

			// 프로필 수정 후 업데이트
			userNAME_TextField.setText(currentMember.getNickname());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToChart_Action(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("songChart.fxml"));
			Parent parent = loader.load();

			Stage newStage = new Stage();
			Stage currentStage = (Stage) goToChart_BTN.getScene().getWindow();

			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle("프로필 편집");
			newStage.setScene(new Scene(parent, 600, 410));
			newStage.show();
			currentStage.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}