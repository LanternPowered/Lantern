package org.lanternpowered.server.network;

import io.netty.channel.Channel;

import org.lanternpowered.server.network.session.Session;

public interface NetworkContext {

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
