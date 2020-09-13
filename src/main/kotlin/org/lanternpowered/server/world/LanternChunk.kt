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
package org.lanternpowered.server.world

import org.lanternpowered.api.world.chunk.Chunk
import org.lanternpowered.api.world.chunk.ChunkPosition
import org.lanternpowered.server.world.chunk.Chunks
import org.lanternpowered.server.world.chunk.LanternChunkLayout
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.fluid.FluidState
import org.spongepowered.api.fluid.FluidType
import org.spongepowered.api.scheduler.ScheduledUpdateList
import org.spongepowered.api.util.AABB
import org.spongepowered.api.world.HeightType
import org.spongepowered.api.world.World
import org.spongepowered.api.world.biome.BiomeType
import org.spongepowered.api.world.chunk.ChunkState
import org.spongepowered.math.vector.Vector3i
import java.util.Optional
import java.util.UUID
import java.util.function.Predicate

class LanternChunk(val position: ChunkPosition) : Chunk {

    var region: WorldRegion? = null

    private val blockMin = Chunks.toGlobal(this.position)
    private val blockMax = this.blockMin.add(LanternChunkLayout.chunkSize.sub(1, 1, 1))

    override fun getChunkPosition(): Vector3i = this.position.toVector()

    override fun getBlockMin(): Vector3i = this.blockMin
    override fun getBlockMax(): Vector3i = this.blockMax
    override fun getBlockSize(): Vector3i = LanternChunkLayout.chunkSize

    override fun setInhabitedTime(newInhabitedTime: Long) {
        TODO("Not yet implemented")
    }

    override fun isAreaAvailable(x: Int, y: Int, z: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getScheduledBlockUpdates(): ScheduledUpdateList<BlockType> {
        TODO("Not yet implemented")
    }

    override fun getBlockEntity(x: Int, y: Int, z: Int): Optional<out BlockEntity> {
        TODO("Not yet implemented")
    }

    override fun loadChunk(generate: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun getScheduledFluidUpdates(): ScheduledUpdateList<FluidType> {
        TODO("Not yet implemented")
    }

    override fun removeBlock(x: Int, y: Int, z: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBiome(x: Int, y: Int, z: Int): BiomeType {
        TODO("Not yet implemented")
    }

    override fun getFluid(x: Int, y: Int, z: Int): FluidState {
        TODO("Not yet implemented")
    }

    override fun getHeight(type: HeightType?, x: Int, z: Int): Int {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getInhabitedTime(): Long {
        TODO("Not yet implemented")
    }

    override fun getEntity(uuid: UUID?): Optional<Entity> {
        TODO("Not yet implemented")
    }

    override fun supports(x: Int, y: Int, z: Int, key: Key<*>?): Boolean {
        TODO("Not yet implemented")
    }

    override fun unloadChunk(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockState?): Boolean {
        TODO("Not yet implemented")
    }

    override fun addEntity(entity: Entity?) {
        TODO("Not yet implemented")
    }

    override fun getRegionalDifficultyPercentage(): Double {
        TODO("Not yet implemented")
    }

    override fun <E : Any?, V : Value<E>?> getValue(x: Int, y: Int, z: Int, key: Key<V>?): Optional<V> {
        TODO("Not yet implemented")
    }

    override fun getPlayers(): MutableCollection<out Player> {
        TODO("Not yet implemented")
    }

    override fun <E : Any?> get(x: Int, y: Int, z: Int, key: Key<out Value<E>>?): Optional<E> {
        TODO("Not yet implemented")
    }

    override fun getWorld(): World<*> {
        TODO("Not yet implemented")
    }

    override fun setBiome(x: Int, y: Int, z: Int, biome: BiomeType?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getState(): ChunkState {
        TODO("Not yet implemented")
    }

    override fun removeBlockEntity(x: Int, y: Int, z: Int) {
        TODO("Not yet implemented")
    }

    override fun getKeys(x: Int, y: Int, z: Int): MutableSet<Key<*>> {
        TODO("Not yet implemented")
    }

    override fun getEntities(box: AABB?, filter: Predicate<in Entity>?): MutableCollection<out Entity> {
        TODO("Not yet implemented")
    }

    override fun <T : Entity?> getEntities(entityClass: Class<out T>?, box: AABB?, predicate: Predicate<in T>?): MutableCollection<out T> {
        TODO("Not yet implemented")
    }

    override fun getRegionalDifficultyFactor(): Double {
        TODO("Not yet implemented")
    }

    override fun containsBlock(x: Int, y: Int, z: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun addBlockEntity(x: Int, y: Int, z: Int, blockEntity: BlockEntity?) {
        TODO("Not yet implemented")
    }

    override fun getValues(x: Int, y: Int, z: Int): MutableSet<Value.Immutable<*>> {
        TODO("Not yet implemented")
    }

    override fun getHighestYAt(x: Int, z: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getBlock(x: Int, y: Int, z: Int): BlockState {
        TODO("Not yet implemented")
    }

    override fun getBlockEntities(): MutableCollection<out BlockEntity> {
        TODO("Not yet implemented")
    }
}
