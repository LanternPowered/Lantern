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
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SetActiveAdvancementTreePacket

object SetActiveAdvancementTreeCodec : PacketEncoder<SetActiveAdvancementTreePacket> {

    override fun encode(context: CodecContext, packet: SetActiveAdvancementTreePacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        val id = packet.id
        buf.writeBoolean(id != null)
        if (id != null)
            buf.writeString(id)
        return buf
    }
}
