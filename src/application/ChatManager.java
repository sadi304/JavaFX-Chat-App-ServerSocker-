package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	
	public void addMessage(String message, String serverName) throws SQLException {
		PreparedStatement preparedStatement = null;
		String query = "INSERT INTO ServerMessage (message, serverName) values (?, ?)";
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, message);
			preparedStatement.setString(2, serverName);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			preparedStatement.close();
		}
	}
	
	public List<String> getEarlierMessages(String serverName) throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String query = "SELECT message from ServerMessage where serverName = ? order by id DESC LIMIT 20";

		List<String> messages = new ArrayList<>();
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, serverName);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				messages.add(resultSet.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			preparedStatement.close();
			resultSet.close();
		}
		
		return messages;
	}
}
