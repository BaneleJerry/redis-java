package jedis;

import java.net.ServerSocket;
import java.net.Socket;

public class Jedis {
    private int PORT;
    private final int PORT_DEFAULT = 6379;
    private final String HOST = "localhost";
    public boolean isMaster;

    // Constructor with port and isMaster parameters
    public Jedis(int PORT, boolean isMaster) {
        this.PORT = PORT;
        this.isMaster = isMaster;
    }

    // Constructor with only port parameter (default isMaster to false)
    public Jedis(int PORT) {
        this(PORT, true);
    }

    // Default constructor
    public Jedis() {
        this(0, true);
    }

    public void startServer() {
        System.out.println("Logs from your program will appear here!");
        ServerSocket serverSocket = null;

        try {
            // If PORT is invalid, use the default port
            if (PORT <= 0) {
                serverSocket = new ServerSocket(PORT_DEFAULT);
            } else {
                serverSocket = new ServerSocket(PORT);
            }

            serverSocket.setReuseAddress(true);
            System.out.println("Server started on port: " + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Wait for connection from client
                Thread t = new Thread(new HandleClients(clientSocket, this));
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public String getHOST() {
        return HOST;
    }

    public boolean isMaster() {
        return this.isMaster;
    }

    public void setMaster(boolean isMaster) {
        this.isMaster = isMaster;
    }

    public String replicaState(){
        if (this.isMaster){
            return "master";
        }
        return "slave";
    }
}
