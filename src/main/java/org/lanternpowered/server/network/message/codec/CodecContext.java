package org.lanternpowered.server.network.message.codec;

import org.lanternpowered.server.network.message.codec.object.serializer.ObjectSerializerContext;
import org.lanternpowered.server.network.session.Session;

import io.netty.channel.Channel;

public interface CodecContext extends ObjectSerializerContext {

    /**
     * Gets the channel that is used with this context.
     * 
     * @return the channel
     */
    Channel channel();

    /**
     * Gets the session that is used with this context.
     * 
     * @return the session
     */
    Session session();
}
