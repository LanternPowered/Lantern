package org.lanternpowered.server.network.vanilla.message.type.play;

import io.netty.buffer.ByteBuf;

import org.lanternpowered.server.network.message.Message;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MessagePlayInOutChannelPayload implements Message {

    private final ByteBuf content;
    private final String channel;

    /**
     * Creates a new custom payload message.
     * 
     * @param channel the channel
     * @param content the content
     */
    public MessagePlayInOutChannelPayload(String channel, ByteBuf content) {
        this.channel = checkNotNull(channel, "channel");
        this.content = checkNotNull(content, "content");
    }

    /**
     * Gets the channel the plugin message is using.
     * 
     * @return the channel
     */
    public String getChannel() {
        return this.channel;
    }

    /**
     * Gets the content of the plugin message.
     * 
     * @return the content
     */
    public ByteBuf getContent() {
        return this.content;
    }

}
