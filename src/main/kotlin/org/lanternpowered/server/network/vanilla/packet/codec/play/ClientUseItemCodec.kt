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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientUseItemPacket
import org.spongepowered.api.data.type.HandTypes

object ClientUseItemCodec : PacketDecoder<ClientUseItemPacket> {

    override fun decode(context: CodecContext, buf: ByteBuffer): ClientUseItemPacket =
            ClientUseItemPacket(if (buf.readVarInt() == 0) HandTypes.MAIN_HAND.get() else HandTypes.OFF_HAND.get())
}
