package GFS.WireFormats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerAddresses implements WireFormatInterface {

    private final short type = 3;
    private String addressString;

    public ServerAddresses(String address){
        this.addressString = address;
    }


    @Override
    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream baopstream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baopstream));

        byte[] b = addressString.getBytes();

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
