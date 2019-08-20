package application;

import java.io.*; 

public class ServerMain {
    public static void main(String args[]) throws IOException { 
        int port = 5000;
        Server server = new Server(port);
        server.start();
    } 
}