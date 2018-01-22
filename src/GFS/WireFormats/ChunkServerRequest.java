package GFS.WireFormats;

import java.io.*;

/**
 * Wire Format for sending chunk count and file  to the Controller.
 * In response controller will give chuck server address for each chunk
 */

public class ChunkServerRequest implements WireFormatInterface {
    private short type = 2;
    // Chunk Number
    private int chunkNumber;
//    // Name of the file which we want to store on this file system
    private String fileName;

    public ChunkServerRequest(String fileName, int chunkNumber) {
        this.fileName = fileName;
        this.chunkNumber = chunkNumber;
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
        dout.writeShort(type);
        dout.writeInt(Len);
        dout.writeInt(chunkNumber);
        dout.write(b);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
