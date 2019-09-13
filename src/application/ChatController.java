package application;

import java.net.*;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.io.*;

public class ChatController {
	String address = "localhost";
	int port = 5000;
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	
	private String userName;
	
	@FXML
	private TextField message;
	
	@FXML
	private TextArea messages;
	
	@FXML
	private VBox messageArea;
	
	@FXML
	private ScrollPane scrollPane;
	
    public void initialize() {
    	connectToSocket();

    	scrollPane.setFitToWidth(true);
    }
    
    public void setUserName(String userName) {
    	this.userName = userName;
    	
    }
    
    public void connectToSocket() {
    	try {
    		socket = new Socket(address, port); 
            System.out.println("Connected");
            dis = new DataInputStream(socket.getInputStream()); 
            dos = new DataOutputStream(socket.getOutputStream()); 
            
            // send user name
            dos.writeUTF(userName);
            
            Thread t = new ServerHandler(socket, dis, dos, messages, messageArea, scrollPane);
            t.start();
    	} catch(UnknownHostException u) { 
            System.out.println(u); 
        } 
        catch(IOException i) { 
            System.out.println(i); 
        }
    }
    
    public void onMessageEnter(ActionEvent e) throws IOException {
    	if(message.getText().trim().isEmpty()) return;
    	dos.writeUTF(message.getText());
    	message.setText("");
    }
}

class ServerHandler extends Thread {
	final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
    private TextArea messages;
    private VBox messageArea;
    private ScrollPane scrollPane;
	public ServerHandler(Socket s, DataInputStream dis, DataOutputStream dos, TextArea messages, VBox messageArea, ScrollPane scrollPane) { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
        this.messages = messages;
        this.messageArea = messageArea;
        this.scrollPane = scrollPane;
    }
	
	@Override
    public void run() {
		String received;
		while (true)  { 
            try { 
            	received = dis.readUTF();
            	final String lambdaReceived = received;
            	messages.appendText(received + "\n");
            	Platform.runLater(() -> {
                	Label label = new Label(lambdaReceived);
                	HBox messageHolder = new HBox(5);
                	
                	Circle avatar = new Circle(20);
                	
                	String firstLetter = lambdaReceived.substring(0, 1);
                	Text text = new Text(firstLetter);
                	text.setBoundsType(TextBoundsType.VISUAL); 
                	
                	StackPane avatarWithLetter = new StackPane();
                	avatarWithLetter.getChildren().addAll(avatar, text);
                	
                	avatar.setFill(Color.CADETBLUE);    
                	HBox.setMargin(label, new Insets(10, 0, 10, 0));
                	messageHolder.getChildren().addAll(avatarWithLetter, label);
                	messageArea.getChildren().add(messageHolder);
                	scrollPane.vvalueProperty().bind(messageArea.heightProperty());
            	});
            } catch (IOException e) { 
                e.printStackTrace(); 
            }
		}
	}
}
