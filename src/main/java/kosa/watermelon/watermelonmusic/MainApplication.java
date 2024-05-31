package kosa.watermelon.watermelonmusic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 464);
        stage.setTitle("WaterMelon Music!");
        stage.setScene(scene);
        //stage.setResizable(false); // 창 크기 조절 비활성화
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
