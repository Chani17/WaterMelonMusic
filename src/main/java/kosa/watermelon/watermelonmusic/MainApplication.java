package kosa.watermelon.watermelonmusic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("WaterMelon Music!");
        Image icon = new Image(
        		getClass().getResourceAsStream("/kosa/watermelon/watermelonmusic/watermelon_logo_only.png")); // 로고 이미지 파일 경로 지정
        stage.getIcons().add(icon);
        
        stage.setScene(scene);
        //stage.setResizable(false); // 창 크기 조절 비활성화
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}