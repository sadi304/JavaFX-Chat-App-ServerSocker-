package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class ServerWorker extends Thread {
	private final Socket clientSocket;
	private final Server server;
	DataOutputStream dos;
	
	String userName;
	
	public ServerWorker(Server server, Socket clientSocket, String userName) {
		this.server = server;
		this.clientSocket = clientSocket;
		this.userName = userName;
	}
	
	@Override
	public void run() {
		try {
			DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
		    dos = new DataOutputStream(clientSocket.getOutputStream());
			String receivedMessage;
			String userNames = "";
        	List<ServerWorker> workerList = server.getWorkerList();
        	for(ServerWorker worker : workerList) {
        		userNames += worker.userName + ",";
        	}
			for(ServerWorker worker : workerList) {
        		worker.sendOnlineUsers(workerList.size());
        		worker.sendOnlineUserNames(userNames);
        	}
			
			ChatManager chatManager = new ChatManager();
			
			while (true)  { 
	            try { 
	            	receivedMessage = dis.readUTF();
	            	String[] splitMessage = receivedMessage.split("#_#");
	            	
	            	String message = splitMessage[1];
	            	String type = splitMessage[0];
	            	if(type.equalsIgnoreCase("message")) {
	            		chatManager.addMessage((this.userName + ": " + message), this.server.getServerName());
		            	for(ServerWorker worker : workerList) {
		            		worker.send(this.userName, message);
		            	}
	            	} else if(type.equalsIgnoreCase("individual")) {
	            		String[] splitIndividualMessage = message.split(":");
	            		String messageNew = splitIndividualMessage[1];
	            		String to = splitIndividualMessage[0];
//	            		chatManager.addMessage((this.userName + ": " + message), this.server.getServerName());
	            		for(ServerWorker worker : workerList) {
	            			if(worker.userName.equalsIgnoreCase(to) || worker.userName.equalsIgnoreCase(this.userName)) {
			            		worker.send(this.userName, messageNew);
	            			}
		            	}
	            	}
	            	
	            	else {
	            		String updatedUserNames = "";
	            		for(ServerWorker worker : workerList) {
	            			if(worker.userName != this.userName) {
	            				updatedUserNames += worker.userName + ",";
	            			}
	            		}
	            		for(ServerWorker worker : workerList) {
	            			if(worker.userName != this.userName) {
	            				worker.sendOnlineUsers(workerList.size() - 1);
	            				worker.sendOnlineUserNames(updatedUserNames);
	            			}
		            	}
	            		
	            		this.server.removeWorker(this);
	            		
	            		break;
	            	}
	            } catch (IOException | SQLException e) { 
	                e.printStackTrace(); 
	                break;
	            }
			}
			
			clientSocket.close();
			dis.close();
			dos.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
	}
	
	private void sendOnlineUsers(int count) {
		try {
			dos.writeUTF("online#_#" + count);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendOnlineUserNames(String userNames) {
		try {
			dos.writeUTF("online_users#_#" + userNames);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void send(String userName, String message) {
		try {
			dos.writeUTF("message#_#" + userName + ": " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
