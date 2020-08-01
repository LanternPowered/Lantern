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
package org.lanternpowered.server.network.vanilla.packet.handler.play;

import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.packet.handler.Handler;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSneakStatePacket;
import org.spongepowered.api.data.Keys;

public final class HandlerPlayInPlayerSneak implements Handler<ClientSneakStatePacket> {

    @Override
    public void handle(NetworkContext context, ClientSneakStatePacket packet) {
        context.getSession().getPlayer().offer(Keys.IS_SNEAKING, packet.isSneaking());
    }
}