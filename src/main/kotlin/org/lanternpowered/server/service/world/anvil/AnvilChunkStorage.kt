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
package org.lanternpowered.server.service.world.anvil

import org.lanternpowered.api.data.persistence.DataContainer
import org.lanternpowered.api.data.persistence.DataQuery
import org.lanternpowered.api.data.persistence.DataView
import org.lanternpowered.api.data.persistence.DataViewSafetyMode
import org.lanternpowered.api.service.world.chunk.ChunkGroupData
import org.lanternpowered.api.service.world.chunk.ChunkGroupPosition
import org.lanternpowered.api.service.world.chunk.ChunkStorage
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.data.persistence.nbt.NbtStreamUtils
import org.spongepowered.math.vector.Vector3i
import java.nio.file.Path
import kotlin.math.floor

class AnvilChunkStorage(worldDirectory: Path) : ChunkStorage {

    override val groupSize: Vector3i = Vector3i(1, AnvilRegionFile.REGION_Y_SIZE, 1)

    @Volatile private var closed = false
    private val anvilRegionFileCache = AnvilRegionFileCache(worldDirectory)

    private fun checkOpen() {
        check(!this.closed) { "The chunk storage is closed." }
    }

    override fun exists(position: ChunkGroupPosition): Boolean {
        this.checkOpen()
        return this.anvilRegionFileCache.exists(position)
    }

    override fun delete(position: ChunkGroupPosition): Boolean {
        this.checkOpen()
        return this.anvilRegionFileCache.delete(position)
    }

    override fun save(position: ChunkGroupPosition, data: ChunkGroupData) {
        this.checkOpen()
        val output = this.anvilRegionFileCache.getOutputStream(position)
        val dataView = this.fixSavedData(position, data)
        NbtStreamUtils.write(dataView, output, false)
    }

    override fun load(position: ChunkGroupPosition): ChunkGroupData? {
        this.checkOpen()
        val input = this.anvilRegionFileCache.getInputStream(position) ?: return null
        val data = NbtStreamUtils.read(input, false)
        return this.fixLoadedData(data)
    }

    override fun sequence(): Sequence<ChunkStorage.Entry> {
        this.checkOpen()
        return this.anvilRegionFileCache.sequence()
                .flatMap { file ->
                    file.all.asSequence().map { position -> Entry(position) }
                }
    }

    private inner class Entry(override val position: ChunkGroupPosition) : ChunkStorage.Entry {
        override fun load(): ChunkGroupData =
                load(this.position) ?: throw IllegalStateException("The data for chunk $position is no longer available.")
    }

    private class SectionData {

        var base: DataContainer? = null
        var blockEntities: MutableList<DataView>? = null
        var entities: MutableList<DataView>? = null
        var lightPopulated: Boolean = false

        fun build(): DataContainer {
            val data = this.base ?: DataContainer.createNew(DataViewSafetyMode.NO_DATA_CLONED)
            if (this.blockEntities != null)
                data.set(BLOCK_ENTITIES, this.blockEntities)
            if (this.entities != null)
                data.set(ENTITIES, this.entities)
            if (this.lightPopulated)
                data.set(LIGHT_POPULATED, 1.toByte())
            return data
        }
    }

    private fun fixLoadedData(data: DataContainer): ChunkGroupData? {
        val level = data.getView(LEVEL).orNull() ?: return null
        val sections = Array(AnvilRegionFile.REGION_Y_SIZE) { SectionData() }

        /*
         * The official chunk format groups some data that we need to be split into different
         * sections, because each section is a chunk, unlike in vanilla where the column is a chunk.
         */

        val sectionBaseViews = level.getViewList(SECTIONS).orElse(emptyList())
        for (sectionBaseView in sectionBaseViews) {
            val section = sectionBaseView.getInt(Y).get()
            sections[section].base = sectionBaseView.copy(DataViewSafetyMode.NO_DATA_CLONED)
        }

        val blockEntityViews = level.getViewList(TILE_ENTITIES).orElse(emptyList())
        for (blockEntityView in blockEntityViews) {
            val y = blockEntityView.getInt(BLOCK_ENTITY_Y).get()
            val section = y shr AnvilRegionFile.REGION_COORDINATE_Y_BITS
            blockEntityView.set(BLOCK_ENTITY_Y, y and AnvilRegionFile.REGION_Y_MASK)
            var blockEntities = sections[section].blockEntities
            if (blockEntities == null) {
                blockEntities = ArrayList()
                sections[section].blockEntities = blockEntities
            }
            blockEntities.add(blockEntityView)
        }

        val entityViews = level.getViewList(ENTITIES).orElse(emptyList())
        for (entityView in entityViews) {
            val position = entityView.getDoubleList(ENTITY_POSITION).get()
            val y = position[1]
            val section = (floor(y).toInt() shr AnvilRegionFile.REGION_COORDINATE_Y_BITS).coerceIn(sections.indices)
            var entities = sections[section].entities
            if (entities == null) {
                entities = ArrayList()
                sections[section].entities = entities
            }
            entities.add(entityView)
        }

        return AnvilChunkGroupData(sections.map(SectionData::build))
    }

    private class AnvilChunkGroupData(private val views: List<DataContainer>) : ChunkGroupData {
        override fun get(x: Int, y: Int, z: Int): DataContainer {
            check(x == 0 && z == 0)
            return this.views[y]
        }
    }

    private fun fixSavedData(position: ChunkGroupPosition, groupData: ChunkGroupData): DataContainer {
        @Suppress("NAME_SHADOWING")
        val data = DataContainer.createNew(DataViewSafetyMode.NO_DATA_CLONED)

        val level = data.createView(LEVEL)
        level.set(X_POS, position.x)
        level.set(Z_POS, position.z)

        val blockEntityViews = ArrayList<DataView>()
        val entityViews = ArrayList<DataView>()
        val sections = ArrayList<DataView>(AnvilRegionFile.REGION_Y_SIZE)

        for (y in 0 until AnvilRegionFile.REGION_Y_SIZE) {
            val sectionData = groupData[0, y, 0]

            sectionData.getViewList(ENTITIES).ifPresent { entities -> entityViews.addAll(entities) }
            sectionData.getViewList(BLOCK_ENTITIES).ifPresent { blockEntities ->
                for (blockEntity in blockEntities) {
                    val localY = blockEntity.getInt(BLOCK_ENTITY_Y).get()
                    blockEntity.set(BLOCK_ENTITY_Y, y shl AnvilRegionFile.REGION_COORDINATE_Y_BITS + localY)
                    blockEntityViews.add(blockEntity)
                }
            }
            sectionData.remove(BLOCK_ENTITIES)
            sectionData.remove(ENTITIES)
            sections += sectionData
        }

        level.set(BLOCK_ENTITIES, blockEntityViews)
        level.set(ENTITIES, entityViews)
        level.set(SECTIONS, sections)

        return data
    }

    fun close() {
        if (this.closed)
            return
        this.closed = true
        this.anvilRegionFileCache.clear()
    }

    companion object {

        private val LEVEL: DataQuery = DataQuery.of("Level")
        private val X_POS: DataQuery = DataQuery.of("xPos")
        private val Z_POS: DataQuery = DataQuery.of("zPos")
        private val Y: DataQuery = DataQuery.of("Y")
        private val TILE_ENTITIES: DataQuery = DataQuery.of("TileEntities")
        private val BLOCK_ENTITIES: DataQuery = DataQuery.of("BlockEntities")
        private val BLOCK_ENTITY_Y: DataQuery = DataQuery.of("y")
        private val ENTITIES: DataQuery = DataQuery.of("Entities")
        private val ENTITY_POSITION: DataQuery = DataQuery.of("Pos")
        private val SECTIONS: DataQuery = DataQuery.of("Sections")
        private val LIGHT_POPULATED: DataQuery = DataQuery.of("LightPopulated")
    }
}
