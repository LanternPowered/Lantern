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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientUpdateJigsawBlockPacket

object ClientUpdateJigsawBlockDecoder : PacketDecoder<ClientUpdateJigsawBlockPacket> {

    override fun decode(ctx: CodecContext, buf: ByteBuffer): ClientUpdateJigsawBlockPacket {
        val position = buf.readBlockPosition()
        val name = buf.readString()
        val target = buf.readString()
        val pool = buf.readString()
        val finalState = buf.readString()
        val jointType = buf.readString()
        return ClientUpdateJigsawBlockPacket(position, name, target, pool, finalState, jointType)
    }
}
