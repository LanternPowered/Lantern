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
package org.lanternpowered.server.world.chunk

import org.lanternpowered.api.block.BlockState
import org.lanternpowered.api.block.BlockType
import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.math.ceilToInt
import org.lanternpowered.api.util.math.floorToInt
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.chunk.ChunkColumn
import org.lanternpowered.api.world.chunk.ChunkColumnPosition
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.world.LanternChunk
import org.lanternpowered.server.world.inBox
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer
import org.spongepowered.api.fluid.FluidState
import org.spongepowered.api.fluid.FluidType
import org.spongepowered.api.scheduler.ScheduledUpdateList
import org.spongepowered.api.util.AABB
import org.spongepowered.api.util.PositionOutOfBoundsException
import org.spongepowered.api.world.HeightType
import org.spongepowered.api.world.biome.BiomeType
import org.spongepowered.api.world.volume.game.LocationBaseDataHolder
import org.spongepowered.math.vector.Vector2i
import org.spongepowered.math.vector.Vector3i
import java.util.Optional
import java.util.UUID
import java.util.function.Predicate

class LanternChunkColumn(
        override val world: World,
        override val position: ChunkColumnPosition
) : ChunkColumn, LocationBaseDataHolder.Mutable {

    companion object {

        const val IntersectionBoxMargin = 2.0
    }

    private val blockMin = Chunks.toGlobal(this.position.x, Chunks.MinY, this.position.z)
    private val blockMax = Chunks.toGlobal(this.position.x + 1, Chunks.MaxY + 1, this.position.z + 1).sub(1, 1, 1)

    fun getChunkAt(y: Int): LanternChunk = this.getChunk(Chunks.toChunk(y))

    fun getChunk(y: Int): LanternChunk {
        TODO()
    }

    private fun checkBounds(x: Int, z: Int) {
        if (Chunks.toChunkColumn(x, z) != this.position)
            throw PositionOutOfBoundsException(Vector2i(x, z), this.blockMin.toVector2(true), this.blockMax.toVector2(true))
    }

    private fun checkBounds(x: Int, y: Int, z: Int) {
        if (!this.containsBlock(x, y, z))
            throw PositionOutOfBoundsException(Vector3i(x, y, z), this.blockMin, this.blockMax)
    }

    override fun containsBlock(x: Int, y: Int, z: Int): Boolean =
            Chunks.toChunkColumn(x, z) == this.position && y in this.blockMin.y..this.blockMax.y

    override fun isAreaAvailable(x: Int, y: Int, z: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBlockSize(): Vector3i = LanternChunkLayout.chunkColumnSize
    override fun getBlockMin(): Vector3i = this.blockMin
    override fun getBlockMax(): Vector3i = this.blockMax

    override fun removeBlock(x: Int, y: Int, z: Int): Boolean {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).removeBlockLocally(Chunks.toLocal(x, y, z))
    }

    override fun getFluid(x: Int, y: Int, z: Int): FluidState {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).getFluidLocally(Chunks.toLocal(x, y, z))
    }

    override fun setRawData(x: Int, y: Int, z: Int, container: DataView) {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).setRawDataLocally(Chunks.toLocal(x, y, z), container)
    }

    override fun getKeys(x: Int, y: Int, z: Int): Set<Key<*>> {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).getKeysLocally(Chunks.toLocal(x, y, z))
    }

    override fun remove(x: Int, y: Int, z: Int, key: Key<*>): DataTransactionResult {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).removeLocally(Chunks.toLocal(x, y, z), key)
    }

    override fun <E : Any> get(x: Int, y: Int, z: Int, key: Key<out Value<E>>): Optional<E> {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).getLocally(Chunks.toLocal(x, y, z), key)
    }

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, from: ValueContainer): DataTransactionResult =
            this.copyFrom(xTo, yTo, zTo, from, MergeFunction.REPLACEMENT_PREFERRED)

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, from: ValueContainer, function: MergeFunction): DataTransactionResult {
        this.checkBounds(xTo, yTo, zTo)
        return this.getChunkAt(yTo).copyFromLocally(Chunks.toLocal(xTo, yTo, zTo), from, function)
    }

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, xFrom: Int, yFrom: Int, zFrom: Int, function: MergeFunction): DataTransactionResult {
        this.checkBounds(xTo, yTo, zTo)
        this.checkBounds(xFrom, yFrom, zFrom)
        return this.getChunkAt(yTo).copyFromLocally(Chunks.toLocal(xTo, yTo, zTo), Chunks.toLocal(xFrom, yFrom, zFrom), function)
    }

    override fun validateRawData(x: Int, y: Int, z: Int, container: DataView): Boolean {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).validateRawDataLocally(Chunks.toLocal(x, y, z), container)
    }

    override fun <E : Any, V : Value<E>> getValue(x: Int, y: Int, z: Int, key: Key<V>): Optional<V> {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).getValueLocally(Chunks.toLocal(x, y, z), key)
    }

    override fun getValues(x: Int, y: Int, z: Int): Set<Value.Immutable<*>> {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).getValuesLocally(Chunks.toLocal(x, y, z))
    }

    override fun <E : Any> offer(x: Int, y: Int, z: Int, key: Key<out Value<E>>, value: E): DataTransactionResult {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).offerLocally(Chunks.toLocal(x, y, z), key, value)
    }

    override fun undo(x: Int, y: Int, z: Int, result: DataTransactionResult): DataTransactionResult {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).undoLocally(Chunks.toLocal(x, y, z), result)
    }

    override fun supports(x: Int, y: Int, z: Int, key: Key<*>): Boolean {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).supportsLocally(Chunks.toLocal(x, y, z), key)
    }

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockState): Boolean {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).setBlockLocally(Chunks.toLocal(x, y, z), block)
    }

    override fun getBlock(x: Int, y: Int, z: Int): BlockState {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).getBlockLocally(Chunks.toLocal(x, y, z))
    }

    override fun getBlockEntity(x: Int, y: Int, z: Int): Optional<out BlockEntity> {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).getBlockEntityLocally(Chunks.toLocal(x, y, z)).asOptional()
    }

    override fun removeBlockEntity(x: Int, y: Int, z: Int) {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).removeBlockEntityLocally(Chunks.toLocal(x, y, z))
    }

    override fun addBlockEntity(x: Int, y: Int, z: Int, blockEntity: BlockEntity) {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).addBlockEntityLocally(Chunks.toLocal(x, y, z), blockEntity)
    }

    override fun setBiome(x: Int, y: Int, z: Int, biome: BiomeType): Boolean {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).setBiomeLocally(Chunks.toLocal(x, y, z), biome)
    }

    override fun getBiome(x: Int, y: Int, z: Int): BiomeType {
        this.checkBounds(x, y, z)
        return this.getChunkAt(y).getBiomeLocally(Chunks.toLocal(x, y, z))
    }

    override fun getBlockEntities(): Collection<BlockEntity> {
        // This can only return loaded block entities
        TODO("Not yet implemented")
    }

    /**
     * Gets a sequence of chunks between the given min and max y chunk coordinates.
     */
    private fun chunkSequenceBetween(minY: Int, maxY: Int): Sequence<LanternChunk> =
            (minY..maxY).asSequence().map { y -> this.getChunkAt(y) }

    override fun <T : Entity> getEntities(entityClass: Class<out T>, box: AABB, predicate: Predicate<in T>?): Collection<T> {
        val sequence = this.entitySequence(box).filterIsInstance(entityClass)
        if (predicate != null)
            return sequence.filter { predicate.test(it) }.toImmutableSet()
        return sequence.toImmutableSet()
    }

    override fun getEntities(box: AABB, filter: Predicate<in Entity>?): Collection<Entity> {
        val sequence = this.entitySequence(box)
        if (filter != null)
            return sequence.filter { filter.test(it) }.toImmutableSet()
        return sequence.toImmutableSet()
    }

    fun entitySequence(box: AABB): Sequence<Entity> {
        val minY = (box.min.y - IntersectionBoxMargin).floorToInt().coerceIn(Chunks.RangeY)
        val maxY = (box.max.y + IntersectionBoxMargin).ceilToInt().coerceIn(Chunks.RangeY)
        return this.chunkSequenceBetween(minY, maxY)
                .flatMap { chunk -> chunk.entities.asSequence().inBox(box) }
    }

    override fun getEntity(uuid: UUID): Optional<Entity> {
        val optional = this.world.getEntity(uuid)
        if (optional.isPresent) {
            val entity = optional.get() as LanternEntity
            if (entity.chunkPosition.column == this.position)
                return optional
        }
        return emptyOptional()
    }

    override fun getPlayers(): Collection<Player> {
        TODO("Not yet implemented")
    }

    override fun getScheduledBlockUpdates(): ScheduledUpdateList<BlockType> {
        TODO("Not yet implemented")
    }

    override fun getScheduledFluidUpdates(): ScheduledUpdateList<FluidType> {
        TODO("Not yet implemented")
    }

    override fun getHighestYAt(x: Int, z: Int): Int {
        this.checkBounds(x, z)
        return this.getHighestYAtLocally(Chunks.toLocal(x), Chunks.toLocal(z))
    }

    fun getHighestYAtLocally(x: Int, z: Int): Int {
        TODO()
    }

    override fun getHeight(type: HeightType, x: Int, z: Int): Int {
        this.checkBounds(x, z)
        return this.getHeightAtLocally(type, Chunks.toLocal(x), Chunks.toLocal(z))
    }

    fun getHeightAtLocally(type: HeightType, x: Int, z: Int): Int {
        TODO("Not yet implemented")
    }
}
