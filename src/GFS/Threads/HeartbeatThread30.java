package GFS.Threads;

import GFS.WireFormats.Heartbeat30;

import java.io.IOException;

/**
 * @author saurabhs
 * This class is to send heatbeats from chunk server
 * to the controller every 30 seconds.
 * This is a minor heartbeat
 */

public class HeartbeatThread30 extends Thread {
    private float minutecount = 0;

    public void run(){
        while (!isInterrupted()){
            try {
                Thread.sleep(30000);
                minutecount += 0.5;
                if (minutecount == 5.0f){
                    continue;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Heartbeat30 msg = new Heartbeat30();
            try {
                msg.getByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }




    }
}
