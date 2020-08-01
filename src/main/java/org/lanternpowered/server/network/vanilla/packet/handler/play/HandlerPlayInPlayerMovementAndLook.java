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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPlayerMovementAndLookPacket;

public class HandlerPlayInPlayerMovementAndLook implements Handler<ClientPlayerMovementAndLookPacket> {

    @Override
    public void handle(NetworkContext context, ClientPlayerMovementAndLookPacket packet) {
        final LanternPlayer player = context.getSession().getPlayer();
        player.setRawPosition(packet.getPosition());
        player.setRawRotation(HandlerPlayInPlayerLook.toRotation(packet.getPitch(), packet.getYaw()));
        player.handleOnGroundState(packet.isOnGround());
    }
}