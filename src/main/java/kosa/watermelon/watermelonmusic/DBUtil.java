package kosa.watermelon.watermelonmusic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
	private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe"; // DB URL
	private static final String USER = "admin"; // DB 사용자명
	private static final String PASSWORD = "1234"; // DB 비밀번호

	private static final int MAX_RETRIES = 3;
	private static final int RETRY_DELAY_MS = 1000; // 1초
	
	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
	
	// ResultSet, Statement, Connection을 닫는 메서드
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public static void close(PreparedStatement pstmt, ResultSet rs, Connection conn) {
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Statement, Connection을 닫는 메서드
    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    // Connection만 닫는 메서드
    public static void close(Connection conn) {
        close(conn, null, null);
    }
}