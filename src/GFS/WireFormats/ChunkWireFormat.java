package GFS.WireFormats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChunkWireFormat implements WireFormatInterface {

    private final short type = 4;

    private byte[] chunkArray;

    private int chunkNumber;

    private String fileName;

    public ChunkWireFormat(String fileName, int chunkNumber, byte[] chunkArray){
        this.fileName = fileName;
        this.chunkNumber = chunkNumber;
        this.chunkArray = chunkArray;
    }

    @Override
    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        byte [] fileNameArray = fileName.getBytes();
        int fileNamelength = fileNameArray.length;
        int chunkArrayLen = chunkArray.length;

        dout.writeShort(type);
        dout.writeInt(fileNamelength);
        dout.writeInt(chunkNumber);
        dout.write(fileNameArray);
        dout.write(chunkArrayLen);
        dout.write(chunkArray);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;

    }
}
