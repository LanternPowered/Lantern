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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientEditCommandBlockPacket

object ClientEditCommandBlockEntityCodec : PacketDecoder<ClientEditCommandBlockPacket.Entity> {

    override fun decode(ctx: CodecContext, buf: ByteBuffer): ClientEditCommandBlockPacket.Entity {
        val entityId = buf.readVarInt()
        val command = buf.readString()
        val shouldTrackOutput = buf.readBoolean()
        return ClientEditCommandBlockPacket.Entity(entityId, command, shouldTrackOutput)
    }
}
