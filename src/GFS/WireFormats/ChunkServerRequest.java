package GFS.WireFormats;

import java.io.*;

/**
 * Wire Format for sending chunk count and file  to the Controller.
 * In response controller will give chuck server address for each chunk
 */

public class ChunkServerRequest {
    private int type = 1;               //File found indicator
    private int chunkCount;
    private String fileName;

    public ChunkServerRequest(int ChunkCount, String fileName) {
        this.chunkCount = ChunkCount;
        this.fileName = fileName;
    }

    /**
     * Converts a request (count + file name) into byte array
     * @return byte array to be sent to the controller
     * @throws IOException
     */

    public byte [] getByteArray() throws IOException {

        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));
        byte[] b = fileName.getBytes();

        int Len = b.length + 4;
        dout.writeInt(type);
        dout.writeInt(Len);
        dout.writeInt(chunkCount);
        dout.write(b);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
