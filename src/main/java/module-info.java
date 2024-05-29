module kosa.watermelon.watermelonmusic {
    requires javafx.controls;
    requires javafx.fxml;


    opens kosa.watermelon.watermelonmusic to javafx.fxml;
    exports kosa.watermelon.watermelonmusic;
}