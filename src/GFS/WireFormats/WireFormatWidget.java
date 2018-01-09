package GFS.WireFormats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * This class helps to reconstruct the wireformat messages
 * from byte arrays
 */
public class WireFormatWidget {
    private int type;
    private byte [] identifier;

    /**
     * @param bytes bytes received on TCPReceiver
     * @throws IOException
     */
    public WireFormatWidget(byte [] bytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(bytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.type = din.readInt();
        int Len = din.readInt();
        byte[] data = new byte[Len];

        din.readFully(data);
        this.identifier = data;

        din.close();
        baInputStream.close();
    }

    /**
     * Since every wireformat has an integer identifier
     * this will return the corresponding value
     * @return type of wireformat
     */
    public int getType() {
        return type;
    }

    public void getChunkAddress(){

    }
}
