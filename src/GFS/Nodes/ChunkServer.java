package GFS.Nodes;

import GFS.Threads.HeartbeatThread30;
import GFS.Threads.HeartbeatThread5;
import GFS.Threads.UserInputThread;
import GFS.Transport.TCPReceiver;
import GFS.Transport.TCPSender;
import GFS.WireFormats.ChunkRegisterReq;
import GFS.WireFormats.WireFormatInterface;
import GFS.utils.ConfigurationManager;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChunkServer {

    private int port;
    private ServerSocket serverSocket;
    private int chunkCount = 0;

    public static final File PATH_TO_STORE_CHUNKS = new File("/tmp/");


    private static ChunkServer chunkServer = null;
    private TCPSender controllerSender;

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
            chunkServer.controllerSender = new TCPSender(controllerSocket);

            // Create Register Request
            short localPort = (short) controllerSocket.getLocalPort();
            String localIP = controllerSocket.getLocalSocketAddress().toString();
            localIP = localIP.replace("/","");
            System.out.println(localIP);
            ChunkRegisterReq registerReq = new ChunkRegisterReq(localIP,(short)configManager.getLocalServerPort());
            byte [] bRequestArray = registerReq.getByteArray();

            // send Register request
            chunkServer.controllerSender.send_and_maintain(bRequestArray);

            while (!chunkServer.isRegistered){

            }

            System.out.println("Registered Successfully");

            // Start thread to send hearbeat every 5 minutes
            HeartbeatThread5 heartbeatThread5 = new HeartbeatThread5(chunkServer);
            heartbeatThread5.start();

            // Start thread to send heartbeat every 30 seconds
            HeartbeatThread30 heartbeatThread30 = new HeartbeatThread30(chunkServer);
            heartbeatThread30.start();

            // For taking user input from console
            UserInputThread inputThread = new UserInputThread(chunkServer, heartbeatThread5, heartbeatThread30);
            inputThread.start();

            // Listening for the connections
            while (true) {
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

    /**
     * Setter for isRegistered
     */
    public synchronized void setRegistered(){
        this.isRegistered = true;
    }

    /**
     *
     * @return Chunk count
     */
    public int getChunkCount(){
        return chunkCount;
    }

    /**
     * To sen the corresponding heartbeat
     * @param wireFormat Wireformat corresponding to the heartbeat
     */
    public void sendHeartbeat(WireFormatInterface wireFormat){
        try {
            chunkServer.controllerSender.send_and_maintain(wireFormat.getByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
