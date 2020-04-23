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
import org.lanternpowered.server.network.vanilla.message.type.play.SetDifficultyMessage
import org.lanternpowered.server.registry.type.world.DifficultyRegistry

class SetDifficultyCodec : Codec<SetDifficultyMessage> {

    override fun encode(context: CodecContext, message: SetDifficultyMessage): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeByte(DifficultyRegistry.getId(message.difficulty).toByte())
            writeBoolean(message.locked)
        }
    }
}
