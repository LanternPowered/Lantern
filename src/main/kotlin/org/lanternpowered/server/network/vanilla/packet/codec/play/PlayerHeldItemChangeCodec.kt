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
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerHeldItemChangePacket

/**
 * Note that incoming and outgoing codecs are not the same, but we use the same
 * message class.
 */
object PlayerHeldItemChangeCodec : Codec<PlayerHeldItemChangePacket> {

    override fun encode(context: CodecContext, packet: PlayerHeldItemChangePacket): ByteBuffer =
            context.byteBufAlloc().buffer(1).writeByte(packet.slot.toByte())

    override fun decode(context: CodecContext, buf: ByteBuffer): PlayerHeldItemChangePacket =
            PlayerHeldItemChangePacket(buf.readShort().toInt())
}
