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
import org.lanternpowered.server.entity.event.RefreshAbilitiesPlayerEvent;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerAbilities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityVelocity;
import org.spongepowered.api.data.key.Keys;

public class HandlerPlayInPlayerAbilities implements Handler<MessagePlayInPlayerAbilities> {

    @Override
    public void handle(NetworkContext context, MessagePlayInPlayerAbilities message) {
        final boolean flying = message.isFlying();
        final LanternPlayer player = context.getSession().getPlayer();
        if (!flying || player.get(Keys.CAN_FLY).orElse(false)) {
            player.offer(Keys.IS_FLYING, flying);
        } else {
            if (player.get(LanternKeys.SUPER_STEVE).orElse(false)) {
                // TODO: Just set velocity once it's implemented
                context.getSession().send(new MessagePlayOutEntityVelocity(player.getNetworkId(), 0, 1.0, 0));
                player.offer(LanternKeys.IS_ELYTRA_FLYING, true);
            }
            player.triggerEvent(RefreshAbilitiesPlayerEvent.of());
        }
    }
}
