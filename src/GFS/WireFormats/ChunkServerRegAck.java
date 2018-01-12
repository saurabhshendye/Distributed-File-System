package GFS.WireFormats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChunkServerRegAck implements WireFormatInterface {

    private short type = 1;
    private String message;

    public ChunkServerRegAck(String message) {
       this.message = message;
    }

    /**
     * Converts a request (count + file name) into byte array
     * @return byte array to be sent to the controller
     * @throws IOException
     */

    public byte [] getByteArray() throws IOException {

        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));
        byte[] b = message.getBytes();

        int Len = b.length;
        dout.writeShort(type);
        dout.writeInt(Len);
        dout.write(b);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
