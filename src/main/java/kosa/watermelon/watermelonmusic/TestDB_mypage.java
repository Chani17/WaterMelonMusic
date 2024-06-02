package kosa.watermelon.watermelonmusic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestDB_mypage {
	// git에 올릴 때 URL, USER, PASSSWORD 삭제하고 주석처리해서 올리기!!!
	//private static final String URL = "";
    //private static final String USER = "";
    //private static final String PASSWORD = "";
    
    private static TestDB_mypage instance;
    private List<Member> members;
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public TestDB_mypage() {
		this.members = new ArrayList<>();
    }
    
    public static Member getMemberById(String memberId) {
        String query = "SELECT member_id, member_pw, email, nickname, gender, birth FROM Member WHERE member_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String id = resultSet.getString("member_id");
                String pw = resultSet.getString("member_pw");
                String email = resultSet.getString("email");
                String nickname = resultSet.getString("nickname");
                String gender = resultSet.getString("gender");
                java.sql.Date birth = resultSet.getDate("birth");

                return new Member(id, pw, nickname, email, gender, birth.toLocalDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TestDB_mypage getInstance() {
        if(instance == null) instance = new TestDB_mypage();
        return instance;
    }
    
	public void updateMember(Member updatedMember) {
		String query = "UPDATE Member SET member_pw=?, email=?, nickname=?, gender=?, birth=? WHERE member_id=?";
	    try (Connection connection = getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setString(1, updatedMember.getPw());
	        preparedStatement.setString(2, updatedMember.getEmail());
	        preparedStatement.setString(3, updatedMember.getNickname());
	        preparedStatement.setString(4, updatedMember.getGender());
	        preparedStatement.setDate(5, java.sql.Date.valueOf(updatedMember.getBirth()));
	        preparedStatement.setString(6, updatedMember.getId());
	        
	        int rowsAffected = preparedStatement.executeUpdate();
	        if (rowsAffected == 0) {
	            throw new IllegalArgumentException("Member not found");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
}