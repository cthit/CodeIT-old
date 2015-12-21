package it.chalmers.digit.codeit.server.game;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by tejp on 20/12/15.
 */
public class Team {

    private String teamName;
    private int score;

    private int expectedSizeOfChunks;
    private byte[] chunks;

    public Team(String teamName) {
        this.teamName = teamName;
    }

    public void setExpectedSizeOfChunks(int expectedSizeOfChunks) {
        this.expectedSizeOfChunks = expectedSizeOfChunks;
    }

    public void addChunk(byte[] chunk) {
        chunks = ArrayUtils.addAll(chunks, chunk);
    }

    public byte[] getChunks() {
        return chunks;
    }

    public void resetChunks() {
        chunks = null;
    }

    public int getExpectedSizeOfChunks() {
        return expectedSizeOfChunks;
    }

    public String getTeamName() {
        return teamName;
    }
}
