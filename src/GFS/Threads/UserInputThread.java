package GFS.Threads;

import GFS.Nodes.Client;

import java.io.IOException;
import java.util.Scanner;

public class UserInputThread extends Thread {

    private Client client;

    /**
     *
     * @param client to maintain the reference to the Client class
     *               This will help to invoke client class methods
     */
    public UserInputThread(Client client){
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {
            Scanner in = new Scanner(System.in);
            System.out.println("Enter a Command: ");
            String command = in.nextLine();
            System.out.println("Input from User: " + command);
            try {
                input_parser(command, client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void input_parser(String command, Client client) throws IOException {
        // Command Move to fs
        if (command.startsWith("mvfs ")) {
            client.moveToFS(command);
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
