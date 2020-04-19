/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network.vanilla.message.handler.play;

import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInResourcePackStatus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.resourcepack.ResourcePack;

import java.util.Optional;

public final class HandlerPlayInResourcePackStatus implements Handler<MessagePlayInResourcePackStatus> {

    @Override
    public void handle(NetworkContext context, MessagePlayInResourcePackStatus message) {
        final Optional<ResourcePack> resourcePack = context.getSession().getPlayer().getResourcePackSendQueue().poll(message.getStatus());
        final LanternPlayer player = context.getSession().getPlayer();
        if (!resourcePack.isPresent()) {
            Lantern.getLogger().warn("{} received a unexpected resource pack status message ({}), no resource pack was pending",
                    player.getName(), message.getStatus());
            return;
        }
        final CauseStack causeStack = CauseStack.current();
        try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
            frame.addContext(EventContextKeys.PLAYER, player);
            Sponge.getEventManager().post(SpongeEventFactory.createResourcePackStatusEvent(
                    frame.getCurrentCause(), resourcePack.get(), player, message.getStatus()));
        }
    }
}
