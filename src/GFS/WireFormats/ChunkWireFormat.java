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

    private String addresses;

    private int addressCount;

    public ChunkWireFormat(String fileName, int chunkNumber, byte[] chunkArray,
                           String addresses, int addressCount){
        this.fileName = fileName;
        this.chunkNumber = chunkNumber;
        this.chunkArray = chunkArray;
        this.addresses = addresses;
        this.addressCount = addressCount;
    }

    @Override
    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        byte [] fileNameArray = fileName.getBytes();
        int fileNamelength = fileNameArray.length;
        int chunkArrayLen = chunkArray.length;
        System.out.println("File Name Length: " +fileNamelength);

//        String addressString = addresses[2] + "_" + addresses[3];
        int addressesLen = 0;
        byte[] addressBytes;
        if (addresses == null){
            String nullString = "NULL";
            addressBytes = nullString.getBytes();
            addressesLen = addressBytes.length;
        } else {
            addressBytes = addresses.getBytes();
            addressesLen = addressBytes.length;
        }

        System.out.println("addressesLen: " + addressesLen);

        int totalLength = fileNamelength + 4 + 4 + chunkArrayLen + 4 + addressesLen + 4 + 4;

        dout.writeShort(type);
        dout.writeInt(totalLength);
        dout.writeInt(fileNamelength);
        dout.write(fileNameArray);
        dout.writeInt(chunkNumber);
        dout.writeInt(chunkArrayLen);
        dout.write(chunkArray);
        dout.writeInt(addressCount);
        dout.writeInt(addressesLen);
        dout.write(addressBytes);
        dout.flush();

        byte[] marshaled = baopstream.toByteArray();

        baopstream.close();
        dout.close();

        return marshaled;
    }
}
