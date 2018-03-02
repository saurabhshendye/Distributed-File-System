package GFS.Threads;

import GFS.Nodes.ChunkServer;
import GFS.WireFormats.Heartbeat5;


import static GFS.Nodes.ChunkServer.MAX_REQUIRED_SPACE;
import static GFS.Nodes.ChunkServer.PATH_TO_STORE_CHUNKS;

/**
 * @author saurabhs
 * This class is to send heatbeats from chunk server
 * to the controller every 5 minutes.
 * This is a major heartbeat
 */

public class HeartbeatThread5 extends Thread {

    private ChunkServer chunkServer;

    public HeartbeatThread5(ChunkServer chunkServer){
        this.chunkServer = chunkServer;
    }

    public void run(){

        while (!isInterrupted()){
            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                break;
            }

            if (chunkServer.getisRegistered()){
                Heartbeat5 h5 = new Heartbeat5(chunkServer.getChunkCount(), chunkServer.getSpcaeRemaining());
                chunkServer.sendHeartbeat(h5);
            }
        }
    }


}
