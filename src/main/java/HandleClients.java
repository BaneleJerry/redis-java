import jedis.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class HandleClients implements Runnable {

    private final Socket clientSocket;
    private final Protocol protocol;

    public HandleClients(Socket clientSocket) {
        this.clientSocket = clientSocket;
        protocol = new Protocol();
    }

    @Override
    public void run() {
        try {
            OutputStream writer = clientSocket.getOutputStream();
            byte[] input = new byte[1024];
            while (clientSocket.isConnected()) {
                clientSocket.getInputStream().read(input);
                String req = new String(input).trim();
                String[] parts = req.split("\r\n");
                
                
                if (parts.length > 2) {
                    String cmd = parts[2];
                    System.out.println(cmd);
                    switch (cmd.toUpperCase()) {
                        case "PING":
                            writer.write(protocol.simpleStringResp("PONG".getBytes()));
                            break;

                        case "ECHO":
                            writer.write(protocol.bulkStringResp(parts[parts.length -1].getBytes()));
                    }
                    writer.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
}
