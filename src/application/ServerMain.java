package application;

import java.io.*;
import java.sql.SQLException;
import java.util.Scanner; 

public class ServerMain {
    public static void main(String args[]) throws IOException { 
    	ChatManager chatManager = new ChatManager();
        int port = 5000;
        System.out.println("Please enter server name: (we'll create one for you if it doesn't exist");
        
        Scanner scanner = new Scanner(System.in);
        String serverName = scanner.nextLine();
        scanner.close();
        
        try {
        	boolean doesExist = chatManager.ifServerExist(serverName);
        	if(!doesExist) {
        		chatManager.createServer(serverName);
        	}
        	
            Server server = new Server(port, serverName);
            server.start();
            System.out.println("Server statarted at " + port);
        } catch (SQLException e) {
			e.printStackTrace();
		}
        
    } 
}