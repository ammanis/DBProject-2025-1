// DBConnection.java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/newmadangdb", "root", "1234"); // JDBC 연결
            System.out.println("DB 연결 완료");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
        }
        return conn;
    }
}
