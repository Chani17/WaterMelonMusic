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

/**
 * MemberController 클래스 : 회원 정보를 관리하는 기능을 제공
 * 
 * 작성자 : 김효정
 */
public class MemberController {

	private static MemberController instance;

	// MemberController의 기본 생성자
	private MemberController() {
	}

	/**
	 * MemberController의 인스턴스를 반환하는 메서드 (싱글톤 패턴)
	 * 
	 * @return MemberController 인스턴스
	 */
	public static MemberController getInstance() {
		if (instance == null) {
			instance = new MemberController();
		}
		return instance;
	}

	/**
	 * 데이터베이스 연결을 반환하는 메서드
	 * 
	 * @return Connection 객체
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		return DBUtil.getConnection();
	}

	/**
	 * 회원 ID로 회원 정보를 가져오는 메서드
	 * 
	 * @param memberId 회원 ID
	 * @return Member 객체
	 * @throws IOException
	 */
	public Member getMemberById(String memberId) throws IOException {
		String query = "SELECT MEMBER_ID, MEMBER_PW, EMAIL, NICKNAME, PROFILE_IMAGE, GENDER, BIRTH FROM MEMBER WHERE MEMBER_ID = ?";
		Connection connection = null; // 변수를 선언하고 null로 초기화
		try {
			connection = getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
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
		} finally {
			DBUtil.close(connection); // 연결 닫기
		}
		return null;
	}

	/**
	 * ResultSet에서 프로필 이미지를 BFILE로 읽어오는 메서드
	 * 
	 * @param resultSet ResultSet 객체
	 * @return 프로필 이미지 바이트 배열
	 * @throws SQLException
	 * @throws IOException
	 */
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

	/**
	 * 회원 정보를 업데이트하는 메서드
	 * 
	 * @param updatedMember 업데이트할 회원 정보
	 */
	public void updateMember(Member updatedMember) {
		String query = "UPDATE MEMBER SET MEMBER_PW=?, NICKNAME=? WHERE MEMBER_ID=?";
		Connection connection = null; // 변수를 선언하고 null로 초기화

		try {
			connection = getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, updatedMember.getPw());
			preparedStatement.setString(2, updatedMember.getNickname());
			preparedStatement.setString(3, updatedMember.getId());

			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected == 0) {
				throw new IllegalArgumentException("Member not found");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(connection); // 연결 닫기
		}
	}
}
