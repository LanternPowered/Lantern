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

import org.lanternpowered.server.game.registry.type.world.DifficultyRegistryModule
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInSetDifficulty

class CodecPlayInSetDifficulty : Codec<MessagePlayInSetDifficulty> {

    override fun decode(context: CodecContext, buf: ByteBuffer): MessagePlayInSetDifficulty {
        return MessagePlayInSetDifficulty(DifficultyRegistryModule.get().getByInternalId(buf.readVarInt()).get())
    }
}
