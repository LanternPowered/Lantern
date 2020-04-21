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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntitySoundEffect
import org.lanternpowered.server.registry.type.effect.sound.SoundCategoryRegistry

class CodecPlayOutEntitySoundEffect : Codec<MessagePlayOutEntitySoundEffect> {

    override fun encode(context: CodecContext, message: MessagePlayOutEntitySoundEffect): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        buf.writeVarInt(message.type)
        buf.writeVarInt(SoundCategoryRegistry.getId(message.category))
        buf.writeVarInt(message.entityId)
        buf.writeFloat(message.volume)
        buf.writeFloat(message.pitch)
        return buf
    }
}
