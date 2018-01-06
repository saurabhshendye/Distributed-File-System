package GFS.Nodes;

import GFS.Transport.TCPReceiver;
import GFS.Transport.TCPSender;
import GFS.utils.ConfigurationManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChunkServer {

    private int port;
    private ServerSocket serverSocket;

    private static ChunkServer chunkServer = null;
    private static TCPSender sender;

    private ChunkServer(int port) throws IOException{
        serverSocket = new ServerSocket(port,10);
    }

    public static void main(String[] args) throws IOException {
        ConfigurationManager configManager = new ConfigurationManager(1);
        if (configManager.isValid()){
            // Creating a local Server
            try {
                chunkServer = new ChunkServer(configManager.getLocalPort());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Listening for the connections
            while (true)
            {
                Socket clientSocket = chunkServer.serverSocket.accept();
                sender = new TCPSender(clientSocket);
                Thread receiver = new TCPReceiver(clientSocket);
                receiver.start();
            }
        } else {
            System.out.println("Incorrect Configuration.");
            System.exit(1);
        }
    }
}
