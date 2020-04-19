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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetDifficulty
import org.lanternpowered.server.world.difficulty.LanternDifficulty

class CodecPlayOutSetDifficulty : Codec<MessagePlayOutSetDifficulty> {

    override fun encode(context: CodecContext, message: MessagePlayOutSetDifficulty): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeByte((message.difficulty as LanternDifficulty).internalId.toByte())
            writeBoolean(message.locked)
        }
    }
}
