package GFS.Nodes;

import GFS.Threads.UserInputThread;
import GFS.Transport.TCPReceiver;
import GFS.Transport.TCPSender;
import GFS.utils.ConfigurationManager;
import GFS.utils.findFile;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Singleton class. Need to maintain only one object of the client class
 */
public class Client {

    private static final Client client = new Client();
    // Name of the file to be copied to File System
    private static String fileName;
    private static String path;

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

        } else {
            System.out.println("Incorrect Configuration.");
            System.exit(1);
        }
    }

    public void moveToFS(String command){
        String [] parts = command.split(" ");
        // issue #1 unable to process files which have spaces in between
        String fileName = parts[1];
        // This class is used to find file in the given location
        findFile ff = new findFile();
        ff.fileLookup(fileName, ff.getPath());
        if (ff.isPresent()){
            System.out.println("File Present");

        }

    }
}
