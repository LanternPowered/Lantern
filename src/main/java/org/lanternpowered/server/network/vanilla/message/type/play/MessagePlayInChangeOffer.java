package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInChangeOffer implements Message {

    private final int index;

    /**
     * Creates a new change offer message.
     * 
     * @param index the index
     */
    public MessagePlayInChangeOffer(int index) {
        this.index = index;
    }

    /**
     * Gets the offer index that the sender changed to.
     * 
     * @return the offer index
     */
    public int getIndex() {
        return this.index;
    }

}
