package GFS.WireFormats;

import GFS.Nodes.ChunkServer;
import GFS.Nodes.Client;
import GFS.Nodes.Controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * This class helps to reconstruct the wireformat messages
 * from byte arrays
 */
public class WireFormatWidget {
    private short type;
    private byte [] identifier;
    private Object object;


    /**
     * @param bytes bytes received on TCPReceiver
     * @throws IOException
     */
    public WireFormatWidget(byte [] bytes, Object object) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(bytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.type = din.readShort();
        int Len = din.readInt();
        byte[] data = new byte[Len];

        din.readFully(data);
        this.identifier = data;

        din.close();
        baInputStream.close();

        this.object = object;
    }

    /**
     * Since every wireformat has an integer identifier
     * this will return the corresponding value
     * @return type of wireformat
     */
    public short getType() {
        return type;
    }

    /**
     * @return message data in the form of byte []
     */
    public byte [] getIdentifier(){
        return identifier;
    }

    /**
     * TODO : WRITE DESCRIPTION
     */
    public void getChunkAddress(){
        System.out.println("Request Received");
    }

}
