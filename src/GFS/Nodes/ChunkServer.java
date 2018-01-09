package GFS.Nodes;

import GFS.Threads.UserInputThread;
import GFS.Transport.TCPReceiver;
import GFS.Transport.TCPSender;
import GFS.WireFormats.ChunkRegisterReq;
import GFS.utils.ConfigurationManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChunkServer {

    private int port;
    private ServerSocket serverSocket;

    private static ChunkServer chunkServer = null;
    private static TCPSender controllerSender;

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

            UserInputThread inputThread = new UserInputThread(chunkServer);
            inputThread.start();

            // Get registered on Controller
            Socket controllerSocket = new Socket(configManager.getServerIP(), configManager.getServerPort());
            TCPReceiver controllerReceiver = new TCPReceiver(controllerSocket);
            controllerReceiver.start();
            controllerSender = new TCPSender(controllerSocket);

            // Create Register Request
            int localPort = chunkServer.serverSocket.getLocalPort();
            String localIP = controllerSocket.getLocalSocketAddress().toString();
            localIP = localIP.replace("/","");
            System.out.println(localIP);
            ChunkRegisterReq registerReq = new ChunkRegisterReq(localIP,configManager.getServerPort(),localPort);
            byte [] bRequestArray = registerReq.getByteArray();
            controllerSender.send_and_maintain(bRequestArray);

            // Listening for the connections
            while (true)
            {
                Socket clSocket = chunkServer.serverSocket.accept();
                TCPSender sender = new TCPSender(clSocket);
                Thread tcpReceiver = new TCPReceiver(clSocket);
                tcpReceiver.start();
            }
        } else {
            System.out.println("Incorrect Configuration.");
            System.exit(1);
        }
    }
}
