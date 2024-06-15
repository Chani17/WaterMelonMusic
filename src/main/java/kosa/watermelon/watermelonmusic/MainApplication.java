package kosa.watermelon.watermelonmusic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * MainApplication 클래스 : 애플리케이션의 메인 진입점을 정의
 * 
 * 작성자 : 김찬희
 */
public class MainApplication extends Application {

	/**
	 * 애플리케이션의 시작 메서드
	 * 
	 * @param stage 메인 스테이지
	 * @throws Exception
	 */
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 800, 600);
		stage.setTitle("WaterMelon Music!");
		Image icon = new Image(
				getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png"));
		stage.getIcons().add(icon);

		stage.setScene(scene);
		stage.show();
	}

	/**
	 * 메인 메서드
	 * 
	 * @param args 프로그램 실행 시 전달되는 인자들
	 */
	public static void main(String[] args) {
		launch(args);
	}
}