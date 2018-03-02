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
import GFS.WireFormats.ServerAddresses;
import GFS.utils.ChunkServerInfo;
import GFS.utils.ConfigurationManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

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

    // Contains information about the chunk servers such as Ip
    // connection port and server port Chunk count
    private HashMap<String, ChunkServerInfo> chunkServerInfohMap = new HashMap<>();

    // Temporary hashmap for tracking free memory in chunk servers
    // this is a unsorted map
    private HashMap <String, Long> freeMemoryMap = new HashMap<>();

    // Sorted Free memory map
    private Map<String,Long> sortedMemoryMap;

    // Controller Instance
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
            e.printStackTrace();
        }
    }

    public void removeChunkServer(String key){
        if (chunkServerInfohMap.containsKey(key)){
            chunkServerInfohMap.remove(key);
            System.out.println("Chunk Server: " + key + " is disconnected and removed" +
                    " from the registry");
        }

        if (freeMemoryMap.containsKey(key)){
            freeMemoryMap.remove(key);
        }
    }

    /**
     * To process Major Heartbeat from the Chunk Server
     * @param data Heartbeat data in the form of byte array
     * @param socket Socket from which the heartbeat was received.
     */
    public synchronized void processMajorHeartbeat(byte [] data, Socket socket){
        try {
            String IpandPort = socket.getRemoteSocketAddress().toString().replace("/","");
            ByteArrayInputStream bin = new ByteArrayInputStream(data);
            DataInputStream din = new DataInputStream(new BufferedInputStream(bin));

            int chunkcount = din.readInt();
            Long freeMemory = din.readLong();

            if (chunkServerInfohMap.containsKey(IpandPort)){
                System.out.println("Key found to update data Major Heartbeat");
                ChunkServerInfo chunkServerInfo = chunkServerInfohMap.get(IpandPort);
                chunkServerInfo.setChunkCount(chunkcount);
                freeMemoryMap.put(IpandPort, freeMemory);
            }
            din.close();
            bin.close();
            sortedMemoryMap = sortByMemValues(freeMemoryMap);

//            printChunkServerInfo();

        }catch (IOException e){

        }
    }

    /**
     * To process minor heartbeat
     * @param data Heartbeat data byte array
     * @param socket socket from which the heartbeat was received
     *               This is used to get key for hashmaps
     */
    public synchronized void processMinorHeartbeat(byte [] data, Socket socket){
        try {
            String IpandPort = socket.getRemoteSocketAddress().toString().replace("/","");
            ByteArrayInputStream bin = new ByteArrayInputStream(data);
            DataInputStream din = new DataInputStream(new BufferedInputStream(bin));

            int chunkcount = din.readInt();
            Long freeMemory = din.readLong();

            freeMemoryMap.put(IpandPort, freeMemory);

            if (chunkServerInfohMap.containsKey(IpandPort)){
                System.out.println("Key found to update data Minor Heartbeat");
                ChunkServerInfo chunkServerInfo = chunkServerInfohMap.get(IpandPort);
                chunkServerInfo.setChunkCount(chunkcount);

            }
//            printChunkServerInfo();
            sortedMemoryMap = sortByMemValues(freeMemoryMap);

            din.close();
            bin.close();

        } catch (IOException e){

        }
    }

    /**
     * To print out the chunk server information
     */
    public void printChunkServerInfo(){
        for (Long freeMemory: sortedMemoryMap.values()){
            System.out.println(freeMemory);
        }
    }

    /**
     * Sends back three ChunkServer addresses for a
     * given chunk
     */
    public void serveAllotmentRequest(byte[] data, Socket socket){

        int chunkNumber = 0;
        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(data);
            DataInputStream din = new DataInputStream(new BufferedInputStream(bin));

            chunkNumber = din.readInt();
            din.close();
        }catch (IOException e){

        }

        String IpandPort = socket.getRemoteSocketAddress().toString().replace("/","");
        int i = 0;
        StringBuilder builder = new StringBuilder();
        builder.append(chunkNumber);

        // Get top 3 servers with highest free memory
        // and build the address string
        for (Map.Entry<String, Long> entry : sortedMemoryMap.entrySet()) {
            // So that only 3 addresses will be provided
            if (i>2){
                break;
            }
            i++;
            builder.append("_");
            ChunkServerInfo chunkServerInfo = chunkServerInfohMap.get(entry.getKey());
            String IP = chunkServerInfo.IP.split(":")[0];
            builder.append(IP);
            builder.append(":");
            builder.append(chunkServerInfo.serverPort);
            System.out.println("Server Port" + chunkServerInfo.serverPort);
//            builder.append(entry.getKey());
        }

        // Send the address String to the Client
        ServerAddresses serverAddresses = new ServerAddresses(builder.toString());
        try {
            TCPSender tcpSender = new TCPSender(socket);
            tcpSender.send_and_maintain(serverAddresses.getByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sort the map by values
     * @param unsortedMap unsorted memory
     * @return Sorted Map
     */
    private Map<String, Long> sortByMemValues(HashMap<String, Long> unsortedMap){
        // 1. Convert Map to List of Map
        List<Map.Entry<String, Long>> list =
                new LinkedList<Map.Entry<String, Long>>(unsortedMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Long> sortedMap = new LinkedHashMap<String, Long>();
        for (Map.Entry<String, Long> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}

