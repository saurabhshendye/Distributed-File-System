package GFS.Threads;

import GFS.Nodes.ChunkServer;
import GFS.WireFormats.Heartbeat30;

import static GFS.Nodes.ChunkServer.PATH_TO_STORE_CHUNKS;

/**
 * @author saurabhs
 * This class is to send heatbeats from chunk server
 * to the controller every 30 seconds.
 * This is a minor heartbeat
 */

public class HeartbeatThread30 extends Thread {
    private float minutecount = 0;
    private ChunkServer chunkServer;

    public HeartbeatThread30(ChunkServer chunkServer){
        this.chunkServer = chunkServer;
    }

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

            if (chunkServer.getisRegistered()){
                Heartbeat30 msg = new Heartbeat30(chunkServer.getChunkCount(), PATH_TO_STORE_CHUNKS.getUsableSpace());
                chunkServer.sendHeartbeat(msg);
            }
        }
    }
}
