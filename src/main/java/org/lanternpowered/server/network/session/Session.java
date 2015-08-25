package org.lanternpowered.server.network.session;

import io.netty.channel.Channel;

import javax.annotation.Nullable;

import org.lanternpowered.server.entity.player.LanternPlayer;
import org.lanternpowered.server.network.message.Message;

import org.spongepowered.api.network.PlayerConnection;

public interface Session extends PlayerConnection {

    /**
     * Gets the channel that is assigned to this session.
     * 
     * @return the channel
     */
    Channel getChannel();

    /**
     * Gets whether the session still active is.
     * 
     * @return is active
     */
    boolean isActive();

    /**
     * Sends the message for this session.
     * 
     * @param message the message
     */
    void send(Message message);

    /**
     * Sends the messages for this session.
     * 
     * @param messages the messages
     */
    void send(Message... messages);

    void send(Iterable<Message> messages);

    void receive(Message message);

    void receive(Message... messages);

    void receive(Iterable<Message> messages);

    @Override
    @Nullable
    LanternPlayer getPlayer();

}
