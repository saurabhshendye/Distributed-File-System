package GFS.WireFormats;

/**
 * @author saurabhs
 * This class is to create a major heartbeat
 * message to  be sent to the controller
 */

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Heartbeat5 implements WireFormatInterface{

    private short type = 5;
    // Number of chunks
    private int chunkCount;
    private long freeMemory;

    public Heartbeat5 (int ChunkCount, long freeMemory ) {
        this.chunkCount = ChunkCount;
        this.freeMemory = freeMemory;
    }

    public Heartbeat5(){

    }

    /**
     *
     * @return byte array to be sent to the controller
     * @throws IOException
     */

    public byte [] getByteArray() throws IOException {

        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        int Len = 4;
        dout.writeInt(type);
        dout.writeInt(Len);
        dout.flush();
        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
