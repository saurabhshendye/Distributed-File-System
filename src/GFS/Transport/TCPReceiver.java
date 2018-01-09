package GFS.Transport;

import GFS.WireFormats.WireFormatWidget;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPReceiver extends Thread {

    private Socket Serving;
    private DataInputStream din;

    /**
     *
     * @param S socket on which the receiver will be listening
     * @throws IOException
     */
    public TCPReceiver(Socket S) throws IOException {
        this.Serving = S;
        din = new DataInputStream(Serving.getInputStream());
    }

    /**
     * This will run till connection for this particular socket breaks
     */
    public void run() {
        while (Serving != null) {
            try {
                int D_len = din.readInt();
                byte[] data = new byte[D_len];
                din.readFully(data);
                WireFormatWidget WireFormat = new WireFormatWidget(data);

                int type = WireFormat.getType();
                switch (type)
                {
                    case 0:
                        System.out.println("Received Register request from chunk server");
                        WireFormat.getChunkServerRegistered();
                        break;
                    case 1:
                        System.out.println("Received request to get chunk server addresses");
                        WireFormat.getChunkAddress();
                        break;
                    default: System.out.println("Unknown Message");
                        break;
                }
            }
            catch (IOException e) {
                System.out.println("Error Message: " +e.getMessage());
                break;
            }
        }
    }
}
