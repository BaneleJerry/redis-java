package jedis;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Jedis {
    private int PORT;
    private final int PORT_DEFAULT = 6379;
    private final String HOST = "localhost";
    private boolean isMaster;
    private int OffSet;
    private String replicationID;

    // Constructor with port and isMaster parameters
    public Jedis(int PORT, boolean isMaster) {
        this.PORT = PORT;
        this.isMaster = isMaster;
        this.replicationID = (this.isMaster) ? generateReplicationID() : "";
        this.OffSet = 0;
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

    private String generateReplicationID(){
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final int STRING_LENGTH = 40;

        Random random = new Random();
        StringBuilder sb = new StringBuilder(STRING_LENGTH);

        for (int i = 0; i < STRING_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
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

    public String getReplicationID() {
        return replicationID;
    }

    public int getOffSet() {
        return OffSet;
    }
}
