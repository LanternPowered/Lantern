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
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientPlayerMovementAndLookPacket

object ClientPlayerMovementAndLookCodec : PacketDecoder<ClientPlayerMovementAndLookPacket> {

    override fun decode(context: CodecContext, buf: ByteBuffer): ClientPlayerMovementAndLookPacket {
        val position = buf.readVector3d()
        val yaw = buf.readFloat()
        val pitch = buf.readFloat()
        val onGround = buf.readBoolean()
        return ClientPlayerMovementAndLookPacket(position, yaw, pitch, onGround)
    }
}
