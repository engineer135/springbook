package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Engineer135
 *
 * 
 * 
 */
public class NUserDao extends UserDao {
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mariadb://localhost/springbook", "spring", "book");
		
		return c;
	}
}
