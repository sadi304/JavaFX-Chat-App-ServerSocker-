package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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

        	List<ServerWorker> workerList = server.getWorkerList();
			for(ServerWorker worker : workerList) {
        		worker.sendOnlineUsers(workerList.size());
        	}
			
			while (true)  { 
	            try { 
	            	receivedMessage = dis.readUTF(); 
	            	String[] splitMessage = receivedMessage.split("#_#");
	            	
	            	String message = splitMessage[1];
	            	String type = splitMessage[0];
	            	if(type.equalsIgnoreCase("message")) {
		            	for(ServerWorker worker : workerList) {
		            		worker.send(this.userName, message);
		            	}
	            	} else {
	            		for(ServerWorker worker : workerList) {
	            			if(worker.userName != this.userName) {
	            				worker.sendOnlineUsers(workerList.size() - 1);
	            			}
		            	}
	            		
	            		break;
	            	}
	            } catch (IOException e) { 
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

	private void send(String userName, String message) {
		try {
			dos.writeUTF("message#_#" + userName + ": " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
