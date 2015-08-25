package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInChangeItemName implements Message {

    private final String name;

    /**
     * Creates a new change item name message.
     * 
     * @param name the name
     */
    public MessagePlayInChangeItemName(String name) {
        this.name = checkNotNull(name, "name");
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
    }

}
