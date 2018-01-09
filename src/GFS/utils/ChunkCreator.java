package GFS.utils;


import java.io.*;
import java.util.HashMap;

public class ChunkCreator {
    private File file;
    private static final int CHUNK_SIZE = 65536;
    private int chunkCount;

    public ChunkCreator(File file){
        this.file = file;
    }

    /**
     * Getter for chunk count
     * @return
     * @throws IOException
     */
    public int getChunkCount() throws IOException{
        RandomAccessFile rf = new RandomAccessFile(file, "r");
        int count = 0;
        if ((int) rf.length()%CHUNK_SIZE == 0){
            count = (int) rf.length()/CHUNK_SIZE;
        } else {
            count = ((int) rf.length()/CHUNK_SIZE) + 1;
        }
        rf.close();

        return count;
    }

    /**
     *
     * @throws IOException
     */
    public void createChunks() throws IOException {
        RandomAccessFile rf = new RandomAccessFile(file, "r");
        byte [] b = null;

        HashMap chunkMap = new HashMap();
        int totalChunks = (int) (rf.length()/CHUNK_SIZE);
        int lastChunkLength = (int)rf.length() % CHUNK_SIZE;
        int byteCount = 0;
        for (int i=0, j=1; i<(int) rf.length(); i+=CHUNK_SIZE) {
            if (j == totalChunks && lastChunkLength!=0){
                byteCount = lastChunkLength;
                b = new byte[lastChunkLength];
            } else {
                byteCount = CHUNK_SIZE;
                b = new byte[CHUNK_SIZE];
            }
            rf.read(b,i,byteCount);
        }

    }
}
