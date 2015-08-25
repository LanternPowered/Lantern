package org.lanternpowered.server.network.session;

import org.spongepowered.api.Server;

import io.netty.channel.Channel;

public interface SessionFactory {

    /**
     * Creates a new session instance.
     * 
     * @param server the server
     * @param channel the network channel
     * @return the session
     */
    Session create(Server server, Channel channel);

}
