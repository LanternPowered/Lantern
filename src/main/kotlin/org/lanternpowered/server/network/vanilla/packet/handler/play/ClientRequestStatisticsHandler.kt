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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientRequestStatisticsPacket

object ClientRequestStatisticsHandler : PacketHandler<ClientRequestStatisticsPacket> {

    override fun handle(ctx: NetworkContext, packet: ClientRequestStatisticsPacket) {
        val session = ctx.session
        // TODO: Update statistics protocol
        // session.send(session.getPlayer().getStatisticMap().createStatisticsMessage());
    }
}
