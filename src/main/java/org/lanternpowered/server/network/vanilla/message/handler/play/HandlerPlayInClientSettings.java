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

import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClientSettings;
import org.lanternpowered.server.registry.type.data.SkinPartRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.SkinPart;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.living.player.PlayerChangeClientSettingsEvent;

import java.util.Set;

public final class HandlerPlayInClientSettings implements Handler<MessagePlayInClientSettings> {

    @Override
    public void handle(NetworkContext context, MessagePlayInClientSettings message) {
        final LanternPlayer player = context.getSession().getPlayer();
        final Cause cause = Cause.of(EventContext.empty(), player);
        final Set<SkinPart> skinParts = SkinPartRegistry.INSTANCE.fromBitPattern(message.getSkinPartsBitPattern());
        final PlayerChangeClientSettingsEvent event = SpongeEventFactory.createPlayerChangeClientSettingsEvent(
                cause, message.getChatVisibility(), skinParts, message.getLocale(), player,
                message.getEnableColors(), message.getViewDistance());
        Sponge.getEventManager().post(event);
        player.setLocale(event.getLocale());
        player.setViewDistance(event.getViewDistance());
        player.setChatVisibility(event.getChatVisibility());
        player.setChatColorsEnabled(message.getEnableColors());
        player.offer(LanternKeys.DISPLAYED_SKIN_PARTS, event.getDisplayedSkinParts());
        player.offer(Keys.DOMINANT_HAND, message.getDominantHand());
    }
}
