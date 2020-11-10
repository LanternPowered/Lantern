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
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerRespawnPacket
import org.lanternpowered.server.registry.type.data.GameModeRegistry

object PlayerRespawnEncoder : PacketEncoder<PlayerRespawnPacket> {

    override fun encode(ctx: CodecContext, packet: PlayerRespawnPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeNamespacedKey(packet.dimension)
        buf.writeNamespacedKey(packet.worldName)
        buf.writeLong(packet.seed)
        buf.writeByte(GameModeRegistry.getId(packet.gameMode).toByte())
        buf.writeByte(GameModeRegistry.getId(packet.previousGameMode).toByte())
        buf.writeBoolean(packet.isFlat)
        buf.writeBoolean(packet.isDebug)
        buf.writeBoolean(packet.copyMetadata)
        return buf
    }
}
