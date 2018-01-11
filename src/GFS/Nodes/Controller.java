package GFS.Nodes;

/**
 * @author saurabhs
 * This class is the master or controller node of
 * the file system
 */

import GFS.Threads.UserInputThread;
import GFS.Transport.TCPReceiver;
import GFS.Transport.TCPSender;
import GFS.WireFormats.ChunkServerRegAck;
import GFS.utils.ChunkServerInfo;
import GFS.utils.ConfigurationManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Controller {

    private int port;
    private ServerSocket serverSocket;

    // This map is just used for staging the TCPReveivers created
    // at the time when the request is received on the server port
    // If the request is from the client then the receiver will deleted
    // as soon as the connection is broken by the controller
    // If the request is from the chunk server then details about the
    // chunk server will be stored in another data structure
    public HashMap<String, TCPReceiver> tempReceiverMap = new HashMap<>();

    //
    private HashMap<String, ChunkServerInfo> chunkServerInfohMap = new HashMap<>();

    private static Controller controllerServer = null;

    public static boolean isInterrupted = false;

    private Controller(int port) throws IOException{
        serverSocket = new ServerSocket(port,10);
    }

    public static void main(String[] args) throws IOException{
        ConfigurationManager configManager = new ConfigurationManager(2);
        if (configManager.isValid()){
            // Creating a local Server
            try {
                controllerServer = new Controller(configManager.getLocalServerPort());
            } catch (IOException e) {
                e.printStackTrace();
            }

            UserInputThread inputThread = new UserInputThread(controllerServer);
            inputThread.start();

            // Listening for the connections
            while (true)
            {
                Socket clientSocket = controllerServer.serverSocket.accept();
                TCPReceiver receiver = new TCPReceiver(clientSocket, controllerServer);
                receiver.start();
                controllerServer.makeTempReceiverEntry(receiver);
            }
        } else {
            System.out.println("Incorrect Configuration.");
            System.exit(1);
        }
    }

    /**
     * This method makes a temporary entry in the map for TCPReceiver which gets
     * created with every request
     * @param receiver
     */
    private void makeTempReceiverEntry(TCPReceiver receiver){
        String IpAndPort = receiver.getSocket().getRemoteSocketAddress().toString().replace("/","");
        tempReceiverMap.put(IpAndPort, receiver);
    }

    /**
     * This method gets a chunk server registered in the controller
     * @param bArray Register request data in the form of byte array
     */
    public synchronized void chunkServerRegister(byte [] bArray){
        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(bArray);
            DataInputStream din = new DataInputStream(new BufferedInputStream(bin));

            short serverPort = din.readShort();

            byte [] ipArray = new byte[bArray.length-2];
            din.readFully(ipArray);
            String Ip = new String(ipArray);

            if (tempReceiverMap.containsKey(Ip)){
                // finding the temporary reference, creating info object
                TCPReceiver receiver = tempReceiverMap.get(Ip);
                TCPSender sender = new TCPSender(receiver.getSocket());
                ChunkServerInfo chunkServerInfo = new ChunkServerInfo(receiver,sender,Ip,serverPort);
                chunkServerInfohMap.put(Ip,chunkServerInfo);

                // removing the temporary reference
                tempReceiverMap.remove(Ip);

                // Sending the +ve reply back to chunk server
                System.out.println("Successfully Registered");
                ChunkServerRegAck regAck = new ChunkServerRegAck("OK");
                sender.send_and_maintain(regAck.getByteArray());

            } else {
                // Sending the -ve reply back to Chunk Server
                // issue #2 send it to whom?
                System.out.println("Key not found.");
            }

        }catch (IOException e){

        }
    }

    public void removeChunkServer(String key){
        if (chunkServerInfohMap.containsKey(key)){
            chunkServerInfohMap.remove(key);
            System.out.println("Chunk Server: " + key + " is disconnected and removed" +
                    " from the registry");
        }
    }
}
