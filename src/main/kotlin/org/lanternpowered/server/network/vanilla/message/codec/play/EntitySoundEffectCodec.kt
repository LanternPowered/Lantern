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
import org.lanternpowered.server.network.vanilla.message.type.play.EntitySoundEffectMessage
import org.lanternpowered.server.registry.type.effect.sound.SoundCategoryRegistry

class EntitySoundEffectCodec : Codec<EntitySoundEffectMessage> {

    override fun encode(context: CodecContext, message: EntitySoundEffectMessage): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeVarInt(message.type)
            writeVarInt(SoundCategoryRegistry.getId(message.category))
            writeVarInt(message.entityId)
            writeFloat(message.volume)
            writeFloat(message.pitch)
        }
    }
}
