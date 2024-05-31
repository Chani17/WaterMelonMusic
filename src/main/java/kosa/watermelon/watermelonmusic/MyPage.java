package kosa.watermelon.watermelonmusic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// 마이페이지에서 정보 탐색되는지 확인하는 페이지 (추후 삭제 예정)
public class MyPage extends Application {

	@Override
    public void start(Stage startstage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MyPage.class.getResource("mypage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 410);
        startstage.setTitle("마이페이지");
        startstage.setScene(scene);
        startstage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}