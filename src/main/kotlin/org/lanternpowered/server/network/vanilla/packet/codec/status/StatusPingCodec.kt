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
package org.lanternpowered.server.network.vanilla.packet.codec.status

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.status.StatusPingPacket

object StatusPingCodec : PacketEncoder<StatusPingPacket>, PacketDecoder<StatusPingPacket> {

    override fun encode(ctx: CodecContext, packet: StatusPingPacket): ByteBuffer =
            ctx.byteBufAlloc().buffer(Long.SIZE_BYTES).writeLong(packet.time)

    override fun decode(ctx: CodecContext, buf: ByteBuffer): StatusPingPacket = StatusPingPacket(buf.readLong())
}
