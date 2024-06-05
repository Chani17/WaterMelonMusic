module kosa.watermelon.watermelonmusic {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.media;
    requires com.oracle.database.jdbc;
    requires java.management;

    opens kosa.watermelon.watermelonmusic to javafx.fxml;
    exports kosa.watermelon.watermelonmusic;
}