package application;

import java.sql.Connection;
import java.sql.SQLException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class JoinController {
	@FXML
	private TextField name;
	@FXML
	private TextField server;
	@FXML
	private Text errorMessage;
	public void handleJoin(ActionEvent e) {
		Connection connection = DBConnect.getConnection();
		if(name.getText().trim().isEmpty() || server.getText().trim().isEmpty()) {
			errorMessage.setText("Fields are required. Please enter.");
			return;
		}
		
		String userName = name.getText();
		String serverName = server.getText();
		
		ChatManager chatManager = new ChatManager();
		try {
			boolean doesServerExist = chatManager.ifServerExist(serverName);
			
			if(!doesServerExist) {
				errorMessage.setText("Server does not exist. Please enter valid server name.");
				return;
			}
			
			// handle scene change
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("Chat.fxml"));
				
				ChatController controller = new ChatController();
				
				controller.setUserName(userName);
				controller.setServerName(serverName);
				
				loader.setController(controller);
				
				AnchorPane root = (AnchorPane) loader.load();
				Stage stage = (Stage) name.getScene().getWindow();
				
				Scene scene = new Scene(root);
				stage.setScene(scene);
				stage.setOnHidden(event -> {
					controller.shutdown();
					Platform.exit();
				});
			} catch(Exception exception) {
				exception.printStackTrace();
			}
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
