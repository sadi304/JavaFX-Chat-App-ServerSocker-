package application;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List; 

public class Server extends Thread {
    private final int port;
    private final String name;
    
    private ArrayList<ServerWorker> workerList = new ArrayList<>(); 
	
    public Server(int port, String name) {
    	this.port = port;
    	this.name = name;
    }
    
    public String getServerName() {
    	return this.name;
    }
    
    public List<ServerWorker> getWorkerList() {
    	return workerList;
    }
    
    public void removeWorker(ServerWorker worker) {
    	this.workerList.remove(worker);
    }
    
    @Override
    public void run() {
        try { 
        	ServerSocket server = new ServerSocket(port); 
            while(true) {
	            Socket socket = server.accept(); 
	            
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				
				String userName = "";
				try { 
					userName = dis.readUTF();
				} catch (IOException e) {
					e.printStackTrace();
				}
	            
	            System.out.println("A Client accepted: " + socket);  
                
                ServerWorker worker = new ServerWorker(this, socket, userName);
                workerList.add(worker);
                worker.start();
            }
            
        } 
        catch(Exception exception) { 
            exception.printStackTrace();
        } 
    }
	
}
