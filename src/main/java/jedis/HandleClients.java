package jedis;
import jedis.memory.DatabasesManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;

public class HandleClients implements Runnable {

    private final Socket clientSocket;
    private final Protocol protocol;
    private final Jedis jedis;

    public HandleClients(Socket clientSocket, Jedis jedis) {
        this.clientSocket = clientSocket;
        protocol = new Protocol();
        this.jedis = jedis;
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
                            String replicationInfo = "#Replication role:" + jedis.replicaState()
                                    + " \nmaster_replid:" + jedis.getReplicationID()
                                    + "\nmaster_repl_offset:" + jedis.getOffSet();
                            writer.write(protocol.bulkStringResp(replicationInfo));
                            break;

                        case "REPLCONF":
                            writer.write(protocol.simpleStringResp("OK"));
                            break;

                        case "PSYNC":
                            String msg = "FULLRESYNC " + jedis.getReplicationID() + " " + jedis.getOffSet();
                            writer.write(protocol.simpleStringResp(msg.getBytes()));

                            String fileContents = "UkVESVMwMDEx+glyZWRpcy12ZXIFNy4yLjD6CnJlZGlzLWJpdHPAQPoFY3RpbWXCbQi8ZfoIdXNlZC1tZW3CsMQQAPoIYW9mLWJhc2XAAP/wbjv+wP9aog==";
                            byte[] bytes = Base64.getDecoder().decode(fileContents);
                            writer.write(("$" + bytes.length + "\r\n").getBytes());
                            writer.write(bytes);
                            break;
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
