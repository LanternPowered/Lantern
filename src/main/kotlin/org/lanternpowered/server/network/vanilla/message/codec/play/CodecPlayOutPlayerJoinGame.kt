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
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerJoinGame
import org.lanternpowered.server.world.dimension.LanternDimensionType
import kotlin.experimental.or
import kotlin.math.min

class CodecPlayOutPlayerJoinGame : Codec<MessagePlayOutPlayerJoinGame> {

    override fun encode(context: CodecContext, message: MessagePlayOutPlayerJoinGame): ByteBuffer {
        context.channel.attr(PLAYER_ENTITY_ID).set(message.entityId)
        return context.byteBufAlloc().buffer().apply {
            writeInt(message.entityId)
            var gameMode = (message.gameMode as LanternGameMode).internalId.toByte()
            if (message.isHardcore) {
                gameMode = gameMode or 0x8
            }
            writeByte(gameMode)
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

        @JvmField val PLAYER_ENTITY_ID = AttributeKey.valueOf<Int>("player-entity-id")
    }
}
