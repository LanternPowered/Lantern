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
import org.lanternpowered.server.network.vanilla.message.type.play.PlayerRespawnMessage
import org.lanternpowered.server.registry.type.data.GameModeRegistry
import org.lanternpowered.server.world.dimension.LanternDimensionType

class PlayerRespawnCodec : Codec<PlayerRespawnMessage> {

    override fun encode(context: CodecContext, message: PlayerRespawnMessage): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeInt((message.dimensionType as LanternDimensionType<*>).internalId)
            writeLong(message.seed)
            writeByte(GameModeRegistry.getId(message.gameMode).toByte())
            writeString(if (message.lowHorizon) "flat" else "default")
        }
    }
}
