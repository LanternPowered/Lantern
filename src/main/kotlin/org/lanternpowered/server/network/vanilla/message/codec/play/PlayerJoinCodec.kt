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

import io.netty.util.AttributeKey
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.PlayerJoinMessage
import org.lanternpowered.server.registry.type.data.GameModeRegistry
import org.lanternpowered.server.world.dimension.LanternDimensionType
import kotlin.math.min

class PlayerJoinCodec : Codec<PlayerJoinMessage> {

    override fun encode(context: CodecContext, message: PlayerJoinMessage): ByteBuffer {
        context.channel.attr(PLAYER_ENTITY_ID).set(message.entityId)
        return context.byteBufAlloc().buffer().apply {
            writeInt(message.entityId)
            var gameMode = GameModeRegistry.getId(message.gameMode)
            if (message.isHardcore)
                gameMode += 0x8
            writeByte(gameMode.toByte())
            writeInt((message.dimensionType as LanternDimensionType<*>).internalId)
            writeLong(message.seed)
            writeByte(min(message.playerListSize, 255).toByte())
            writeString(if (message.lowHorizon) "flat" else "default")
            writeVarInt(message.viewDistance)
            writeBoolean(message.reducedDebug)
            writeBoolean(message.enableRespawnScreen)
        }
    }

    companion object {

        @JvmField val PLAYER_ENTITY_ID: AttributeKey<Int> = AttributeKey.valueOf<Int>("player-entity-id")
    }
}
