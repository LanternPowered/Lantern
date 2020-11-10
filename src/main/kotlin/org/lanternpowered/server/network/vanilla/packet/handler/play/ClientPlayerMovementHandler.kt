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
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPlayerMovementPacket

object ClientPlayerMovementHandler : PacketHandler<ClientPlayerMovementPacket> {

    override fun handle(ctx: NetworkContext, packet: ClientPlayerMovementPacket) {
        val player = ctx.session.player
        player.setRawPosition(packet.position)
        player.handleOnGroundState(packet.isOnGround)
    }
}
