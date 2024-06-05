module kosa.watermelon.watermelonmusic {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
	requires com.oracle.database.jdbc;

    opens kosa.watermelon.watermelonmusic to javafx.fxml;
    exports kosa.watermelon.watermelonmusic;
}