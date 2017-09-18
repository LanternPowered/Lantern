/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.handler.play;

import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.type.LanternSkinPart;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClientSettings;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.living.humanoid.player.PlayerChangeClientSettingsEvent;

public final class HandlerPlayInClientSettings implements Handler<MessagePlayInClientSettings> {

    @Override
    public void handle(NetworkContext context, MessagePlayInClientSettings message) {
        final LanternPlayer player = context.getSession().getPlayer();
        final Cause cause = Cause.of(EventContext.empty(), player);
        final PlayerChangeClientSettingsEvent event = SpongeEventFactory.createPlayerChangeClientSettingsEvent(
                cause, message.getChatVisibility(), LanternSkinPart.fromBitPattern(message.getSkinPartsBitPattern()),
                message.getLocale(), player, message.getEnableColors(), message.getViewDistance());
        Sponge.getEventManager().post(event);
        player.setLocale(event.getLocale());
        player.setViewDistance(event.getViewDistance());
        player.setChatVisibility(event.getChatVisibility());
        player.setChatColorsEnabled(message.getEnableColors());
        player.offer(LanternKeys.DISPLAYED_SKIN_PARTS, event.getDisplayedSkinParts());
        player.offer(Keys.DOMINANT_HAND, message.getDominantHand());
    }
}
