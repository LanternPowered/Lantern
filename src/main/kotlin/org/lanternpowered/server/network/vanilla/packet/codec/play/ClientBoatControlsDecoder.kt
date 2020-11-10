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
package org.lanternpowered.server.network.vanilla.packet.codec.play

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientBoatControlsPacket

object ClientBoatControlsDecoder : PacketDecoder<ClientBoatControlsPacket> {

    override fun decode(ctx: CodecContext, buf: ByteBuffer): ClientBoatControlsPacket {
        val leftPaddleTurning = buf.readBoolean()
        val rightPaddleTurning = buf.readBoolean()
        return ClientBoatControlsPacket(leftPaddleTurning, rightPaddleTurning)
    }
}
