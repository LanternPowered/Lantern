package org.lanternpowered.server.network.vanilla.message.type.connection;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.text.Text;

public final class MessageOutDisconnect implements Message {

    private final Text reason;

    /**
     * Creates a new disconnect message with a specific reason.
     * 
     * @param json the reason
     */
    public MessageOutDisconnect(Text reason) {
        this.reason = checkNotNull(reason, "reason");
    }

    /**
     * Gets the reason.
     * 
     * @return the reason
     */
    public Text getReason() {
        return this.reason;
    }
}
