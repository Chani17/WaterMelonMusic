package kosa.watermelon.watermelonmusic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProfileEdit extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(Page.class.getResource("profileEDIT.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 600, 410);
		stage.setTitle("프로필 편집");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}