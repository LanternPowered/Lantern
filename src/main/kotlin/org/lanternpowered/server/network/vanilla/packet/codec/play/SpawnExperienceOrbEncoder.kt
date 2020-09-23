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
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnExperienceOrbPacket

object SpawnExperienceOrbEncoder : Codec<SpawnExperienceOrbPacket> {

    override fun encode(ctx: CodecContext, packet: SpawnExperienceOrbPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeVarInt(packet.entityId)
        buf.writeVector3d(packet.position)
        buf.writeShort(packet.quantity.coerceAtMost(Short.MAX_VALUE.toInt()).toShort())
        return buf
    }
}
