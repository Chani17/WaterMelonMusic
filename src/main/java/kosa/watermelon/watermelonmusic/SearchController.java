package kosa.watermelon.watermelonmusic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import oracle.jdbc.OracleResultSet;
import oracle.sql.BFILE;

public class SearchController {

    @FXML private TextField search_TextField;

    @FXML private Button search_BTN;
    
    @FXML private ImageView search_ImageView;

    @FXML
    private TableView<Song> tableView; // tableView 변수 선언
    
    public void setTableView(TableView<Song> tableView) {
        this.tableView = tableView; // tableView 설정 메서드 정의
    }
    
    private Member currentMember;
 
    public void setMember(Member member) {
		this.currentMember = member;
    }
    
    @FXML
    public void initialize() {
        search_TextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");

        search_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                search_TextField.setPromptText("검색어를 입력하세요");
            } else {
                search_TextField.setPromptText("");
            }
        });

        search_TextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                search_TextField.setPromptText("검색어를 입력하세요");
            } else if (search_TextField.getText().isEmpty()) {
                search_TextField.setPromptText("검색어를 입력하세요");
            }
        });
    }
    
    @FXML
    void search_Action(ActionEvent event) {
        String searchSong = search_TextField.getText();
        ObservableList<Song> searchResults = FXCollections.observableArrayList(searchSongs(searchSong));
        tableView.setItems(searchResults);
		System.out.println("검색된 단어 : " + searchSong);
	}

    private List<Song> searchSongs(String keyword) {
        List<Song> result = new ArrayList<>();
        String query = "SELECT " +
                "ROW_NUMBER() OVER (ORDER BY s.click_count DESC) AS ranking, " +
                "s.song_id, s.song_name, a.artist_name, s.click_count " +
                "FROM Song s " +
                "LEFT OUTER JOIN Artist a " +
                "ON s.artist_id = a.artist_id " +
                "WHERE LOWER(s.song_name) LIKE ? OR LOWER(a.artist_name) LIKE ? " +
                "ORDER BY s.click_count DESC";

        try (Connection conn = DBConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");
            pstmt.setString(2, "%" + keyword.toLowerCase() + "%");
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                BFILE bfile = ((OracleResultSet) rs).getBFILE("album_cover");
                bfile.openFile(); // BFILE 열기
                InputStream inputStream = bfile.getBinaryStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                byte[] imageData = outputStream.toByteArray();
                outputStream.close();
                inputStream.close();
                bfile.closeFile(); // 자원 누수 방지를 위함

            	Song song = new Song(
                    rs.getInt("ranking"), 
                    rs.getLong("song_id"), 
                    rs.getString("song_name"), 
                    rs.getString("artist_name"),
                    imageData,
                    rs.getLong("click_count")
                );
                result.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    
    private Connection DBConnection() {
        try {
            return DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}