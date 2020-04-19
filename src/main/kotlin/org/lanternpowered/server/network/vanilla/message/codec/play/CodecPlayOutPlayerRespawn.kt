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

import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerRespawn
import org.lanternpowered.server.world.dimension.LanternDimensionType

class CodecPlayOutPlayerRespawn : Codec<MessagePlayOutPlayerRespawn> {

    override fun encode(context: CodecContext, message: MessagePlayOutPlayerRespawn): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeInt((message.dimensionType as LanternDimensionType<*>).internalId)
            writeLong(message.seed)
            writeByte((message.gameMode as LanternGameMode).internalId.toByte())
            writeString(if (message.lowHorizon) "flat" else "default")
        }
    }
}
