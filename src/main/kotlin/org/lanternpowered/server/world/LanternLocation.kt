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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.math.minus
import org.lanternpowered.api.util.math.plus
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.api.world.Location
import org.lanternpowered.api.world.World
import org.lanternpowered.server.block.LanternLocatableBlock
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.CollectionValue
import org.spongepowered.api.data.value.MapValue
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.fluid.FluidState
import org.spongepowered.api.fluid.FluidType
import org.spongepowered.api.scheduler.ScheduledUpdate
import org.spongepowered.api.scheduler.TaskPriority
import org.spongepowered.api.util.Direction
import org.spongepowered.api.world.BlockChangeFlag
import org.spongepowered.api.world.LocatableBlock
import org.spongepowered.api.world.ServerLocation
import org.spongepowered.api.world.biome.BiomeType
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3i
import java.time.Duration
import java.time.temporal.TemporalUnit
import java.util.Objects
import java.util.Optional
import java.util.function.BiFunction

fun Vector3i.toBiomePosition(): Vector3i = this.div(4).mul(4) // TODO: Move these
fun Vector3i.toChunkPosition(): Vector3i = this.div(16)

class LanternLocation : Location {

    object Factory : ServerLocation.Factory {

        override fun create(world: World, position: Vector3d): Location =
                LanternLocation(world, position)

        override fun create(world: World, blockPosition: Vector3i): Location =
                LanternLocation(world, blockPosition)

        override fun create(key: NamespacedKey, position: Vector3d): Location =
                LanternLocation(WeakWorldReference(key), position)

        override fun create(key: NamespacedKey, blockPosition: Vector3i): Location =
                LanternLocation(WeakWorldReference(key), blockPosition)
    }

    private val worldRef: WeakWorldReference

    private var lazyPosition: Vector3d? = null
    private var lazyBlockPosition: Vector3i? = null
    private var lazyBiomePosition: Vector3i? = null
    private var lazyChunkPosition: Vector3i? = null

    private var hashCode = 0

    constructor(world: World, position: Vector3d) : this(WeakWorldReference(world), position)

    constructor(world: World, position: Vector3i) : this(WeakWorldReference(world), position)

    constructor(world: WeakWorldReference, position: Vector3d) {
        this.worldRef = world
        this.lazyPosition = position
    }

    constructor(world: WeakWorldReference, position: Vector3i) {
        this.worldRef = world
        this.lazyBlockPosition = position
    }

    override fun getWorld(): World = this.worldRef.world ?: error("The world is unavailable.")
    override fun getWorldKey(): NamespacedKey = this.worldRef.key
    override fun getWorldIfAvailable(): Optional<World> = this.worldRef.world.asOptional()
    override fun inWorld(world: World): Boolean = this.world == world

    override fun asLocatableBlock(): LocatableBlock = LanternLocatableBlock(this, this.block)

    override fun withWorld(world: World): Location =
            if (this.worldRef.world == world) this
            else {
                val worldRef = WeakWorldReference(world)
                val blockPosition = this.lazyBlockPosition
                if (blockPosition != null) {
                    LanternLocation(worldRef, blockPosition)
                } else {
                    LanternLocation(worldRef, this.position)
                }
            }

    override fun isValid(): Boolean = true // TODO: Check world bounds

    override fun getX(): Double = this.position.x
    override fun getY(): Double = this.position.y
    override fun getZ(): Double = this.position.z

    override fun getBlockX(): Int = this.blockPosition.x
    override fun getBlockY(): Int = this.blockPosition.y
    override fun getBlockZ(): Int = this.blockPosition.z

    override fun isAvailable(): Boolean = this.worldRef.world != null

    override fun getPosition(): Vector3d =
            this.lazyPosition ?: this.lazyBlockPosition!!.toDouble().also { this.lazyPosition = it }

    override fun getBlockPosition(): Vector3i =
            this.lazyBlockPosition ?: this.lazyPosition!!.toInt().also { this.lazyBlockPosition = it }

    override fun getBiomePosition(): Vector3i =
            this.lazyBiomePosition ?: this.blockPosition.toBiomePosition().also { this.lazyBiomePosition = it }

    override fun getChunkPosition(): Vector3i =
            this.lazyChunkPosition ?: this.blockPosition.toChunkPosition().also { this.lazyChunkPosition = it }

    override fun withPosition(position: Vector3d): Location =
            if (position == this.lazyPosition) this else LanternLocation(this.worldRef, position)

    override fun withBlockPosition(position: Vector3i): Location =
            if (position == this.lazyBlockPosition) this else LanternLocation(this.worldRef, position)

    override fun asHighestLocation(): Location =
            this.withBlockPosition(this.world.getHighestPositionAt(this.blockPosition))

    override fun add(v: Vector3d): Location = LanternLocation(this.worldRef, this.position + v)
    override fun add(v: Vector3i): Location = LanternLocation(this.worldRef, this.position + v)
    override fun add(x: Double, y: Double, z: Double): Location = LanternLocation(this.worldRef, this.position.add(x, y, z))

    override fun sub(v: Vector3d): Location = LanternLocation(this.worldRef, this.position - v)
    override fun sub(v: Vector3i): Location = LanternLocation(this.worldRef, this.position - v)
    override fun sub(x: Double, y: Double, z: Double): Location = LanternLocation(this.worldRef, this.position.sub(x, y, z))

    override fun relativeToBlock(direction: Direction): Location {
        check(!direction.isSecondaryOrdinal) { "Secondary cardinal directions can't be used here" }
        return LanternLocation(this.worldRef, this.blockPosition + direction.asBlockOffset())
    }

    override fun relativeTo(direction: Direction): Location =
            this.add(direction.asOffset())

    override fun createEntity(type: EntityType<*>): Entity =
            this.world.createEntity(type, this.position)

    override fun spawnEntity(entity: Entity): Boolean {
        entity.position = this.position
        return this.world.spawnEntity(entity)
    }

    override fun spawnEntities(entities: Iterable<Entity>): Collection<Entity> =
            entities.asSequence().filter(this::spawnEntity).toImmutableSet()

    override fun getBlock(): BlockState =
            this.world.getBlock(this.blockPosition)

    override fun setBlockType(type: BlockType): Boolean =
            this.world.setBlock(this.blockPosition, type.defaultState)

    override fun setBlockType(type: BlockType, flag: BlockChangeFlag): Boolean =
            this.world.setBlock(this.blockPosition, type.defaultState, flag)

    override fun setBlock(state: BlockState): Boolean =
            this.world.setBlock(this.blockPosition, state)

    override fun setBlock(state: BlockState, flag: BlockChangeFlag): Boolean =
            this.world.setBlock(this.blockPosition, state, flag)

    override fun removeBlock(): Boolean =
            this.world.removeBlock(this.blockPosition)

    override fun hasBlock(): Boolean =
            this.world.hasBlockState(this.blockPosition)

    override fun hasBlockEntity(): Boolean =
            this.world.getBlockEntity(this.blockPosition).isPresent

    override fun getBlockEntity(): Optional<BlockEntity> =
            this.world.getBlockEntity(this.blockPosition).uncheckedCast()

    override fun getFluid(): FluidState =
            this.world.getFluid(this.blockPosition)

    override fun getBiome(): BiomeType =
            this.world.getBiome(this.blockPosition)

    override fun getScheduledBlockUpdates(): Collection<ScheduledUpdate<BlockType>> =
            this.world.scheduledBlockUpdates.getScheduledAt(this.blockPosition)

    override fun scheduleBlockUpdate(delay: Int, temporalUnit: TemporalUnit): ScheduledUpdate<BlockType> =
            this.world.scheduledBlockUpdates.schedule(this.blockPosition, this.block.type, delay, temporalUnit)

    override fun scheduleBlockUpdate(delay: Int, temporalUnit: TemporalUnit, priority: TaskPriority): ScheduledUpdate<BlockType> =
            this.world.scheduledBlockUpdates.schedule(this.blockPosition, this.block.type, delay, temporalUnit, priority)

    override fun scheduleBlockUpdate(delay: Duration): ScheduledUpdate<BlockType> =
            this.world.scheduledBlockUpdates.schedule(this.blockPosition, this.block.type, delay)

    override fun scheduleBlockUpdate(delay: Duration, priority: TaskPriority): ScheduledUpdate<BlockType> =
            this.world.scheduledBlockUpdates.schedule(this.blockPosition, this.block.type, delay, priority)

    override fun getScheduledFluidUpdates(): Collection<ScheduledUpdate<FluidType>> =
            this.world.scheduledFluidUpdates.getScheduledAt(this.blockPosition)

    override fun scheduleFluidUpdate(delay: Int, temporalUnit: TemporalUnit): ScheduledUpdate<FluidType> =
            this.world.scheduledFluidUpdates.schedule(this.blockPosition, this.fluid.type, delay, temporalUnit)

    override fun scheduleFluidUpdate(delay: Int, temporalUnit: TemporalUnit, priority: TaskPriority): ScheduledUpdate<FluidType> =
            this.world.scheduledFluidUpdates.schedule(this.blockPosition, this.fluid.type, delay, temporalUnit, priority)

    override fun scheduleFluidUpdate(delay: Duration): ScheduledUpdate<FluidType> =
            this.world.scheduledFluidUpdates.schedule(this.blockPosition, this.fluid.type, delay)

    override fun scheduleFluidUpdate(delay: Duration, priority: TaskPriority): ScheduledUpdate<FluidType> =
            this.world.scheduledFluidUpdates.schedule(this.blockPosition, this.fluid.type, delay, priority)

    override fun <T : Any> map(mapper: BiFunction<World, Vector3d, T>): T =
            mapper.apply(this.world, this.position)

    override fun <T : Any> mapChunk(mapper: BiFunction<World, Vector3i, T>): T =
            mapper.apply(this.world, this.chunkPosition)

    override fun <T : Any> mapBlock(mapper: BiFunction<World, Vector3i, T>): T =
            mapper.apply(this.world, this.blockPosition)

    override fun <T : Any> mapBiome(mapper: BiFunction<World, Vector3i, T>): T =
            mapper.apply(this.world, this.biomePosition)

    override fun <E : Any> offerSingle(key: Key<out CollectionValue<E, *>>, element: E): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun <K : Any, V : Any> offerSingle(key: Key<out MapValue<K, V>>, valueKey: K, value: V): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun <E : Any> tryOffer(key: Key<out Value<E>>, value: E): DataTransactionResult {
        val result = this.world.offer(this.blockPosition, key, value)
        if (!result.isSuccessful) {
            throw IllegalArgumentException("Failed offer transaction!")
        }
        return result
    }

    override fun <E : Any> removeSingle(key: Key<out CollectionValue<E, *>>, element: E): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun <E : Any> offer(key: Key<out Value<E>>, value: E): DataTransactionResult =
            this.world.offer(this.blockPosition, key, value)

    override fun offer(value: Value<*>): DataTransactionResult =
            this.world.offer(this.blockPosition, value)

    override fun <K : Any, V : Any> offerAll(key: Key<out MapValue<K, V>>, map: Map<out K, V>): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun offerAll(value: MapValue<*, *>): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun offerAll(value: CollectionValue<*, *>): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun <E : Any> offerAll(key: Key<out CollectionValue<E, *>>, elements: Collection<E>): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun supports(key: Key<*>): Boolean =
            this.world.supports(this.blockPosition, key)

    override fun undo(result: DataTransactionResult): DataTransactionResult =
            this.world.undo(this.blockPosition, result)

    override fun removeAll(value: CollectionValue<*, *>): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun <E : Any> removeAll(key: Key<out CollectionValue<E, *>>, elements: Collection<E>): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun removeAll(value: MapValue<*, *>): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun <K : Any, V : Any> removeAll(key: Key<out MapValue<K, V>>, map: Map<out K, V>): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun <E : Any, V : Value<E>> getValue(key: Key<V>): Optional<V> =
            this.world.getValue(this.blockPosition, key)

    override fun getKeys(): Set<Key<*>> =
            this.world.getKeys(this.blockPosition)

    override fun getValues(): Set<Value.Immutable<*>> =
            this.world.getValues(this.blockPosition)

    override fun remove(key: Key<*>): DataTransactionResult =
            this.world.remove(this.blockPosition, key)

    override fun <K : Any> removeKey(key: Key<out MapValue<K, *>>, mapKey: K): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun <E : Any> get(key: Key<out Value<E>>): Optional<E> =
            this.world.get(this.blockPosition, key)

    override fun <E : Any> get(direction: Direction, key: Key<out Value<E>>): Optional<E> {
        TODO("Not yet implemented")
    }

    override fun copyFrom(that: ValueContainer, function: MergeFunction): DataTransactionResult =
            this.world.copyFrom(this.blockPosition, that, function)

    override fun createSnapshot(): BlockSnapshot =
            this.world.createSnapshot(this.blockPosition)

    override fun restoreSnapshot(snapshot: BlockSnapshot, force: Boolean, flag: BlockChangeFlag): Boolean =
            this.world.restoreSnapshot(snapshot.withLocation(this), force, flag)

    override fun equals(other: Any?): Boolean {
        return other is LanternLocation &&
                other.worldRef == this.worldRef &&
                other.position == this.position
    }

    override fun hashCode(): Int {
        var hashCode = this.hashCode
        if (hashCode == 0) {
            hashCode = Objects.hash(this.worldRef, this.position).also { this.hashCode = it }
        }
        return hashCode
    }
}
