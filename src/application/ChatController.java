package application;

import java.net.*;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
	
    public void initialize() {
    	connectToSocket();
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
            
            Thread t = new ServerHandler(socket, dis, dos, messages);
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
	public ServerHandler(Socket s, DataInputStream dis, DataOutputStream dos, TextArea messages) { 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
        this.messages = messages;
    }
	
	@Override
    public void run() {
		String received;
		while (true)  { 
            try { 
            	received = dis.readUTF();
            	messages.appendText(received + "\n");
            } catch (IOException e) { 
                e.printStackTrace(); 
            }
		}
	}
}
