package GFS.Nodes;

import GFS.Transport.TCPReceiver;
import GFS.Transport.TCPSender;
import GFS.utils.ConfigurationManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Controller {

    private int port;
    private ServerSocket serverSocket;

    private static Controller controllerServer = null;
    private static TCPSender sender;


    private Controller(int port) throws IOException{
        serverSocket = new ServerSocket(port,10);
    }

    public static void main(String[] args) throws IOException{
        ConfigurationManager configManager = new ConfigurationManager(2);
        if (configManager.isValid()){
            // Creating a local Server
            try {
                controllerServer = new Controller(configManager.getLocalPort());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Listening for the connections
            while (true)
            {
                Socket clientSocket = controllerServer.serverSocket.accept();
                Thread receiver = new TCPReceiver(clientSocket);
                receiver.start();
                sender = new TCPSender(clientSocket);
            }
        } else {
            System.out.println("Incorrect Configuration.");
            System.exit(1);
        }
    }
}
