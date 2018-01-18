package GFS.WireFormats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Heartbeat30 implements WireFormatInterface {

    private short type = 30;
    // Number of chunks
    private int chunkCount;
    private long freeMemory;

    public Heartbeat30 (int ChunkCount, long freeMemory) {
        this.chunkCount = ChunkCount;
        this.freeMemory = freeMemory;
    }

    /**
     *
     * @return byte array to be sent to the controller
     * @throws IOException
     */

    public byte [] getByteArray() throws IOException {

        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        int Len = 12;
        dout.writeShort(type);
        dout.writeInt(Len);
        dout.writeInt(chunkCount);
        dout.writeLong(freeMemory);
        dout.flush();
        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
