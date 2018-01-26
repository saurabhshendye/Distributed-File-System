package GFS.utils;


import java.io.*;
import java.util.HashMap;

public class ChunkCreator {
    private File file;
    private static final int CHUNK_SIZE = 65536;
    private RandomAccessFile rf;

    private int totalChunks;
    private int lastChunkLength;

    // To keep count
    private int i=0, j=1;

    public ChunkCreator(File file) throws IOException {
        this.file = file;
        this.rf =  new RandomAccessFile(file, "r");
    }

    /**
     * Getter for chunk count
     * @return
     * @throws IOException
     */
    public int getChunkCount() throws IOException{
        int count = 0;
        System.out.println("File length: " + rf.length());
        if ((int) rf.length()%CHUNK_SIZE == 0){
            count = (int) rf.length()/CHUNK_SIZE;
        } else {
            count = ((int) rf.length()/CHUNK_SIZE) + 1;
        }
        return count;
    }

//    /**
//     *
//     * @throws IOException
//     */
//    public void createChunks() throws IOException {
//        byte [] b = null;
//
//        int byteCount = 0;
//        for (int i=0, j=1; i<(int) rf.length(); i+=CHUNK_SIZE) {
//            if (j == totalChunks && lastChunkLength!=0){
//                byteCount = lastChunkLength;
//                b = new byte[lastChunkLength];
//            } else {
//                byteCount = CHUNK_SIZE;
//                b = new byte[CHUNK_SIZE];
//            }
//            rf.read(b,i,byteCount);
//        }
//    }


    /**
     * Returns the next chunk
     * @return chunk byte array
     * @throws IOException
     */
    public byte[] getNextChunk() throws IOException{
        byte [] b = null;
        int byteCount = 0;
        if (j == totalChunks && lastChunkLength!=0){
            byteCount = lastChunkLength;
            b = new byte[lastChunkLength];
            j++;
        } else {
            byteCount = CHUNK_SIZE;
            b = new byte[CHUNK_SIZE];
            j++;
        }
        rf.readFully(b);
        i+=byteCount;
        return b;
    }

    public void gatherDetails() throws IOException{
        totalChunks = getChunkCount();
        lastChunkLength = (int)rf.length() % CHUNK_SIZE;
    }
}
