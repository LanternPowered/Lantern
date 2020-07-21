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

import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.packet.handler.Handler
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientBlockPlacementPacket

class ClientBlockPlacementHandler : Handler<ClientBlockPlacementPacket> {

    override fun handle(context: NetworkContext, packet: ClientBlockPlacementPacket) {
        val player = context.session.player
        player.resetIdleTimeoutCounter()
        player.resetOpenedSignPosition()
        player.interactionHandler.handleBlockPlacing(packet)
    }
}
