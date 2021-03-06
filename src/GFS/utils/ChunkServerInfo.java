package GFS.utils;

import GFS.Nodes.ChunkServer;
import GFS.Transport.TCPReceiver;
import GFS.Transport.TCPSender;

/**
 * @author saurabhs
 * This class holds information about the chunk server
 *
 */
public class ChunkServerInfo {

    public TCPSender sender;
    public TCPReceiver receiver;
    public String IP;
    public short serverPort;
    public int chunkCount = 0;

    public ChunkServerInfo(TCPReceiver receiver, TCPSender sender, String IP,
                           short serverPort){

        this.IP = IP;
        this.serverPort = serverPort;
        this.receiver = receiver;
        this.sender = sender;
    }

    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }

    public void incrementChunkCount(int num){
        this.chunkCount += num;
    }

}
