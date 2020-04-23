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
import org.lanternpowered.server.network.vanilla.message.type.play.SoundEffectMessage
import org.lanternpowered.server.registry.type.effect.sound.SoundCategoryRegistry

class SoundEffectCodec : Codec<SoundEffectMessage> {

    override fun encode(context: CodecContext, message: SoundEffectMessage): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeVarInt(message.type)
            writeVarInt(SoundCategoryRegistry.getId(message.category))
            val pos = message.position
            writeInt((pos.x * 8.0).toInt())
            writeInt((pos.y * 8.0).toInt())
            writeInt((pos.z * 8.0).toInt())
            writeFloat(message.volume)
            writeFloat(message.pitch)
        }
    }
}
