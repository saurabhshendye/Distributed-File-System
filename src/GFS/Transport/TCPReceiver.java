package GFS.Transport;

import GFS.Nodes.ChunkServer;
import GFS.Nodes.Client;
import GFS.Nodes.Controller;
import GFS.WireFormats.WireFormatWidget;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPReceiver extends Thread {

    private Socket socket;
    private DataInputStream din;
    private Object object;
    private String IpAddress;

    private static final Class CLIENT_CLASS = Client.class;
    private static final Class CHUNK_SERVER_CLASS = ChunkServer.class;
    private static final Class CONTROLLER_CLASS = Controller.class;

    /**
     *
     * @param S socket on which the receiver will be listening
     * @throws IOException
     */
    public TCPReceiver(Socket S, Object object) throws IOException {
        this.socket = S;
        this.din = new DataInputStream(this.socket.getInputStream());
        this.object = object;
        this.IpAddress = S.getRemoteSocketAddress().toString().replace("/", ":");
    }

    /**
     * This will run till connection for this particular socket breaks
     */
    public void run() {
        while (this.socket != null) {
            try {
                int dataLength = din.readInt();
                byte[] data = new byte[dataLength];
                din.readFully(data);
                WireFormatWidget wireFormat = new WireFormatWidget(data, object);

                short type = wireFormat.getType();
                System.out.println("Type Received: " + type);
                switch (type)
                {
                    case 0:
                        if (object.getClass().equals(CONTROLLER_CLASS)){
                            System.out.println("Received Register request from chunk server");
                            Controller controller = (Controller) object;
                            controller.chunkServerRegister(wireFormat.getIdentifier());
                        }
                        break;
                    case 1:
                        if (object.getClass().equals(CHUNK_SERVER_CLASS)){
                            System.out.println("Received Reg acknowledgement");
                            ChunkServer chunkServer = (ChunkServer) object;
                            chunkServer.setRegistered();
                        }

                        break;
                    case 5:
                        System.out.println("Major Heartbeat received");
                        break;
                    case 30:
                        System.out.println("Minor Heartbeat Received");
                        break;
                    default: System.out.println("Unknown Message");
                        break;
                }
            }
            catch (IOException e) {
                if (object.getClass().equals(CONTROLLER_CLASS)){
                    System.out.println("Error Message: " +e.getMessage());
                    Controller controller = (Controller) object;
                    controller.removeChunkServer(IpAddress);
                }
                break;
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
