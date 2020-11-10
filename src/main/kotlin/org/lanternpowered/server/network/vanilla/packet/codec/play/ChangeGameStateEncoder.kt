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
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.internal.ChangeGameStatePacket

object ChangeGameStateEncoder : PacketEncoder<ChangeGameStatePacket> {

    override fun encode(ctx: CodecContext, packet: ChangeGameStatePacket): ByteBuffer {
        return ctx.byteBufAlloc().buffer(Byte.SIZE_BYTES + Int.SIZE_BYTES).apply {
            writeByte(packet.type.toByte())
            writeFloat(packet.value)
        }
    }
}
