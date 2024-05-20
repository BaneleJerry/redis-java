import jedis.Protocol;
import jedis.memory.DatabasesManager;

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
                    System.out.println(Arrays.toString(parts));
                    // System.out.println(cmd);
                    switch (cmd.toUpperCase()) {
                        case "PING":
                            writer.write(protocol.simpleStringResp("PONG".getBytes()));
                            break;

                        case "ECHO":
                            writer.write(protocol.bulkStringResp(parts[parts.length - 1].getBytes()));
                            break;

                        case "SET":
                            if (parts.length > 7) {
                                DatabasesManager.DEFAULT_DB.set(parts[4], parts[6], Long.parseLong(parts[10]));
                                writer.write(protocol.bulkStringResp("OK"));
                                break;
                            }
                            DatabasesManager.DEFAULT_DB.set(parts[4], parts[6]);
                            writer.write(protocol.simpleStringResp("OK"));

                            break;

                        case "GET":
                            String output = DatabasesManager.DEFAULT_DB.get(parts[4]);
                            writer.write(protocol.bulkStringResp(output));
                            break;

                        case "INFO":
                            writer.write(protocol.bulkStringResp("#Replication \n  role:master"));
                        default:
                            clientSocket.getOutputStream().write(
                                    "-ERR unknown command\r\n".getBytes());
                            break;
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
