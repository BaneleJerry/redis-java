import jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        ArrayList<String> lisitargs = new ArrayList<String>(List.of(args));
        Jedis jedis;
        int port = 0;
        boolean master = false;

        // Parse the command line arguments
        if (lisitargs.contains("--port")) {
            port = Integer.parseInt(lisitargs.get(lisitargs.indexOf("--port") + 1));
        }

        if (lisitargs.contains("--replicaof")) {
            master = false;
        } else {
            master = true; // If not a replica, it is a master by default
        }

        // Initialize Jedis instance based on parsed arguments
        if (port != 0) {
            jedis = new Jedis(port, master);
        } else {
            jedis = new Jedis(); // Use default constructor if no port is specified
        }

        jedis.startServer();
    }
}
