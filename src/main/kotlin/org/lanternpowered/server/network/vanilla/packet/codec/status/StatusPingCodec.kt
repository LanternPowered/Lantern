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
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.status.StatusPingPacket

class StatusPingCodec : Codec<StatusPingPacket> {

    override fun encode(context: CodecContext, packet: StatusPingPacket): ByteBuffer =
            context.byteBufAlloc().buffer(Long.SIZE_BYTES).writeLong(packet.time)

    override fun decode(context: CodecContext, buf: ByteBuffer): StatusPingPacket = StatusPingPacket(buf.readLong())
}
