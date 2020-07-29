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

import org.lanternpowered.server.inventory.PlayerContainerSession
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.handler.Handler

class ContainerSessionForwardingHandler<P : Packet>(
        private val function: (PlayerContainerSession, P) -> Unit
) : Handler<P> {

    override fun handle(context: NetworkContext, packet: P) = this.function(context.session.player.containerSession, packet)
}
