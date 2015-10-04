package org.lanternpowered.server.network.vanilla.message.handler.play;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.channel.LanternChannelBuf;
import org.lanternpowered.server.network.channel.LanternChannelRegistrar.RegisteredChannel;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

public final class HandlerPlayInChannelPayload implements Handler<MessagePlayInOutChannelPayload> {

    @Override
    public void handle(Session session, MessagePlayInOutChannelPayload message) {
        String channelName = message.getChannel();
        RegisteredChannel channel = LanternGame.get().getChannelRegistrar().getRegisteredChannel(channelName);
        if (channel != null) {
            channel.getListener().handlePayload(session, channelName, new LanternChannelBuf(message.getContent()));
        }
    }
}
