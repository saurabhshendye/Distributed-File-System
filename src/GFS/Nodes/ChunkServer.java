package GFS.Nodes;

import GFS.Threads.HeartbeatThread30;
import GFS.Threads.HeartbeatThread5;
import GFS.Threads.UserInputThread;
import GFS.Transport.TCPReceiver;
import GFS.Transport.TCPSender;
import GFS.WireFormats.ChunkRegisterReq;
import GFS.WireFormats.ChunkWireFormat;
import GFS.WireFormats.WireFormatInterface;
import GFS.utils.ConfigurationManager;
import GFS.utils.Sha1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

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

            // Initializing Heartbeat threads
            // Start thread to send heartbeat every 5 minutes
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

    public synchronized boolean getisRegistered(){
        return isRegistered;
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

    public void processChunk(byte[] data){
        try {
            System.out.println("Total Length: " + data.length);

            ByteArrayInputStream bin = new ByteArrayInputStream(data);
            DataInputStream din = new DataInputStream(new BufferedInputStream(bin));

            // Read File Name
            int fileNameLength = din.readInt();
            byte [] fileNameArray = new byte[fileNameLength];
            int bytesread = din.read(fileNameArray);
//            System.out.println("fileNameArray bytes read: " + bytesread);
//            System.out.println("File Name: " + new String(fileNameArray));

            // Read Chunk Number
            int chunkNumber = din.readInt();
//            System.out.println("Chunk Number Received: " + chunkNumber);

            // Read the chunk
            int chunkLength = din.readInt();
            byte[] chunkArray = new byte[chunkLength];
            int chunkbytes = din.read(chunkArray);

            // Calculate the hash of the chunk
            Sha1 sha1 = new Sha1();
            String hash = sha1.SHA1FromBytes(chunkArray);

            writeToDisk(fileNameArray, chunkArray, hash, chunkNumber);

//            System.out.println("Number of bytes in a chunk: " + chunkbytes);


            // Defining variables that will be used
            // inside switch statement
            String firstIP;
            int addrLen, firstPort;
            byte [] addrArray;
            ChunkWireFormat chunkWireFormat;
            Socket frwdSocket;
            TCPSender tcpSender;

            // Read Rest of the addresses
            int addressCount = din.readInt();
            System.out.println("Number of addresses: "  + addressCount);
            switch (addressCount){
                case 0:
                    System.out.println("Case 0: Nothing to be done");
                    break;
                case 1:
                    System.out.println("Case 1");
                    addrLen = din.readInt();
                    addrArray = new byte[addrLen];
                    din.readFully(addrArray);
                    String address = new String(addrArray);
                    System.out.println("First Address: " + address);
                    // Getting First address
                    firstIP = address.split(":")[0];
                    firstPort = Integer.parseInt(address.split(":")[1]);
                    // Create the TCPSender object to send the chunk
                    frwdSocket = new Socket(firstIP, firstPort);
                    tcpSender = new TCPSender(frwdSocket);
                    // Create a new Chunk Wire Format and send the byte array to next address
                    chunkWireFormat = new ChunkWireFormat(new String(fileNameArray), chunkNumber,
                            chunkArray, null, 0);
                    tcpSender.send_data(chunkWireFormat.getByteArray());
                    break;
                case 2:
                    System.out.println("Case 2");
                    addrLen = din.readInt();
                    addrArray = new byte[addrLen];
                    din.readFully(addrArray);
                    String [] addresses = new String(addrArray).split("_");
                    System.out.println("First Address: " + addresses[0]);
                    // Getting First address
                    firstIP = addresses[0].split(":")[0];
                    firstPort = Integer.parseInt(addresses[0].split(":")[1]);
                    // Create the TCPSender object to send the chunk
                    frwdSocket = new Socket(firstIP, firstPort);
                    tcpSender = new TCPSender(frwdSocket);
                    // Create a new Chunk Wire Format and send the byte array to next address
                    chunkWireFormat = new ChunkWireFormat(new String(fileNameArray), chunkNumber,
                            chunkArray, addresses[1], 1);
                    tcpSender.send_data(chunkWireFormat.getByteArray());
                    break;
                default:
            }
//            System.out.println(addresses);
        } catch (IOException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }


    private void writeToDisk(byte [] fileName, byte[] chunk, String hash, int chunkNumber)
            throws IOException {

        String name = new String(fileName);
        name = "/" + name + "_" + chunkNumber;
        System.out.println("Path Name: " + PATH_TO_STORE_CHUNKS + name);
        RandomAccessFile rf = new RandomAccessFile(PATH_TO_STORE_CHUNKS + name, "rw");

        // Write has of the chunk at the start of the file
        byte [] hashArray = hash.getBytes();
        rf.writeInt(hashArray.length);
        rf.write(hashArray);

        rf.writeInt(chunk.length);
        rf.write(chunk);

        rf.close();
    }

}
