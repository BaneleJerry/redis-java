import jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
//        --port 6380 --replicaof "localhost 6379"
//        [--port, 6380, --replicaof, localhost 6379]
        ArrayList<String> lisitargs = new ArrayList<String>(List.of(args));
        Jedis jedis;
        int port = 0;
        boolean master = false;
        // Parse the command line arguments
        if (lisitargs.contains("--port")) {
            port = Integer.parseInt(lisitargs.get(lisitargs.indexOf("--port") + 1));
        }

        String masterHost = "";
        int masterPort= 0;
        if (lisitargs.contains("--replicaof")) {
            master = false;
            String[] masterArgs = lisitargs.get(lisitargs.indexOf("--replicaof") + 1).split(" ");
            masterHost = masterArgs[0];
            masterPort = Integer.parseInt(masterArgs[1]);
        } else {
            master = true; // If not a replica, it is a master by default
        }

        // Initialize Jedis instance based on parsed arguments
        if (port != 0) {
            if(!master){jedis = new Jedis(port,masterPort,masterHost);}
            else {jedis = new Jedis(port);}
        } else {
            jedis = new Jedis(); // Use default constructor if no port is specified
        }

        jedis.startServer();
    }
}
