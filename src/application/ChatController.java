package application;

import java.net.*;
import java.sql.SQLException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
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
	private String serverName;
	
	@FXML
	private TextField message;
	
	@FXML
	private VBox messageArea;
	
	@FXML
	private ScrollPane scrollPane;
	
	@FXML
	private Label onlineUsers;
	
    public void initialize() {
    	setEarliersMessages();
    	connectToSocket();
    	scrollPane.setFitToWidth(true);
    }
    

    public void shutdown(){
    	try {
			dos.writeUTF("close#_#connection");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("Stage is closing");
    }
    
    public void setEarliersMessages() {
    	ChatManager chatManager = new ChatManager();
    	
    	try {
			for(String message: chatManager.getEarlierMessages(this.serverName)) {
				System.out.println(message);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public void setUserName(String userName) {
    	this.userName = userName;
    	
    }
    
    public void setServerName(String serverName) {
    	this.serverName = serverName;
    }
    
    public void connectToSocket() {
    	try {
    		socket = new Socket(address, port); 
            System.out.println("Connected");
            dis = new DataInputStream(socket.getInputStream()); 
            dos = new DataOutputStream(socket.getOutputStream()); 
            
            // send user name
            dos.writeUTF(userName);
            
            Thread t = new ServerHandler(socket, dis, dos, messageArea, scrollPane, onlineUsers);
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
    	dos.writeUTF("message#_#" + message.getText());
    	message.setText("");
    }
}

class ServerHandler extends Thread {
	final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s; 
    private VBox messageArea;
    private ScrollPane scrollPane;
    private Label onlineUsers;
	public ServerHandler(Socket s, DataInputStream dis, DataOutputStream dos, VBox messageArea, ScrollPane scrollPane, Label onlineUsers) { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
        this.messageArea = messageArea;
        this.scrollPane = scrollPane;
        this.onlineUsers = onlineUsers;
    }
	
	@Override
    public void run() {
		String received;
		while (true)  { 
            try { 
            	received = dis.readUTF();
            	String[] message = received.split("#_#");
            	
            	final String dataMessage = message[1];
            	final String type = message[0];
        		if(type.equalsIgnoreCase("message")) {
        			Platform.runLater(() -> {
                    	Label label = new Label(dataMessage);
                    	HBox messageHolder = new HBox(5);
                    	
                    	Circle avatar = new Circle(20);
                    	
                    	String firstLetter = dataMessage.substring(0, 1);
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
        		} else {
        			Platform.runLater(() -> {
        				onlineUsers.setText(dataMessage);
        			});
        		}
            	
            } catch (IOException e) { 
                e.printStackTrace(); 
                break;
            }
		}
	}
}
