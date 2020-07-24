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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientClickRecipePacket

object ClientClickRecipeCodec : PacketDecoder<ClientClickRecipePacket> {

    override fun decode(context: CodecContext, buf: ByteBuffer): ClientClickRecipePacket {
        val windowId: Int = buf.readByte().toInt() and 0xff
        val recipe = buf.readString()
        val shift = buf.readBoolean()
        return ClientClickRecipePacket(windowId, recipe, shift)
    }
}
