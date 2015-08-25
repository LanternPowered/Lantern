package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInOutRegisterChannels implements Message {

    private final Set<String> channels;

    /**
     * Creates a new register channels message.
     * 
     * @param channels the channels
     */
    public MessagePlayInOutRegisterChannels(Set<String> channels) {
        this.channels = checkNotNull(channels, "channels");
    }

    /**
     * Gets the channels.
     * 
     * @return the channels
     */
    public Set<String> getChannels() {
        return this.channels;
    }

}
