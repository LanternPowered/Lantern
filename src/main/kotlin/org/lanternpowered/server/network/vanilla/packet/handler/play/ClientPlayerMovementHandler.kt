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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPlayerMovementPacket

object ClientPlayerMovementHandler : Handler<ClientPlayerMovementPacket> {

    override fun handle(context: NetworkContext, packet: ClientPlayerMovementPacket) {
        val player = context.session.player
        player.setRawPosition(packet.position)
        player.handleOnGroundState(packet.isOnGround)
    }
}
