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
package org.lanternpowered.server.network.vanilla.packet.handler.play

import org.lanternpowered.server.inventory.PlayerInventoryContainerSession
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.PacketHandler

class ContainerSessionForwardingHandler<P : Packet>(
        private val function: (PlayerInventoryContainerSession, P) -> Unit
) : PacketHandler<P> {

    override fun handle(ctx: NetworkContext, packet: P) = this.function(ctx.session.player.containerSession, packet)
}
