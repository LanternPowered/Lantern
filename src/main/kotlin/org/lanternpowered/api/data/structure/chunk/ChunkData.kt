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
package org.lanternpowered.api.data.structure.chunk

import org.lanternpowered.api.block.BlockState
import org.lanternpowered.api.data.persistence.DataView
import org.lanternpowered.api.data.persistence.dataQueryOf
import org.lanternpowered.api.data.structure.block.BlockEntityData
import org.lanternpowered.api.data.structure.entity.EntityData
import org.lanternpowered.api.util.palette.SerializedPaletteBasedArray
import org.lanternpowered.api.world.chunk.Chunk
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataSerializable

/**
 * Represents the data of a [Chunk].
 *
 * @property blocks Data related to blocks in the chunk
 * @property entities The entities in the chunk
 * @property blockLight The block light data
 * @property skylight The skylight data
 * @property data Extra data related to chunks
 */
class ChunkData(
        val blocks: SerializedPaletteBasedArray<BlockState>,
        val blockEntities: List<BlockEntityData>,
        val blockLight: ByteArray,
        val skylight: ByteArray,
        val entities: List<EntityData>,
        val data: DataView
) : DataSerializable {

    override fun getContentVersion(): Int = 1

    override fun toContainer(): DataContainer = DataContainer.createNew()
            .set(Queries.Blocks, this.blocks)
            .set(Queries.BlockEntities, this.blockEntities)
            .set(Queries.BlockLight, this.blockLight)
            .set(Queries.Entities, this.entities)
            .set(Queries.Skylight, this.skylight)
            .set(Queries.Data, this.data)

    object Queries {

        val Blocks = dataQueryOf("Blocks")
        val BlockLight = dataQueryOf("BlockLight")
        val BlockEntities = dataQueryOf("BlockEntities")
        val Entities = dataQueryOf("Entities")
        val Skylight = dataQueryOf("Skylight")
        val Data = dataQueryOf("Data")
    }
}
