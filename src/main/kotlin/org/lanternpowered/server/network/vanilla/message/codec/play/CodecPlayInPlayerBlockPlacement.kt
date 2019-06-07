/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.codec.play

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.codec.play.CodecUtils.decodeDirection
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerBlockPlacement
import org.spongepowered.api.data.type.HandTypes

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
