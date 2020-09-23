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

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import org.lanternpowered.api.block.BlockState
import org.lanternpowered.api.block.BlockType
import org.lanternpowered.api.data.eq
import org.lanternpowered.api.data.neq
import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.util.collections.asUnmodifiableSet
import org.lanternpowered.api.util.collections.concurrentHashSetOf
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.util.palette.PaletteBasedArray
import org.lanternpowered.api.util.palette.PaletteBasedArrayFactory
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.api.world.BlockChangeFlag
import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.chunk.Chunk
import org.lanternpowered.api.world.chunk.ChunkPosition
import org.lanternpowered.api.world.locationOf
import org.lanternpowered.server.block.LanternBlockState
import org.lanternpowered.server.block.LanternBlockType
import org.lanternpowered.server.block.entity.LanternBlockEntity
import org.lanternpowered.server.data.mergeWithAndCollectResult
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.registry.type.block.GlobalBlockStatePalette
import org.lanternpowered.server.util.collect.array.NibbleArray
import org.lanternpowered.server.world.chunk.ChunkManager
import org.lanternpowered.server.world.chunk.Chunks
import org.lanternpowered.server.world.chunk.LanternChunkLayout
import org.lanternpowered.server.world.chunk.LocalPosition
import org.lanternpowered.server.world.update.ChunkScheduledUpdateList
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.lanternpowered.api.data.Keys
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer
import org.spongepowered.api.fluid.FluidState
import org.spongepowered.api.fluid.FluidType
import org.spongepowered.api.scheduler.ScheduledUpdateList
import org.spongepowered.api.util.AABB
import org.spongepowered.api.util.PositionOutOfBoundsException
import org.spongepowered.api.world.BlockChangeFlags
import org.spongepowered.api.world.HeightType
import org.spongepowered.api.world.biome.BiomeType
import org.spongepowered.api.world.chunk.ChunkState
import org.spongepowered.api.world.volume.game.LocationBaseDataHolder
import org.spongepowered.math.vector.Vector2i
import org.spongepowered.math.vector.Vector3i
import java.util.Optional
import java.util.UUID
import java.util.function.Predicate

class LanternChunk(
        val manager: ChunkManager,
        val position: ChunkPosition
) : Chunk, LocationBaseDataHolder.Mutable {

    companion object {

        private val NoTracker = UUID(0, 0)

        private const val MinLight = 0
        private const val MaxLight = 15
    }

    var region: WorldRegion? = null

    private val blockMin = Chunks.toGlobal(this.position)
    private val blockMax = this.blockMin.add(LanternChunkLayout.chunkSize).sub(1, 1, 1)

    override fun getWorld(): World = this.manager.world
    override fun getChunkPosition(): Vector3i = this.position.toVector()

    override fun getBlockMin(): Vector3i = this.blockMin
    override fun getBlockMax(): Vector3i = this.blockMax
    override fun getBlockSize(): Vector3i = LanternChunkLayout.chunkSize

    /**
     * The array with all the sky light of this chunk, or `null` if
     * this chunk doesn't have any blocks and there are no solid blocks
     * above this chunk.
     */
    private var skylight: NibbleArray? = null

    /**
     * The array with all the block states of this chunk, or `null` if
     * this chunk doesn't have any blocks.
     */
    private var blocks: PaletteBasedArray<BlockState>? = null

    /**
     * An array with all the known trackers in this chunk, can be `null` if
     * there are no notifiers.
     */
    private var notifiers: PaletteBasedArray<UUID>? = null

    /**
     * An array with all the known trackers in this chunk, can be `null` if
     * there are no creators.
     */
    private var creators: PaletteBasedArray<UUID>? = null

    /**
     * The array with all the block light of this chunk, or `null` if
     * this chunk doesn't have any blocks.
     */
    private var blockLight: NibbleArray? = null

    /**
     * The array with all the block entities of this chunk, or `null` if
     * this chunk doesn't have any blocks.
     */
    private var blockEntities: Short2ObjectMap<LanternBlockEntity>? = null

    /**
     * The number of non empty (air with index 0, no cave/void air) blocks in this chunk section.
     */
    private var nonEmptyBlockCount = 0

    /**
     * The number of non air blocks in this chunk.
     */
    private var nonAirBlockCount = 0

    /**
     * A set with all the [Entity]s that are currently located in this chunk.
     */
    private var _entities = concurrentHashSetOf<Entity>()

    /**
     * All the scheduled block updates in this chunk.
     */
    private var scheduledBlockUpdates = ChunkScheduledUpdateList<BlockType>(this.world, this.position)

    /**
     * All the scheduled fluid updates in this chunk.
     */
    private var scheduledFluidUpdates = ChunkScheduledUpdateList<FluidType>(this.world, this.position)

    /**
     * An unmodifiable collection view of all the entities that are currently
     * located in this chunk.
     */
    val entities: Collection<Entity> = this._entities.asUnmodifiableSet()

    override fun getPlayers(): Collection<Player> =
            this._entities.asSequence().filterIsInstance<Player>().toImmutableSet()

    /**
     * Recounts the number of non empty and non air blocks.
     */
    private fun countBlocks() {
        this.nonEmptyBlockCount = 0
        this.nonAirBlockCount = 0
        val blocks = this.blocks ?: return
        for (i in 0 until blocks.size) {
            val type = blocks[i].type as LanternBlockType
            if (type neq BlockTypes.AIR)
                this.nonEmptyBlockCount++
            if (!type.isAir)
                this.nonAirBlockCount++
        }
    }

    private fun checkBounds(x: Int, y: Int, z: Int) {
        if (!this.containsBlock(x, y, z))
            throw PositionOutOfBoundsException(Vector3i(x, y, z), this.blockMin, this.blockMax)
    }

    private fun checkBounds(x: Int, z: Int) {
        if (Chunks.toChunkColumn(x, z) != this.position.column)
            throw PositionOutOfBoundsException(Vector2i(x, z), this.blockMin.toVector2(true), this.blockMax.toVector2(true))
    }

    override fun containsBlock(x: Int, y: Int, z: Int): Boolean = Chunks.toChunk(x, y, z) == this.position
    override fun isAreaAvailable(x: Int, y: Int, z: Int): Boolean = this.containsBlock(x, y, z) // TODO: This is probably not good

    override fun getScheduledBlockUpdates(): ScheduledUpdateList<BlockType> = this.scheduledBlockUpdates
    override fun getScheduledFluidUpdates(): ScheduledUpdateList<FluidType> = this.scheduledFluidUpdates

    override fun getBlockEntities(): Collection<BlockEntity> {
        val blockEntities = this.blockEntities ?: return emptyList()
        return blockEntities.values.toImmutableSet()
    }

    override fun getBlockEntity(x: Int, y: Int, z: Int): Optional<out BlockEntity> {
        this.checkBounds(x, y, z)
        return this.getBlockEntityLocally(Chunks.toLocal(x, y, z)).asOptional()
    }

    fun getBlockEntityLocally(position: LocalPosition): LanternBlockEntity? {
        val blockEntities = this.blockEntities ?: return null
        return blockEntities[position.packedShort]
    }

    override fun removeBlockEntity(x: Int, y: Int, z: Int) {
        this.checkBounds(x, y, z)
        this.removeBlockEntityLocally(Chunks.toLocal(x, y, z))
    }

    fun removeBlockEntityLocally(position: LocalPosition) {
        val blockEntity = this.blockEntities?.remove(position.packedShort) ?: return
        blockEntity.isValid = true
    }

    override fun addBlockEntity(x: Int, y: Int, z: Int, blockEntity: BlockEntity) {
        this.checkBounds(x, y, z)
        this.addBlockEntityLocally(Chunks.toLocal(x, y, z), blockEntity)
    }

    fun addBlockEntityLocally(position: LocalPosition, blockEntity: BlockEntity) {
        val previous = this.blockEntities?.get(position.packedShort)
        if (previous != null)
            previous.isValid = false
        this.putBlockEntity(position, blockEntity)
    }

    private fun putBlockEntity(position: LocalPosition, blockEntity: BlockEntity) {
        var blockEntities = this.blockEntities
        if (blockEntities == null)
            blockEntities = Short2ObjectOpenHashMap<LanternBlockEntity>().also { this.blockEntities = it }

        blockEntity as LanternBlockEntity
        blockEntity.setLocation(locationOf(this.world, Chunks.toGlobal(this.position, position)))
        blockEntity.block = this.getBlockLocally(position)
        blockEntity.isValid = true

        blockEntities[position.packedShort] = blockEntity
    }

    override fun loadChunk(generate: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeBlock(x: Int, y: Int, z: Int): Boolean {
        this.checkBounds(x, y, z)
        return this.removeBlockLocally(Chunks.toLocal(x, y, z))
    }

    fun removeBlockLocally(position: LocalPosition): Boolean =
            this.setBlockLocally(position, BlockTypes.AIR.get().defaultState)

    override fun setBlock(x: Int, y: Int, z: Int, block: BlockState): Boolean {
        this.checkBounds(x, y, z)
        return this.setBlockLocally(Chunks.toLocal(x, y, z), block)
    }

    fun setBlockLocally(position: LocalPosition, block: BlockState): Boolean =
            this.setBlockLocally(position, block, BlockChangeFlags.ALL)

    fun setBlockLocally(position: LocalPosition, block: BlockState, flag: BlockChangeFlag): Boolean {
        block as LanternBlockState
        if (block == GlobalBlockStatePalette.default && this.blocks == null)
            return true

        var blocks = this.blocks
        if (blocks == null)
            blocks = PaletteBasedArrayFactory.of(Chunks.Volume, GlobalBlockStatePalette).also { this.blocks = it }

        val previous = blocks[position.packed] as LanternBlockState
        // The block didn't change, no need to update anything
        if (previous == block)
            return true
        blocks[position.packed] = block

        // Update block counts
        if (previous.type neq BlockTypes.AIR)
            this.nonEmptyBlockCount--
        if (block.type neq BlockTypes.AIR)
            this.nonEmptyBlockCount++
        if (!previous.type.isAir)
            this.nonAirBlockCount--
        if (!block.type.isAir)
            this.nonAirBlockCount++

        // Update the block entity
        var removeBlockEntity = false
        val previousBlockEntity = this.getBlockEntityLocally(position)
        if (previousBlockEntity != null) {
            if (block.type != previous.type) {
                previousBlockEntity.isValid = false
                removeBlockEntity = true
            } else {
                previousBlockEntity.block = block
            }
        }
        val blockEntityType = block.type.blockEntityType
        if (blockEntityType != null) {
            this.putBlockEntity(position, blockEntityType.constructBlockEntity())
            removeBlockEntity = false
        }
        if (removeBlockEntity)
            this.blockEntities?.remove(position.packedShort)

        // TODO: Notify neighbors, etc.
        // TODO: Update heightmap
        return true
    }

    override fun getBlock(x: Int, y: Int, z: Int): BlockState {
        this.checkBounds(x, y, z)
        return this.getBlockLocally(Chunks.toLocal(x, y, z))
    }

    fun getBlockLocally(position: LocalPosition): BlockState {
        val blocks = this.blocks
                ?: return GlobalBlockStatePalette.default
        return blocks[position.packed]
    }

    override fun getBiome(x: Int, y: Int, z: Int): BiomeType {
        this.checkBounds(x, y, z)
        return this.getBiomeLocally(Chunks.toLocal(x, y, z))
    }

    fun getBiomeLocally(position: LocalPosition): BiomeType {
        TODO("Not yet implemented")
    }

    override fun setBiome(x: Int, y: Int, z: Int, biome: BiomeType): Boolean {
        this.checkBounds(x, y, z)
        return this.setBiomeLocally(Chunks.toLocal(x, y, z), biome)
    }

    fun setBiomeLocally(position: LocalPosition, biome: BiomeType): Boolean {
        TODO("Not yet implemented")
    }

    override fun getFluid(x: Int, y: Int, z: Int): FluidState {
        this.checkBounds(x, y, z)
        return this.getFluidLocally(Chunks.toLocal(x, y, z))
    }

    fun getFluidLocally(position: LocalPosition): FluidState =
            this.getBlockLocally(position).fluidState

    @Deprecated(message = "Use on world or column.")
    override fun getHighestYAt(x: Int, z: Int): Int {
        /*
         * This method should never be used on chunk level, because it only represents
         * the height of a single chunk (16x16x16), and not a complete column.
         */
        this.checkBounds(x, z)
        val localX = Chunks.toLocal(x)
        val localZ = Chunks.toLocal(z)
        for (localY in Chunks.Size - 1 downTo 0) {
            val state = this.getBlockLocally(LocalPosition(localX, localY, localZ))
            if (state != BlockTypes.AIR.get())
                return Chunks.toGlobal(this.position.y, localY)
        }
        return Chunks.toGlobal(this.position.y)
    }

    @Deprecated(message = "Use on world or column level.")
    override fun getHeight(type: HeightType, x: Int, z: Int): Int {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean = false

    override fun getEntity(uuid: UUID): Optional<Entity> {
        val optional = this.world.getEntity(uuid)
        if (optional.isPresent) {
            val entity = optional.get() as LanternEntity
            if (entity.chunkPosition == this.position)
                return optional
        }
        return emptyOptional()
    }

    override fun addEntity(entity: Entity) {
        // Do nothing?
    }

    override fun unloadChunk(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getState(): ChunkState {
        TODO("Not yet implemented")
    }

    override fun setRawData(x: Int, y: Int, z: Int, container: DataView) {
        this.checkBounds(x, y, z)
        this.setRawDataLocally(Chunks.toLocal(x, y, z), container)
    }

    fun setRawDataLocally(position: LocalPosition, container: DataView) {
        TODO("Not yet implemented")
    }

    override fun getKeys(x: Int, y: Int, z: Int): Set<Key<*>> {
        this.checkBounds(x, y, z)
        return this.getKeysLocally(Chunks.toLocal(x, y, z))
    }

    fun getKeysLocally(position: LocalPosition): Set<Key<*>> {
        val keys = this.getBlockLocally(position).keys.toMutableSet()
        keys.add(Keys.SKY_LIGHT.get())
        keys.add(Keys.BLOCK_LIGHT.get())

        if (this.getCreatorLocally(position) != null)
            keys.add(Keys.CREATOR.get())
        if (this.getNotifierLocally(position) != null)
            keys.add(Keys.NOTIFIER.get())

        val blockEntity = this.getBlockEntityLocally(position)
        if (blockEntity != null)
            keys.addAll(blockEntity.keys)

        return keys.toImmutableSet()
    }

    override fun <E : Any> get(x: Int, y: Int, z: Int, key: Key<out Value<E>>): Optional<E> {
        this.checkBounds(x, y, z)
        return this.getLocally(Chunks.toLocal(x, y, z), key)
    }

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, from: ValueContainer): DataTransactionResult =
            this.copyFrom(xTo, yTo, zTo, from, MergeFunction.REPLACEMENT_PREFERRED)

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, from: ValueContainer, function: MergeFunction): DataTransactionResult {
        this.checkBounds(xTo, yTo, zTo)
        return this.copyFromLocally(Chunks.toLocal(xTo, yTo, zTo), from, function)
    }

    fun copyFromLocally(
            to: LocalPosition, from: ValueContainer, function: MergeFunction = MergeFunction.REPLACEMENT_PREFERRED
    ): DataTransactionResult {
        val previous = this.getBlockLocally(to)
        val result = DataTransactionResult.builder()
        val block = previous.mergeWithAndCollectResult(from, result)
        if (previous != block)
            this.setBlockLocally(to, block)
        val blockEntity = this.getBlockEntityLocally(to)
        if (blockEntity != null)
            result.absorbResult(blockEntity.copyFrom(from, function))
        from.get(Keys.CREATOR).ifPresent { value -> result.absorbResult(this.offerCreatorLocally(to, value, function)) }
        from.get(Keys.NOTIFIER).ifPresent { value -> result.absorbResult(this.offerNotifierLocally(to, value, function)) }
        return result.build()
    }

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, xFrom: Int, yFrom: Int, zFrom: Int, function: MergeFunction): DataTransactionResult {
        this.checkBounds(xTo, yTo, zTo)
        this.checkBounds(xFrom, yFrom, zFrom)
        return this.copyFromLocally(Chunks.toLocal(xTo, yTo, zTo), Chunks.toLocal(xFrom, yFrom, zFrom), function)
    }

    fun copyFromLocally(to: LocalPosition, from: LocalPosition, function: MergeFunction): DataTransactionResult {
        val previous = this.getBlockLocally(to)
        val fromBlock = this.getBlockLocally(from)
        val result = DataTransactionResult.builder()
        val block = previous.mergeWithAndCollectResult(fromBlock, function, result)
        if (previous != block)
            this.setBlockLocally(to, block)
        val blockEntity = this.getBlockEntityLocally(to)
        if (blockEntity != null) {
            val fromBlockEntity = this.getBlockEntityLocally(from)
            if (fromBlockEntity != null)
                result.absorbResult(blockEntity.copyFrom(fromBlockEntity, function))
        }
        this.getCreatorLocally(from).let { value -> result.absorbResult(this.offerCreatorLocally(to, value, function)) }
        this.getNotifierLocally(from).let { value -> result.absorbResult(this.offerNotifierLocally(to, value, function)) }
        return result.build()
    }

    override fun validateRawData(x: Int, y: Int, z: Int, container: DataView): Boolean {
        this.checkBounds(x, y, z)
        return this.validateRawDataLocally(Chunks.toLocal(x, y, z), container)
    }

    fun validateRawDataLocally(position: LocalPosition, container: DataView): Boolean {
        TODO("Not yet implemented")
    }

    fun <E : Any> getLocally(position: LocalPosition, key: Key<out Value<E>>): Optional<E> {
        if (key eq Keys.NOTIFIER)
            return this.getNotifierLocally(position).asOptional().uncheckedCast()
        if (key eq Keys.CREATOR)
            return this.getCreatorLocally(position).asOptional().uncheckedCast()
        if (key eq Keys.SKY_LIGHT)
            return this.getSkylightLocally(position).asOptional().uncheckedCast()
        if (key eq Keys.BLOCK_LIGHT)
            return this.getBlockLightLocally(position).asOptional().uncheckedCast()

        val blockEntity = this.getBlockEntityLocally(position)
        if (blockEntity != null) {
            val value = blockEntity.get(key)
            if (value.isPresent)
                return value
        }

        return this.getBlockLocally(position).get(key)
    }

    override fun <E : Any, V : Value<E>> getValue(x: Int, y: Int, z: Int, key: Key<V>): Optional<V> {
        this.checkBounds(x, y, z)
        return this.getValueLocally(Chunks.toLocal(x, y, z), key)
    }

    fun <E : Any, V : Value<E>> getValueLocally(position: LocalPosition, key: Key<V>): Optional<V> {
        if (key eq Keys.NOTIFIER)
            return this.getNotifierLocally(position)?.let { Value.immutableOf(Keys.NOTIFIER, it) }.asOptional().uncheckedCast()
        if (key eq Keys.CREATOR)
            return this.getCreatorLocally(position)?.let { Value.immutableOf(Keys.CREATOR, it) }.asOptional().uncheckedCast()
        if (key eq Keys.SKY_LIGHT)
            return Value.immutableOf(Keys.SKY_LIGHT, this.getSkylightLocally(position)).asOptional().uncheckedCast()
        if (key eq Keys.BLOCK_LIGHT)
            return Value.immutableOf(Keys.BLOCK_LIGHT, this.getBlockLightLocally(position)).asOptional().uncheckedCast()
        val blockEntity = this.getBlockEntityLocally(position)
        if (blockEntity != null) {
            val value = blockEntity.getValue(key)
            if (value.isPresent)
                return value
        }
        return this.getBlockLocally(position).getValue(key)
    }

    override fun <E : Any> offer(x: Int, y: Int, z: Int, key: Key<out Value<E>>, value: E): DataTransactionResult {
        this.checkBounds(x, y, z)
        return this.offerLocally(Chunks.toLocal(x, y, z), key, value)
    }

    fun <E : Any> offerLocally(position: LocalPosition, key: Key<out Value<E>>, value: E): DataTransactionResult {
        if (key eq Keys.CREATOR)
            return this.offerCreatorLocally(position, value.uncheckedCast())
        if (key eq Keys.NOTIFIER)
            return this.offerNotifierLocally(position, value.uncheckedCast())
        val blockEntity = this.getBlockEntityLocally(position)
        if (blockEntity != null) {
            val result = blockEntity.offer(key, value)
            if (result.isSuccessful)
                return result
        }
        val oldState = this.getBlockLocally(position)
        val newState = oldState.with(key, value).orNull()
                ?: return DataTransactionResult.failResult(Value.immutableOf(key, value))
        this.setBlockLocally(position, newState)
        val builder = DataTransactionResult.builder()
        oldState.getValue(key).ifPresent { builder.replace(it.asImmutable()) }
        builder.success(Value.immutableOf(key, value))
        return builder.result(DataTransactionResult.Type.SUCCESS).build()
    }

    private fun offerCreatorLocally(position: LocalPosition, uniqueId: UUID?, merge: MergeFunction? = null): DataTransactionResult =
            this.offerTrackerLocally(position, Keys.CREATOR.get(), uniqueId, this::getCreatorLocally, this::setCreatorLocally, merge)

    private fun offerNotifierLocally(position: LocalPosition, uniqueId: UUID?, merge: MergeFunction? = null): DataTransactionResult =
            this.offerTrackerLocally(position, Keys.NOTIFIER.get(), uniqueId, this::getNotifierLocally, this::setNotifierLocally, merge)

    private inline fun offerTrackerLocally(
            position: LocalPosition,
            key: Key<out Value<UUID>>,
            value: UUID?,
            getter: (LocalPosition) -> UUID?,
            setter: (LocalPosition, UUID?) -> Unit,
            merge: MergeFunction? = null
    ): DataTransactionResult {
        val old = getter(position)
        val oldValue = if (old == null) null else Value.immutableOf(key, old)
        var newValue = if (value == null) null else Value.immutableOf(key, value)
        if (merge != null)
            newValue = merge.merge(oldValue, newValue)
        setter(position, newValue?.get())
        val result = DataTransactionResult.builder()
        if (old != null)
            result.replace(oldValue)
        if (newValue != null)
            result.success(newValue)
        return result.result(DataTransactionResult.Type.SUCCESS).build()
    }

    override fun getValues(x: Int, y: Int, z: Int): Set<Value.Immutable<*>> {
        this.checkBounds(x, y, z)
        return this.getValuesLocally(Chunks.toLocal(x, y, z))
    }

    fun getValuesLocally(position: LocalPosition): Set<Value.Immutable<*>> {
        val values = this.getBlockLocally(position).values.toMutableSet()
        values.add(Value.immutableOf(Keys.SKY_LIGHT, this.getSkylightLocally(position)))
        values.add(Value.immutableOf(Keys.BLOCK_LIGHT, this.getBlockLightLocally(position)))

        val creator = this.getCreatorLocally(position)
        if (creator != null)
            values.add(Value.immutableOf(Keys.CREATOR, creator))
        val notifier = this.getNotifierLocally(position)
        if (notifier != null)
            values.add(Value.immutableOf(Keys.NOTIFIER, notifier))

        val blockEntity = this.getBlockEntityLocally(position)
        if (blockEntity != null)
            values.addAll(blockEntity.values)

        return values.asUnmodifiableSet()
    }

    override fun supports(x: Int, y: Int, z: Int, key: Key<*>): Boolean {
        this.checkBounds(x, y, z)
        return this.supportsLocally(Chunks.toLocal(x, y, z), key)
    }

    fun supportsLocally(position: LocalPosition, key: Key<*>): Boolean {
        if (key eq Keys.NOTIFIER ||
                key eq Keys.CREATOR ||
                key eq Keys.SKY_LIGHT ||
                key eq Keys.BLOCK_LIGHT)
            return true
        if (this.getBlockLocally(position).supports(key))
            return true
        return this.getBlockEntityLocally(position)?.supports(key) ?: false
    }

    override fun remove(x: Int, y: Int, z: Int, key: Key<*>): DataTransactionResult {
        this.checkBounds(x, y, z)
        return this.removeLocally(Chunks.toLocal(x, y, z), key)
    }

    fun removeLocally(position: LocalPosition, key: Key<*>): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun undo(x: Int, y: Int, z: Int, undo: DataTransactionResult): DataTransactionResult {
        this.checkBounds(x, y, z)
        return this.undoLocally(Chunks.toLocal(x, y, z), undo)
    }

    fun undoLocally(position: LocalPosition, undo: DataTransactionResult): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun getEntities(box: AABB, filter: Predicate<in Entity>?): Collection<Entity> {
        val sequence = this.entities.asSequence().inBox(box)
        if (filter != null)
            return sequence.filter { filter.test(it) }.toImmutableSet()
        return sequence.toImmutableSet()
    }

    override fun <T : Entity> getEntities(entityClass: Class<out T>, box: AABB, filter: Predicate<in T>?): Collection<T> {
        val sequence = this.entities.asSequence().filterIsInstance(entityClass).inBox(box)
        if (filter != null)
            return sequence.filter { filter.test(it) }.toImmutableSet()
        return sequence.toImmutableSet()
    }

    override fun setInhabitedTime(newInhabitedTime: Long) {
        TODO("Not yet implemented")
    }

    override fun getInhabitedTime(): Long {
        TODO("Not yet implemented")
    }

    override fun getRegionalDifficultyFactor(): Double {
        TODO("Not yet implemented")
    }

    override fun getRegionalDifficultyPercentage(): Double {
        TODO("Not yet implemented")
    }

    /**
     * Gets the skylight light at the given local position.
     */
    fun getSkylightLocally(position: LocalPosition): Int =
            this.skylight?.get(position.packed)?.toInt() ?: MaxLight

    /**
     * Gets the block light at the given local position.
     */
    fun getBlockLightLocally(position: LocalPosition): Int =
            this.blockLight?.get(position.packed)?.toInt() ?: MinLight

    /**
     * Gets the creator at the given local position.
     */
    fun getCreatorLocally(position: LocalPosition): UUID? =
            this.creators?.get(position.packed)

    /**
     * Sets the creator at the given local position.
     */
    fun setCreatorLocally(position: LocalPosition, uniqueId: UUID?) {
        var creators = this.creators
        if (uniqueId == null && creators == null)
            return
        if (creators == null)
            creators = PaletteBasedArrayFactory.of(Chunks.Volume) { NoTracker }.also { this.creators = it }
        creators[position.packed] = uniqueId ?: NoTracker
    }

    /**
     * Gets the creator at the given local position.
     */
    fun getNotifierLocally(position: LocalPosition): UUID? =
            this.notifiers?.get(position.packed)

    /**
     * Sets the notifier at the given local position.
     */
    fun setNotifierLocally(position: LocalPosition, uniqueId: UUID?) {
        var notifiers = this.notifiers
        if (uniqueId == null && notifiers == null)
            return
        if (notifiers == null)
            notifiers = PaletteBasedArrayFactory.of(Chunks.Volume) { NoTracker }.also { this.notifiers = it }
        notifiers[position.packed] = uniqueId ?: NoTracker
    }
}

/**
 * Filters the sequence of entities to only retain
 * entities that are in the given [box].
 */
fun <E : Entity> Sequence<E>.inBox(box: AABB): Sequence<E> = this.filter { entity ->
    if (entity.isRemoved)
        return@filter false
    val boundingBox = entity.boundingBox.orNull()
    if (boundingBox != null)
        return@filter boundingBox.intersects(box)
    box.contains(entity.position)
}
