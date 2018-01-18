package GFS.Transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {

    private Socket socket;
    private DataOutputStream dout;

    /**
     * TCPSender Constructor
     * @param S socket being used to send data
     * @throws IOException
     */
    public TCPSender(Socket S) throws IOException {
        this.socket = S;
        dout = new DataOutputStream(socket.getOutputStream());
    }


    /**
     * This method will close the channel after sending the data
     * @param data_to_send Data to be sent
     * @throws IOException
     */
    public synchronized void send_data(byte[] data_to_send) throws IOException {
        int D_len = data_to_send.length;
        dout.writeInt(D_len);
        dout.write(data_to_send,0,D_len);
        dout.flush();
        dout.close();
    }

    /**
     * This method will not break the channel after sending the data
     * @param data_to_send Data to be sent
     * @throws IOException
     */
    public synchronized void send_and_maintain(byte[] data_to_send) throws IOException {
        int D_len = data_to_send.length;
        dout.writeInt(D_len);
        dout.write(data_to_send, 0, D_len);
        dout.flush();
        System.out.println("Data Sent");
    }
}
