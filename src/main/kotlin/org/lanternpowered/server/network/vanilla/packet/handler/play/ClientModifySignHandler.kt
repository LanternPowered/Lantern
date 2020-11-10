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
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.event.impl.LanternChangeSignEvent
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientModifySignPacket
import org.spongepowered.api.block.entity.Sign

object ClientModifySignHandler : PacketHandler<ClientModifySignPacket> {

    override fun handle(ctx: NetworkContext, packet: ClientModifySignPacket) {
        val player = ctx.session.player
        val openedSignPosition = player.openedSignPosition
        val signPosition = packet.position
        player.resetOpenedSignPosition()
        if (openedSignPosition != signPosition)
            return
        val sign = player.world.getBlockEntity(signPosition).orNull() as? Sign ?: return
        val lines = sign.lines()
                .set(packet.lines.map(::textOf).toMutableList<Text>())
        val originalLines = lines.asImmutable()
        CauseStack.withFrame { frame ->
            frame.pushCause(player)
            frame.addContext(CauseContextKeys.PLAYER, player)

            val event = LanternChangeSignEvent(frame.currentCause, originalLines, lines, sign)
            EventManager.post(event)
            if (event.isCancelled)
                return
            sign.offer(lines)
        }
    }
}
