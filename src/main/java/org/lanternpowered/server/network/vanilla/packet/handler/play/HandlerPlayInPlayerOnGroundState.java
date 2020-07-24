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

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.packet.handler.Handler;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPlayerOnGroundStatePacket;

public class HandlerPlayInPlayerOnGroundState implements Handler<ClientPlayerOnGroundStatePacket> {

    @Override
    public void handle(NetworkContext context, ClientPlayerOnGroundStatePacket packet) {
        final LanternPlayer player = context.getSession().getPlayer();
        player.handleOnGroundState(packet.isOnGround());
    }
}
