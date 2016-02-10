package it.chalmers.digit.codeit.server.game;


import org.apache.commons.lang3.ArrayUtils;

public class ChunkBuffer {

    private int expectedSizeOfChunks;
    private byte[] chunks;

    public ChunkBuffer(int expectedSizeOfChunks) {
        this.expectedSizeOfChunks = expectedSizeOfChunks;
    }

    public void addChunk(byte[] chunk) {
        chunks = ArrayUtils.addAll(chunks, chunk);
    }

    public byte[] getChunks() {
        return chunks;
    }

    public int getExpectedSizeOfChunks() {
        return expectedSizeOfChunks;
    }

}
