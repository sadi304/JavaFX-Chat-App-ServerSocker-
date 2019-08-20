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
			
			while (true)  { 
	            try { 
	            	receivedMessage = dis.readUTF(); 
	            	List<ServerWorker> workerList = server.getWorkerList();
	            	
	            	for(ServerWorker worker : workerList) {
	            		worker.send(this.userName, receivedMessage);
	            	}
	            } catch (IOException e) { 
	                e.printStackTrace(); 
	            }
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
	}

	private void send(String userName, String message) {
		try {
			dos.writeUTF(userName + ": " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
