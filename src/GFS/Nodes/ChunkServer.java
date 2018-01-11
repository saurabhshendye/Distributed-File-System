package GFS.Nodes;

import GFS.Threads.HeartbeatThread30;
import GFS.Threads.HeartbeatThread5;
import GFS.Threads.UserInputThread;
import GFS.Transport.TCPReceiver;
import GFS.Transport.TCPSender;
import GFS.WireFormats.ChunkRegisterReq;
import GFS.utils.ConfigurationManager;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChunkServer {

    private int port;
    private ServerSocket serverSocket;

    private static ChunkServer chunkServer = null;
    private static TCPSender controllerSender;

    private boolean isRegistered = false;

    private ChunkServer(int port) throws IOException{
        serverSocket = new ServerSocket(port,10);
    }

    public static void main(String[] args) throws IOException {
        ConfigurationManager configManager = new ConfigurationManager(1);
        if (configManager.isValid()){
            // Creating a local Server
            try {
                chunkServer = new ChunkServer(configManager.getLocalServerPort());
            } catch (IOException e) {
                e.printStackTrace();
            }


            // Get registered on Controller
            Socket controllerSocket = new Socket(configManager.getServerIP(), configManager.getServerPort());
            TCPReceiver controllerReceiver = new TCPReceiver(controllerSocket, chunkServer);
            controllerReceiver.start();
            controllerSender = new TCPSender(controllerSocket);

            // Create Register Request
            short localPort = (short) controllerSocket.getLocalPort();
            String localIP = controllerSocket.getLocalSocketAddress().toString();
            localIP = localIP.replace("/","");
            System.out.println(localIP);
            ChunkRegisterReq registerReq = new ChunkRegisterReq(localIP,(short)configManager.getLocalServerPort());
            byte [] bRequestArray = registerReq.getByteArray();

            // send Register request
            controllerSender.send_and_maintain(bRequestArray);

            while (!chunkServer.isRegistered){

            }

            System.out.println("Registered Successfully");

            // Start thread to send hearbeat every 5 minutes
            HeartbeatThread5 heartbeatThread5 = new HeartbeatThread5();
            heartbeatThread5.start();

            // Start thread to send heartbeat every 30 seconds
            HeartbeatThread30 heartbeatThread30 = new HeartbeatThread30();
            heartbeatThread30.start();

            // For taking user input from console
            UserInputThread inputThread = new UserInputThread(chunkServer, heartbeatThread5, heartbeatThread30);
            inputThread.start();

            File file = new File("/tmp");
            System.out.println(file.getFreeSpace());

            // Listening for the connections
            while (true)
            {
                Socket clSocket = chunkServer.serverSocket.accept();
                TCPSender sender = new TCPSender(clSocket);
                Thread tcpReceiver = new TCPReceiver(clSocket, chunkServer);
                tcpReceiver.start();
            }
        } else {
            System.out.println("Incorrect Configuration.");
            System.exit(1);
        }
    }

    public synchronized void setRegistered(){
        this.isRegistered = true;
    }
}
