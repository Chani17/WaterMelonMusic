package kosa.watermelon.watermelonmusic;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleResultSet;
import oracle.sql.BFILE;

public class TestDB_mypage {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "admin";
    private static final String PASSWORD = "1234";

    private static TestDB_mypage instance;

    private TestDB_mypage() {
    }

    public static TestDB_mypage getInstance() {
        if (instance == null) {
            instance = new TestDB_mypage();
        }
        return instance;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public Member getMemberById(String memberId) throws IOException {
        String query = "SELECT MEMBER_ID, MEMBER_PW, EMAIL, NICKNAME, PROFILE_IMAGE, GENDER, BIRTH FROM MEMBER WHERE MEMBER_ID = ?";
        try (Connection connection = getConnection();
        		PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String id = resultSet.getString("MEMBER_ID");
                String pw = resultSet.getString("MEMBER_PW");
                String email = resultSet.getString("EMAIL");
                String nickname = resultSet.getString("NICKNAME");
                byte[] profileImage = getProfileImage(resultSet); // BFILE 읽기
                String gender = resultSet.getString("gender");
                java.sql.Date birth = resultSet.getDate("birth");
                return new Member(id, pw, email, nickname, profileImage, gender, birth.toLocalDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private byte[] getProfileImage(ResultSet resultSet) throws SQLException, IOException {
        BFILE bfile = ((OracleResultSet) resultSet).getBFILE("PROFILE_IMAGE");
        if (bfile == null) {
            return null;
        }

        bfile.openFile();
        try (InputStream inputStream = bfile.getBinaryStream()) {
            byte[] fileData = inputStream.readAllBytes();
            return fileData;
        }
    }

    public void updateMember(Member updatedMember) {
        String query = "UPDATE MEMBER SET MEMBER_PW=?, NICKNAME=? WHERE MEMBER_ID=?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, updatedMember.getPw());
            //preparedStatement.setString(2, updatedMember.getEmail());
            preparedStatement.setString(2, updatedMember.getNickname());
            //preparedStatement.setString(4, updatedMember.getProfileImagePath()); // BFILE에 대한 파일 경로
            //preparedStatement.setBytes(4, updatedMember.getProfileImage());
            //preparedStatement.setString(4, updatedMember.getGender());
            //preparedStatement.setDate(5, java.sql.Date.valueOf(updatedMember.getBirth()));
            preparedStatement.setString(3, updatedMember.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("Member not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
