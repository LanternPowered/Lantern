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

import org.lanternpowered.api.cause.CauseContextKeys
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.withFrame
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.event.LanternEventFactory
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.vanilla.packet.type.play.ResourcePackStatusPacket

object ResourcePackStatusHandler : PacketHandler<ResourcePackStatusPacket> {

    override fun handle(ctx: NetworkContext, packet: ResourcePackStatusPacket) {
        val resourcePack = ctx.session.player.resourcePacketSendQueue.poll(packet.status)
        val player = ctx.session.player
        if (resourcePack == null) {
            LanternGame.logger.warn("${player.name} received a unexpected resource pack status " +
                    "message (${packet.status}), no resource pack was pending.")
            return
        }
        CauseStack.withFrame { frame ->
            frame.addContext(CauseContextKeys.PLAYER, player)
            val event = LanternEventFactory.createResourcePackStatusEvent(
                    frame.currentCause, resourcePack, player, packet.status)
            EventManager.post(event)
        }
    }
}
