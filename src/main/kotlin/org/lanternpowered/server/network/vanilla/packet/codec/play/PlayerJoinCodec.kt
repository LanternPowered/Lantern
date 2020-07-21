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
package org.lanternpowered.server.network.vanilla.packet.codec.play

import io.netty.util.AttributeKey
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerJoinPacket
import org.lanternpowered.server.registry.type.data.GameModeRegistry
import org.lanternpowered.server.world.dimension.LanternDimensionType
import kotlin.math.min

class PlayerJoinCodec : Codec<PlayerJoinPacket> {

    override fun encode(context: CodecContext, packet: PlayerJoinPacket): ByteBuffer {
        context.channel.attr(PLAYER_ENTITY_ID).set(packet.entityId)
        return context.byteBufAlloc().buffer().apply {
            writeInt(packet.entityId)
            var gameMode = GameModeRegistry.getId(packet.gameMode)
            if (packet.isHardcore)
                gameMode += 0x8
            writeByte(gameMode.toByte())
            writeInt((packet.dimensionType as LanternDimensionType<*>).internalId)
            writeLong(packet.seed)
            writeByte(min(packet.playerListSize, 255).toByte())
            writeString(if (packet.lowHorizon) "flat" else "default")
            writeVarInt(packet.viewDistance)
            writeBoolean(packet.reducedDebug)
            writeBoolean(packet.enableRespawnScreen)
        }
    }

    companion object {

        @JvmField val PLAYER_ENTITY_ID: AttributeKey<Int> = AttributeKey.valueOf<Int>("player-entity-id")
    }
}
