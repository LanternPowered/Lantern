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
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.UpdateViewPositionPacket

class UpdateViewPositionCodec : Codec<UpdateViewPositionPacket> {

    override fun encode(context: CodecContext, packet: UpdateViewPositionPacket): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeVarInt(packet.chunkX)
            writeVarInt(packet.chunkZ)
        }
    }
}
