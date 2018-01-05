package GFS.Nodes;

import java.io.IOException;
import java.net.ServerSocket;

public class ChunkServer {

    public static void main(String[] args) {
        // Create a Server Socket and bind it to a given port
        try {
            ServerSocket chunkServerSocket = new ServerSocket(Integer.parseInt(args[0]), 10);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Server Socket for Chunk Server is Created");
    }
}
