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
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SetExperiencePacket

class SetExperienceCodec : Codec<SetExperiencePacket> {

    override fun encode(context: CodecContext, packet: SetExperiencePacket): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeFloat(packet.exp)
            writeVarInt(packet.level)
            writeVarInt(packet.total)
        }
    }
}
