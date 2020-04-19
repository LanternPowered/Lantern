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
package org.lanternpowered.server.network.vanilla.message.codec.play

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecUtils.decodeDirection
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerBlockPlacement
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.math.vector.Vector3d

class CodecPlayInPlayerBlockPlacement : Codec<MessagePlayInPlayerBlockPlacement> {

    override fun decode(context: CodecContext, buf: ByteBuffer): MessagePlayInPlayerBlockPlacement {
        val hand = if (buf.readVarInt() == 0) HandTypes.MAIN_HAND else HandTypes.OFF_HAND
        val position = buf.readPosition()
        val face = decodeDirection(buf.readVarInt())
        val ox = buf.readFloat().toDouble()
        val oy = buf.readFloat().toDouble()
        val oz = buf.readFloat().toDouble()
        val offset = Vector3d(ox, oy, oz)
        val insideBlock = buf.readBoolean()
        return MessagePlayInPlayerBlockPlacement(position, offset, face, hand, insideBlock)
    }
}
