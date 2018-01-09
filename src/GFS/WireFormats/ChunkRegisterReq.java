package GFS.WireFormats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This WireFormat is for the chunk server to register
 * to the controller
 */
public class ChunkRegisterReq {

    // Port on which connection to controller is being done
    private int connectionPort;
    // IP address of the chunk server
    private String serverIP;
    // Listening port on the chunk server
    private int serverPort;
    // Request type
    private int type = 0;


    public ChunkRegisterReq (String serverIP, int serverPort, int connectionPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.connectionPort = connectionPort;
    }

    /**
     * Converts a request (count + file name) into byte array
     * @return byte array to be sent to the controller
     * @throws IOException
     */

    public byte [] getByteArray() throws IOException {

        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        byte[] IP_array = this.serverIP.getBytes();
        int IP_Len = IP_array.length;
        int Len = IP_Len + 8;

        dout.writeInt(type);
        dout.writeInt(Len);
        dout.writeInt(this.serverPort);
        dout.writeInt(this.connectionPort);
        dout.write(IP_array);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
