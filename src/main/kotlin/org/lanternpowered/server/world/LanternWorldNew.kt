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

import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.service.world.WorldStorage
import org.lanternpowered.api.world.Location
import org.lanternpowered.api.world.World
import org.lanternpowered.server.LanternServerNew
import org.lanternpowered.server.world.chunk.ChunkManager
import org.lanternpowered.server.world.storage.SpongeWorldStorage
import org.spongepowered.api.Server
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataTransactionResult
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.sound.SoundCategory
import org.spongepowered.api.effect.sound.SoundType
import org.spongepowered.api.effect.sound.music.MusicDisc
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.fluid.FluidState
import org.spongepowered.api.fluid.FluidType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.raid.Raid
import org.spongepowered.api.scheduler.ScheduledUpdateList
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.text.BookView
import org.spongepowered.api.text.title.Title
import org.spongepowered.api.util.AABB
import org.spongepowered.api.util.Direction
import org.spongepowered.api.world.BlockChangeFlag
import org.spongepowered.api.world.ChunkRegenerateFlag
import org.spongepowered.api.world.HeightType
import org.spongepowered.api.world.LightType
import org.spongepowered.api.world.WorldBorder
import org.spongepowered.api.world.biome.BiomeType
import org.spongepowered.api.world.chunk.Chunk
import org.spongepowered.api.world.dimension.Dimension
import org.spongepowered.api.world.explosion.Explosion
import org.spongepowered.api.world.volume.archetype.ArchetypeVolume
import org.spongepowered.api.world.weather.Weather
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3i
import java.nio.file.Path
import java.time.Duration
import java.util.Optional
import java.util.Random
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.function.Predicate
import java.util.function.Supplier

// TODO: Rename world
class LanternWorldNew(
        private val server: LanternServerNew,
        private val properties: LanternWorldProperties,
        private val storage: WorldStorage,
        private val ioExecutor: ExecutorService
) : World {

    private val chunkManager = ChunkManager(this, this.ioExecutor)

    /**
     * The manager of all the regions in this world.
     */
    val regionManager = WorldRegionManager(this)

    /**
     * Whether this world is currently loaded.
     */
    private var loaded: Boolean = true

    fun unload() {

    }

    private val random = Random()
    private val spongeWorldStorage = SpongeWorldStorage(this.ioExecutor, this.properties, this.storage)

    override fun isLoaded(): Boolean = this.loaded
    override fun getDirectory(): Path = this.storage.directory
    override fun getEngine(): Server = this.server
    override fun getWorldStorage(): SpongeWorldStorage = this.spongeWorldStorage
    override fun getRandom(): Random = this.random
    override fun getSeed(): Long = this.properties.seed
    override fun getProperties(): LanternWorldProperties = this.properties

    override fun getLocation(position: Vector3i): Location = LanternLocation(this, position)
    override fun getLocation(position: Vector3d): Location = LanternLocation(this, position)

    override fun isChunkLoaded(x: Int, y: Int, z: Int, allowEmpty: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAreaAvailable(x: Int, y: Int, z: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun interactBlockWith(x: Int, y: Int, z: Int, itemStack: ItemStack, side: Direction, profile: GameProfile): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBlockMin(): Vector3i {
        TODO("Not yet implemented")
    }

    override fun <E : Any> offer(x: Int, y: Int, z: Int, key: Key<out Value<E>>, value: E): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun spawnEntity(entity: Entity): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeBlock(x: Int, y: Int, z: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBiome(x: Int, y: Int, z: Int): BiomeType {
        TODO("Not yet implemented")
    }

    override fun sendBlockChange(x: Int, y: Int, z: Int, state: BlockState) {
        TODO("Not yet implemented")
    }

    override fun getFluid(x: Int, y: Int, z: Int): FluidState {
        TODO("Not yet implemented")
    }

    override fun triggerExplosion(explosion: Explosion?) {
        TODO("Not yet implemented")
    }

    override fun setWeather(weather: Weather?) {
        TODO("Not yet implemented")
    }

    override fun setWeather(weather: Weather?, duration: Duration?) {
        TODO("Not yet implemented")
    }

    override fun interactBlock(x: Int, y: Int, z: Int, side: Direction?, profile: GameProfile?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getHeight(type: HeightType?, x: Int, z: Int): Int {
        TODO("Not yet implemented")
    }

    override fun createSnapshot(x: Int, y: Int, z: Int): BlockSnapshot {
        TODO("Not yet implemented")
    }

    override fun supports(x: Int, y: Int, z: Int, key: Key<*>?): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasBlockState(x: Int, y: Int, z: Int, predicate: Predicate<in BlockState>): Boolean {
        TODO("Not yet implemented")
    }

    override fun isInBorder(entity: Entity): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAreaLoaded(xStart: Int, yStart: Int, zStart: Int, xEnd: Int, yEnd: Int, zEnd: Int, allowEmpty: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun regenerateChunk(cx: Int, cy: Int, cz: Int, flag: ChunkRegenerateFlag?): Optional<Chunk> {
        TODO("Not yet implemented")
    }

    override fun undo(x: Int, y: Int, z: Int, result: DataTransactionResult?): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun playSound(sound: SoundType?, category: SoundCategory?, position: Vector3d?, volume: Double, pitch: Double, minVolume: Double) {
        TODO("Not yet implemented")
    }

    override fun stopSounds() {
        TODO("Not yet implemented")
    }

    override fun stopSounds(sound: SoundType?) {
        TODO("Not yet implemented")
    }

    override fun stopSounds(category: SoundCategory?) {
        TODO("Not yet implemented")
    }

    override fun stopSounds(sound: SoundType?, category: SoundCategory?) {
        TODO("Not yet implemented")
    }

    override fun stopSounds(sound: Supplier<out SoundType>?, category: Supplier<out SoundCategory>?) {
        TODO("Not yet implemented")
    }

    override fun createArchetypeVolume(min: Vector3i?, max: Vector3i?, origin: Vector3i?): ArchetypeVolume {
        TODO("Not yet implemented")
    }

    override fun getRemainingWeatherDuration(): Duration {
        TODO("Not yet implemented")
    }

    override fun digBlock(x: Int, y: Int, z: Int, profile: GameProfile?): Boolean {
        TODO("Not yet implemented")
    }

    override fun spawnEntities(entities: MutableIterable<Entity>?): MutableCollection<Entity> {
        TODO("Not yet implemented")
    }

    override fun sendTitle(title: Title?) {
        TODO("Not yet implemented")
    }

    override fun destroyBlock(pos: Vector3i?, performDrops: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun <E : Any?, V : Value<E>?> getValue(x: Int, y: Int, z: Int, key: Key<V>?): Optional<V> {
        TODO("Not yet implemented")
    }

    override fun <E : Any?> get(x: Int, y: Int, z: Int, key: Key<out Value<E>>?): Optional<E> {
        TODO("Not yet implemented")
    }

    override fun placeBlock(x: Int, y: Int, z: Int, block: BlockState?, side: Direction?, profile: GameProfile?): Boolean {
        TODO("Not yet implemented")
    }

    override fun stopMusicDisc(position: Vector3i?) {
        TODO("Not yet implemented")
    }

    override fun getBlockDigTimeWith(x: Int, y: Int, z: Int, itemStack: ItemStack?, profile: GameProfile): Duration {
        TODO("Not yet implemented")
    }

    override fun getKeys(x: Int, y: Int, z: Int): MutableSet<Key<*>> {
        TODO("Not yet implemented")
    }

    override fun resetBlockChange(x: Int, y: Int, z: Int) {
        TODO("Not yet implemented")
    }

    override fun sendBookView(bookView: BookView) {
        TODO("Not yet implemented")
    }

    override fun getClosestPlayer(
            x: Int, y: Int, z: Int, distance: Double, predicate: Predicate<in org.spongepowered.api.entity.living.player.Player>
    ): Optional<Player> {
        TODO("Not yet implemented")
    }

    override fun containsBlock(x: Int, y: Int, z: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getValues(x: Int, y: Int, z: Int): MutableSet<Value.Immutable<*>> {
        TODO("Not yet implemented")
    }

    override fun getWeather(): Weather {
        TODO("Not yet implemented")
    }

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, from: DataHolder?): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, from: DataHolder?, function: MergeFunction?): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun copyFrom(xTo: Int, yTo: Int, zTo: Int, xFrom: Int, yFrom: Int, zFrom: Int, function: MergeFunction?): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun getBlock(x: Int, y: Int, z: Int): BlockState {
        TODO("Not yet implemented")
    }

    override fun getBlockEntities(): Collection<BlockEntity> {
        TODO("Not yet implemented")
    }

    override fun getSkylightSubtracted(): Int {
        TODO("Not yet implemented")
    }

    override fun getScheduledBlockUpdates(): ScheduledUpdateList<BlockType> {
        TODO("Not yet implemented")
    }

    override fun getBlockEntity(x: Int, y: Int, z: Int): Optional<out BlockEntity> {
        TODO("Not yet implemented")
    }

    override fun digBlockWith(x: Int, y: Int, z: Int, itemStack: ItemStack?, profile: GameProfile?): Boolean {
        TODO("Not yet implemented")
    }

    override fun loadChunk(cx: Int, cy: Int, cz: Int, shouldGenerate: Boolean): Optional<Chunk> {
        TODO("Not yet implemented")
    }

    override fun getScheduledFluidUpdates(): ScheduledUpdateList<FluidType> {
        TODO("Not yet implemented")
    }

    override fun save(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getDimension(): Dimension {
        TODO("Not yet implemented")
    }

    override fun hasLiquid(x: Int, y: Int, z: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getEntity(uuid: UUID): Optional<Entity> {
        TODO("Not yet implemented")
    }

    override fun spawnParticles(particleEffect: ParticleEffect, position: Vector3d, radius: Int) {
        TODO("Not yet implemented")
    }

    override fun getContext(): Context {
        TODO("Not yet implemented")
    }

    override fun unloadChunk(chunk: Chunk): Boolean {
        TODO("Not yet implemented")
    }

    override fun containsAnyLiquids(aabb: AABB): Boolean {
        TODO("Not yet implemented")
    }

    override fun playMusicDisc(position: Vector3i, musicDiscType: MusicDisc) {
        TODO("Not yet implemented")
    }

    override fun playMusicDisc(position: Vector3i?, musicDiscType: Supplier<out MusicDisc>?) {
        TODO("Not yet implemented")
    }

    override fun setBlock(x: Int, y: Int, z: Int, state: BlockState, flag: BlockChangeFlag): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasChunk(x: Int, y: Int, z: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasChunk(position: Vector3i): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCollisionBoxesEmpty(entity: Entity?, aabb: AABB): Boolean {
        TODO("Not yet implemented")
    }

    override fun getRunningWeatherDuration(): Duration {
        TODO("Not yet implemented")
    }

    override fun getSeaLevel(): Int {
        TODO("Not yet implemented")
    }

    override fun setRawData(x: Int, y: Int, z: Int, container: DataView) {
        TODO("Not yet implemented")
    }

    override fun createEntityNaturally(type: EntityType<*>, position: Vector3d): Entity {
        TODO("Not yet implemented")
    }

    override fun getRaidAt(blockPosition: Vector3i): Optional<Raid> {
        TODO("Not yet implemented")
    }

    override fun getLoadedChunks(): MutableIterable<Chunk> {
        TODO("Not yet implemented")
    }

    override fun hitBlock(x: Int, y: Int, z: Int, side: Direction, profile: GameProfile): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBlockSize(): Vector3i {
        TODO("Not yet implemented")
    }

    override fun canSeeSky(x: Int, y: Int, z: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getRaids(): MutableCollection<Raid> {
        TODO("Not yet implemented")
    }

    override fun getBorder(): WorldBorder {
        TODO("Not yet implemented")
    }

    override fun getPlayers(): Collection<Player> {
        TODO("Not yet implemented")
    }

    override fun getChunk(cx: Int, cy: Int, cz: Int): Chunk {
        TODO("Not yet implemented")
    }

    override fun validateRawData(x: Int, y: Int, z: Int, container: DataView): Boolean {
        TODO("Not yet implemented")
    }

    override fun setBiome(x: Int, y: Int, z: Int, biome: BiomeType): Boolean {
        TODO("Not yet implemented")
    }

    override fun createEntity(type: EntityType<*>, position: Vector3d): Entity {
        TODO("Not yet implemented")
    }

    override fun createEntity(entityContainer: DataContainer): Optional<Entity> {
        TODO("Not yet implemented")
    }

    override fun createEntity(entityContainer: DataContainer, position: Vector3d): Optional<Entity> {
        TODO("Not yet implemented")
    }

    override fun remove(x: Int, y: Int, z: Int, key: Key<*>): DataTransactionResult {
        TODO("Not yet implemented")
    }

    override fun getEntities(): Collection<Entity> {
        TODO("Not yet implemented")
    }

    override fun <T : Entity> getEntities(entityClass: Class<out T>, box: AABB, predicate: Predicate<in T>?): Collection<T> {
        TODO("Not yet implemented")
    }

    override fun getEntities(box: AABB, filter: Predicate<in Entity>?): Collection<Entity> {
        TODO("Not yet implemented")
    }

    override fun stopSoundTypes(sound: Supplier<out SoundType>?) {
        TODO("Not yet implemented")
    }

    override fun getBlockMax(): Vector3i {
        TODO("Not yet implemented")
    }

    override fun getLight(type: LightType, x: Int, y: Int, z: Int): Int {
        TODO("Not yet implemented")
    }

    override fun stopSoundCategoriess(category: Supplier<out SoundCategory>?) {
        TODO("Not yet implemented")
    }

    override fun getHighestYAt(x: Int, z: Int): Int {
        TODO("Not yet implemented")
    }

    override fun restoreSnapshot(snapshot: BlockSnapshot?, force: Boolean, flag: BlockChangeFlag?): Boolean {
        TODO("Not yet implemented")
    }

    override fun restoreSnapshot(x: Int, y: Int, z: Int, snapshot: BlockSnapshot?, force: Boolean, flag: BlockChangeFlag?): Boolean {
        TODO("Not yet implemented")
    }
}
