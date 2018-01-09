package GFS.Threads;

import GFS.Nodes.ChunkServer;
import GFS.Nodes.Client;
import GFS.Nodes.Controller;

import java.io.IOException;
import java.util.Scanner;

public class UserInputThread extends Thread {

    private Object O;
    private static final Class clientClass = Client.class;
    private static final Class chunkClass = ChunkServer.class;
    private static final Class controllerClass = Controller.class;

    /**
     *
     * @param obj to maintain the reference to the class which created this thread
     *               This will help to invoke client class methods
     */
    public UserInputThread(Object obj){
        this.O = obj;
    }

    @Override
    public void run() {
        while (true) {
            Scanner in = new Scanner(System.in);
            System.out.println("Enter a Command: ");
            String command = in.nextLine();
            System.out.println("Input from User: " + command);
            try {
                input_parser(command, O);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void input_parser(String command, Object obj) throws IOException {
        // Command Move to fs
        if (command.startsWith("mvfs ")) {
            if (obj.getClass().equals(clientClass)){
                Client client = (Client) obj;
                client.moveToFS(command);
            } else {
                System.out.println("Invalid command for this Class");
            }
        }
        // Delete command
        else if (command.startsWith("del ")){

        } else if (command.equalsIgnoreCase("EXIT")){
            System.out.println("Exit Command Caught. Exiting...");
            System.exit(0);
        }
        else {
            System.out.println("Invalid Command");
        }

    }
}
