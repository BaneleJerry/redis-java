import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import jedis.memory.DatabasesManager;

public class Main {
    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket = null;
        int port = 6379;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Wait for connection from client
                Thread t = new Thread(new HandleClients(clientSocket));
                t.start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());

    }
}
}
