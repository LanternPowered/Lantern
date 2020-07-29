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
import org.lanternpowered.api.data.persistence.DataContainer
import org.lanternpowered.api.data.persistence.DataQuery
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerJoinPacket
import org.lanternpowered.server.registry.type.data.GameModeRegistry
import kotlin.math.min

object PlayerJoinCodec : PacketEncoder<PlayerJoinPacket> {

    val PLAYER_ENTITY_ID: AttributeKey<Int> = AttributeKey.valueOf<Int>("player-entity-id")

    private const val dimensionTypeKey = "minecraft:dimension_type"
    private const val biomeTypeKey = "minecraft:worldgen/biome"

    private val dimensionTypeQuery = DataQuery.of(this.dimensionTypeKey)
    private val biomeTypeQuery = DataQuery.of(this.biomeTypeKey)

    private val typeKey = DataQuery.of("type")
    private val nameKey = DataQuery.of("name")
    private val valueKey = DataQuery.of("value")

    override fun encode(context: CodecContext, packet: PlayerJoinPacket): ByteBuffer {
        context.channel.attr(PLAYER_ENTITY_ID).set(packet.entityId)
        val buf = context.byteBufAlloc().buffer()
        buf.writeInt(packet.entityId)
        buf.writeBoolean(packet.isHardcore)
        buf.writeByte(GameModeRegistry.getId(packet.gameMode).toByte())
        buf.writeByte(GameModeRegistry.getId(packet.previousGameMode).toByte())
        val worldRegistry = packet.dimensionRegistry
        buf.writeVarInt(worldRegistry.size)
        for (worldEntry in worldRegistry)
            buf.writeResourceKey(worldEntry.key)

        val dimensionCodec = DataContainer.createNew()

        val dimensionTypes = dimensionCodec.createView(this.dimensionTypeQuery)
        dimensionTypes.set(this.typeKey, this.dimensionTypeKey)
        dimensionTypes.set(this.valueKey, worldRegistry
                .map { (key, data) -> data.set(this.nameKey, key.formatted) })

        val biomeTypes = dimensionCodec.createView(this.biomeTypeQuery)
        biomeTypes.set(this.typeKey, this.biomeTypeKey)
        biomeTypes.set(this.valueKey, packet.biomeRegistry
                .map { (key, data) -> data.set(this.nameKey, key.formatted) })

        buf.writeDataView(dimensionCodec)
        buf.writeResourceKey(packet.dimension)
        buf.writeResourceKey(packet.worldName)
        buf.writeLong(packet.seed)
        buf.writeByte(min(packet.playerListSize, 255).toByte())
        buf.writeVarInt(packet.viewDistance)
        buf.writeBoolean(packet.hasReducedDebug)
        buf.writeBoolean(packet.enableRespawnScreen)
        buf.writeBoolean(packet.isDebug)
        buf.writeBoolean(packet.isFlat)
        return buf
    }
}
