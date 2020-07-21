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

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerRespawnPacket
import org.lanternpowered.server.registry.type.data.GameModeRegistry
import org.lanternpowered.server.world.dimension.LanternDimensionType

class PlayerRespawnCodec : Codec<PlayerRespawnPacket> {

    override fun encode(context: CodecContext, packet: PlayerRespawnPacket): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeInt((packet.dimensionType as LanternDimensionType<*>).internalId)
            writeLong(packet.seed)
            writeByte(GameModeRegistry.getId(packet.gameMode).toByte())
            writeString(if (packet.lowHorizon) "flat" else "default")
            writeBoolean(packet.copyMetadata)
        }
    }
}
