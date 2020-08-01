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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPlayerLookPacket
import org.lanternpowered.server.util.wrapDegRot
import org.spongepowered.math.vector.Vector3d

object ClientPlayerLookHandler : Handler<ClientPlayerLookPacket> {

    override fun handle(context: NetworkContext, packet: ClientPlayerLookPacket) {
        val player = context.session.player
        player.setRawRotation(this.toRotation(packet.pitch, packet.yaw))
        player.handleOnGroundState(packet.isOnGround)
    }

    fun toRotation(yaw: Float, pitch: Float): Vector3d =
            Vector3d(yaw.toDouble().wrapDegRot(), pitch.toDouble().wrapDegRot(), 0.0)
}
