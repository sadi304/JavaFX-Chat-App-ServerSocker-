package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatManager {
	Connection connection;

	public ChatManager() {
		connection = DBConnect.getConnection();
	}
	
	public boolean ifServerExist(String name) throws SQLException {
		boolean status = false;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String query = "SELECT * from server WHERE name = ?";
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, name);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				status = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			preparedStatement.close();
			resultSet.close();
		}
		return status;
	}
	
	public void createServer(String name) throws SQLException {
		PreparedStatement preparedStatement = null;
		String query = "INSERT INTO server (name) values (?)";
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, name);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			preparedStatement.close();
		}
	}
}
