package org.lanternpowered.server.network.vanilla.message.handler.play;

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClientSettings;

public class HandlerPlayInClientSettings implements Handler<MessagePlayInClientSettings> {

    @Override
    public void handle(Session session, MessagePlayInClientSettings message) {
        LanternPlayer player = session.getPlayer();
        player.setLocale(message.getLocale());
        player.setRenderDistance(message.getViewDistance());
    }
}
