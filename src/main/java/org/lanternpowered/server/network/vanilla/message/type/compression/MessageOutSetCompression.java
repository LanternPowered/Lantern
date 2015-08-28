package org.lanternpowered.server.network.vanilla.message.type.compression;

import org.lanternpowered.server.network.message.Message;

/**
 * The set compression message, this can be send in the
 * LOGIN and PLAY protocol state.
 */
public final class MessageOutSetCompression implements Message {

    private final int threshold;

    /**
     * Creates a new compression message.
     * 
     * @param threshold the threshold
     */
    public MessageOutSetCompression(int threshold) {
        this.threshold = threshold;
    }

    /**
     * Gets the threshold, it is the max size of a packet before its compressed.
     * Use -1 to disable compression.
     * 
     * @return the threshold
     */
    public int getThreshold() {
        return this.threshold;
    }
}
