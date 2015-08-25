package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayOutEntityCollectItem implements Message {

    private final int collectorId;
    private final int collectedId;

    /**
     * Creates a collect item message.
     * 
     * @param collectorId the collector id
     * @param collectedId the collected id
     */
    public MessagePlayOutEntityCollectItem(int collectorId, int collectedId) {
        this.collectorId = collectorId;
        this.collectedId = collectedId;
    }

    /**
     * Gets the id of the entity that will collect the collected entity.
     * 
     * @return the collector id
     */
    public int getCollectorId() {
        return this.collectorId;
    }

    /**
     * Gets the id of the entity that will be collected by the collector entity..
     * 
     * @return the collected id
     */
    public int getCollectedId() {
        return this.collectedId;
    }

}
