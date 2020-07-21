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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInUseEntity;

public final class HandlerPlayInUseEntityInteract implements Handler<PacketPlayInUseEntity.Interact> {

    @Override
    public void handle(NetworkContext context, PacketPlayInUseEntity.Interact packet) {
        final LanternPlayer player = context.getSession().getPlayer();
        player.getWorld().getEntityProtocolManager().playerInteract(player, packet.getEntityId(), packet.getPosition().orElse(null));
    }
}
