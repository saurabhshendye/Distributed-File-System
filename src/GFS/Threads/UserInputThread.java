package GFS.Threads;

/**
 * @auhor saurabhs
 * This class runs as a separate thread and takes
 * input from the console
 */

import GFS.Nodes.ChunkServer;
import GFS.Nodes.Client;
import GFS.Nodes.Controller;

import java.io.IOException;
import java.util.Scanner;

public class UserInputThread extends Thread {

    private Object O;
    private static final Class CLIENT_CLASS = Client.class;
    private static final Class CHUNK_SERVER_CLASS = ChunkServer.class;
    private static final Class CONTROLLER_CLASS = Controller.class;

    private Thread heartbeat5;
    private Thread heartbeat30;

    /**
     *
     * @param obj to maintain the reference to the class which created this thread
     *               This will help to invoke client class methods
     */
    public UserInputThread(Object obj){
        this.O = obj;
    }

    public UserInputThread(Object obj, Thread t5, Thread t30){
        this.O = obj;
        this.heartbeat5 = t5;
        this.heartbeat30 = t30;
    }

    @Override
    public void run() {
        Scanner in = null;
        while (!isInterrupted()) {
            in = new Scanner(System.in);
            System.out.println("Enter a Command: ");
            String command = in.nextLine();
            System.out.println("Input from User: " + command);
            try {
                input_parser(command, O);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        in.close();

    }


    private void input_parser(String command, Object obj) throws IOException {
        // Command Move to fs
        if (command.startsWith("mvfs ")) {
            if (obj.getClass().equals(CLIENT_CLASS)){
                Client client = (Client) obj;
                client.moveToFS(command);
            } else {
                System.out.println("Invalid command for this Class");
            }
        }
        // Delete command
        else if (command.startsWith("del ")){

        }
        // if the object is chunk server then it needs to interrupt the
        // heartbeat threads as well.
        else if (command.equalsIgnoreCase("EXIT")){
            System.out.println("Exit Command Caught. Exiting...");
            if (obj.getClass().equals(CHUNK_SERVER_CLASS)){
                this.heartbeat5.interrupt();
                this.heartbeat30.interrupt();
            }
            Thread.currentThread().interrupt();
            System.exit(0);
        } else if(command.equalsIgnoreCase("SHOW CHUNKSERVERS")){
            if (obj.getClass().equals(CONTROLLER_CLASS)){
                Controller controller = (Controller) obj;
                controller.printChunkServerInfo();
            }
        }
        else {
            System.out.println("Invalid Command");
        }

    }
}
