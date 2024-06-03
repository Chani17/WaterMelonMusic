package kosa.watermelon.watermelonmusic;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestDB_mypage {
    // Git에 올릴 때 URL, USER, PASSWORD 삭제하고 주석처리해서 올리기!!!
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

    public Member getMemberById(String memberId) {
        String query = "SELECT member_id, member_pw, email, nickname, profile_image, gender, birth FROM Member WHERE member_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String id = resultSet.getString("member_id");
                String pw = resultSet.getString("member_pw");
                String email = resultSet.getString("email");
                String nickname = resultSet.getString("nickname");
                Blob profileImageBlob = resultSet.getBlob("profile_image");
                byte[] profileImage = profileImageBlob.getBytes(1, (int) profileImageBlob.length());
                String gender = resultSet.getString("gender");
                java.sql.Date birth = resultSet.getDate("birth");
                return new Member(id, pw, nickname, profileImage, email, gender, birth.toLocalDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateMember(Member updatedMember) {
        String query = "UPDATE Member SET member_pw=?, email=?, nickname=?, profile_image=?, gender=?, birth=? WHERE member_id=?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, updatedMember.getPw());
            preparedStatement.setString(2, updatedMember.getEmail());
            preparedStatement.setString(3, updatedMember.getNickname());
            preparedStatement.setBytes(4, updatedMember.getProfileImage());
            preparedStatement.setString(5, updatedMember.getGender());
            preparedStatement.setDate(6, java.sql.Date.valueOf(updatedMember.getBirth()));
            preparedStatement.setString(7, updatedMember.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("Member not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
