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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientBlockPlacementPacket

object ClientBlockPlacementHandler : PacketHandler<ClientBlockPlacementPacket> {

    override fun handle(ctx: NetworkContext, packet: ClientBlockPlacementPacket) {
        val player = ctx.session.player
        player.resetIdleTime()
        player.resetOpenedSignPosition()
        player.interactionHandler.handleBlockPlacing(packet)
    }
}
