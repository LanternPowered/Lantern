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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPlayerSwingArmPacket;

public class HandlerPlayInPlayerSwingArm implements Handler<ClientPlayerSwingArmPacket> {

    @Override
    public void handle(NetworkContext context, ClientPlayerSwingArmPacket packet) {
        final LanternPlayer player = context.getSession().getPlayer();
        player.resetIdleTimeoutCounter();
        player.resetOpenedSignPosition();
        player.getInteractionHandler().handleSwingArm(packet);
    }
}
