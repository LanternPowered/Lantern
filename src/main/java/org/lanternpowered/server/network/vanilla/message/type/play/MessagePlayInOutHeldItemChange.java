package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInOutHeldItemChange implements Message {

    private final byte slot;

    /**
     * Creates a new held item change message.
     * 
     * @param slot the new slot
     */
    public MessagePlayInOutHeldItemChange(byte slot) {
        this.slot = slot;
    }

    /**
     * Gets the item slot the player changed to or will change to.
     * 
     * @return the slot
     */
    public byte getSlot() {
        return this.slot;
    }

}
