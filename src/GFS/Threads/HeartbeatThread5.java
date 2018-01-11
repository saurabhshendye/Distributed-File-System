package GFS.Threads;

import GFS.WireFormats.Heartbeat5;

import java.io.IOException;

/**
 * @author saurabhs
 * This class is to send heatbeats from chunk server
 * to the controller every 5 minutes.
 * This is a major heartbeat
 */

public class HeartbeatThread5 extends Thread {

    public void run(){
        while (!isInterrupted()){
            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                break;
            }

            Heartbeat5 h5 = new Heartbeat5();
            try {
                h5.getByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
