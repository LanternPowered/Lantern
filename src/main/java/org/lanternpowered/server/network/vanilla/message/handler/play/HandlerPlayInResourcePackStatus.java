package org.lanternpowered.server.network.vanilla.message.handler.play;

import java.util.Optional;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInResourcePackStatus;
import org.lanternpowered.server.resourcepack.LanternResourcePackFactory;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.resourcepack.ResourcePack;

public final class HandlerPlayInResourcePackStatus implements Handler<MessagePlayInResourcePackStatus> {

    @Override
    public void handle(Session session, MessagePlayInResourcePackStatus message) {
        LanternResourcePackFactory factory = LanternGame.get().getRegistry().getResourcePackFactory();
        Optional<ResourcePack> resourcePack = factory.getByHash(message.getHash());
        if (!resourcePack.isPresent()) {
            resourcePack = factory.getById(message.getHash());
        }
        if (!resourcePack.isPresent()) {
            LanternGame.log().warn("Received unknown resource pack with hash or id: " + message.getHash() +
                    " and status: " + message.getStatus().toString().toLowerCase());
            return;
        }
        LanternGame.get().getEventManager().post(SpongeEventFactory.createResourcePackStatusEvent(
                LanternGame.get(), resourcePack.get(), session.getPlayer(), message.getStatus()));
    }
}
