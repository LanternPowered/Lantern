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
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.text.NetworkText
import org.lanternpowered.server.network.vanilla.packet.type.DisconnectPacket

object DisconnectEncoder : PacketEncoder<DisconnectPacket> {

    override fun encode(ctx: CodecContext, packet: DisconnectPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        NetworkText.write(ctx, buf, packet.reason)
        return buf
    }
}
