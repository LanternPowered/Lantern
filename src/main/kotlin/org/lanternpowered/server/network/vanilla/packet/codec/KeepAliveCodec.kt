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
package org.lanternpowered.server.network.vanilla.packet.codec

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.KeepAlivePacket

object KeepAliveCodec : PacketEncoder<KeepAlivePacket>, PacketDecoder<KeepAlivePacket> {

    override fun encode(ctx: CodecContext, packet: KeepAlivePacket): ByteBuffer =
            ctx.byteBufAlloc().buffer(Long.SIZE_BYTES).writeLong(packet.time)

    override fun decode(ctx: CodecContext, buf: ByteBuffer): KeepAlivePacket = KeepAlivePacket(buf.readLong())
}
