package GFS.Nodes;

import GFS.Threads.UserInputThread;
import GFS.Transport.TCPReceiver;
import GFS.Transport.TCPSender;
import GFS.WireFormats.ChunkServerRequest;
import GFS.utils.ChunkCreator;
import GFS.utils.ConfigurationManager;
import GFS.utils.findFile;

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

    public void moveToFS(String command){
        String [] parts = command.split(" ");
        // issue #1 unable to process files which have spaces in between
        String fileName = parts[1];
        // find file in the given location
        findFile ff = new findFile();
        ff.fileLookup(fileName, ff.getPath());
        if (ff.isPresent()){
            System.out.println("File Present");
            ChunkCreator chunkCreator = new ChunkCreator(ff.getPath());
            try {
                // Get Chunk Count and create Chunk Server Request
                int count = chunkCreator.getChunkCount();
                System.out.println("Chunk Count is: " + count);
                ChunkServerRequest request = new ChunkServerRequest(count, fileName);
                byte[] requestArray = request.getByteArray();

                // Send the request
                Socket clientSocket = new Socket(controllerIP, controllerPort);
                TCPSender sender = new TCPSender(clientSocket);
                TCPReceiver receiver = new TCPReceiver(clientSocket, this);
                receiver.start();
                sender.send_and_maintain(requestArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
