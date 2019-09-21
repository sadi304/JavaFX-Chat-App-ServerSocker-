package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
	private static final String jdbc = "com.mysql.cj.jdbc.Driver";
	private static final String connectionString = "jdbc:mysql://localhost/dash-chat";
	
	private static Connection connection = null;
	
	public static Connection getConnection() {
		try {
			Class.forName(jdbc);
			connection = DriverManager.getConnection(connectionString, "root", "");
		} catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return connection;
	}
}
