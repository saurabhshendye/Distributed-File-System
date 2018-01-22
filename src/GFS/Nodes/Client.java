package GFS.Nodes;

import GFS.Threads.UserInputThread;
import GFS.Transport.TCPReceiver;
import GFS.Transport.TCPSender;
import GFS.WireFormats.ChunkServerRequest;
import GFS.WireFormats.ChunkWireFormat;
import GFS.utils.ChunkCreator;
import GFS.utils.ConfigurationManager;
import GFS.utils.FindFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Singleton class. Need to maintain only one object of the client class
 */
public class Client {

    private static final Client client = new Client();
    // Name of the file to be copied to File System
    private static String fileName;
    // Where to look for the file
    private static String path;
    // Controller IP
    private String controllerIP;
    // Controller Port
    private int controllerPort;
    //
    private FindFile ff;
    //
    private ChunkCreator chunkCreator;

    public static Client getClientInstance() {
        return client;
    }

    public static void main(String[] args) throws IOException{
        // Checks the configuration file for this class. indicated by argument type = 0 (0 is for client class)
        ConfigurationManager configManager = new ConfigurationManager(0);
        if (configManager.isValid()){

            // Thread for taking user inputs from console
            UserInputThread inputThread = new UserInputThread(client);
            inputThread.start();

            client.controllerIP = configManager.getServerIP();
            client.controllerPort = configManager.getServerPort();

        } else {
            System.out.println("Incorrect Configuration.");
            System.exit(1);
        }
    }

    /**
     * Checks if the file is present and if it is moves the file to DFS
     * @param command Complete command with file name to be moved to DFS
     */
    public void moveToFS(String command){
        String [] parts = command.split(" ");
        // issue #1 unable to process files which have spaces in between
        String fileName = parts[1];
        // find file in the given location
        ff = new FindFile();
        ff.fileLookup(fileName, ff.getPath());
        if (ff.isPresent()){
            System.out.println("File Present");

            try {
                chunkCreator = new ChunkCreator(ff.getPath());
                // Collect details about file
                chunkCreator.gatherDetails();

                Socket clientSocket = new Socket(controllerIP, controllerPort);
                TCPSender sender = new TCPSender(clientSocket);
                TCPReceiver receiver = new TCPReceiver(clientSocket, this);
                receiver.start();
                // Get Chunk Count and create Chunk Server Request
                int count = chunkCreator.getChunkCount();
                System.out.println("Chunk Count is: " + count);

                // Create the request amd get the byte array
                ChunkServerRequest request = null;
                byte[] requestArray = null;

                // Send the request for number of times
                // equal to chunk count
                for (int i = 0; i<count; i++){
                    request = new ChunkServerRequest(fileName, i);
                    requestArray = request.getByteArray();
                    sender.send_and_maintain(requestArray);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File not found");
        }
    }

    public void processAddresses(byte[] data){
        String addressString = new String(data);
        String [] addresses = addressString.split("_");
        int chunkNumber = Integer.parseInt(addresses[0]);
        System.out.println(addresses[1]);
//        Socket chunkSocket = new Socket()
        try {
            byte [] chunkByteArray = chunkCreator.getNextChunk();
            ChunkWireFormat chunkWireFormat = new ChunkWireFormat(fileName, chunkNumber, chunkByteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
