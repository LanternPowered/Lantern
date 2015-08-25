package org.lanternpowered.server.world;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.lanternpowered.server.world.chunk.LanternChunkManager;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.ScheduledBlockUpdate;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.service.persistence.InvalidDataException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Dimension;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.explosion.Explosion;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.storage.WorldStorage;
import org.spongepowered.api.world.weather.Weather;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

public class LanternWorld implements World {

    private final LanternChunkManager chunkManager = null;

    public LanternChunkManager getChunkManager() {
        return this.chunkManager;
    }

    @Override
    public BlockSnapshot getBlockSnapshot(Vector3i position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockSnapshot getBlockSnapshot(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setBlockSnapshot(Vector3i position, BlockSnapshot snapshot) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBlockSnapshot(int x, int y, int z, BlockSnapshot snapshot) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void interactBlock(Vector3i position, Direction side) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void interactBlock(int x, int y, int z, Direction side) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void interactBlockWith(Vector3i position, ItemStack itemStack, Direction side) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void interactBlockWith(int x, int y, int z, ItemStack itemStack, Direction side) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean digBlock(Vector3i position) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean digBlock(int x, int y, int z) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean digBlockWith(Vector3i position, ItemStack itemStack) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean digBlockWith(int x, int y, int z, ItemStack itemStack) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getBlockDigTimeWith(Vector3i position, ItemStack itemStack) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getBlockDigTimeWith(int x, int y, int z, ItemStack itemStack) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isBlockFacePowered(Vector3i position, Direction direction) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isBlockFacePowered(int x, int y, int z, Direction direction) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(Vector3i position, Direction direction) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(int x, int y, int z, Direction direction) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Collection<Direction> getPoweredBlockFaces(Vector3i position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Direction> getPoweredBlockFaces(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Direction> getIndirectlyPoweredBlockFaces(Vector3i position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Direction> getIndirectlyPoweredBlockFaces(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isBlockFlammable(Vector3i position, Direction faceDirection) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isBlockFlammable(int x, int y, int z, Direction faceDirection) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(Vector3i position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(Vector3i position, int priority, int ticks) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(int x, int y, int z, int priority, int ticks) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeScheduledUpdate(Vector3i position, ScheduledBlockUpdate update) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeScheduledUpdate(int x, int y, int z, ScheduledBlockUpdate update) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isLoaded() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Extent getExtentView(Vector3i newMin, Vector3i newMax) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Extent getExtentView(DiscreteTransform3 transform) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Extent getRelativeExtentView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Entity> getEntities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Entity> getEntities(Predicate<Entity> filter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Entity> createEntity(EntityType type, Vector3d position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Entity> createEntity(EntityType type, Vector3i position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer, Vector3d position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean spawnEntity(Entity entity) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Collection<TileEntity> getTileEntities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<TileEntity> getTileEntities(Predicate<TileEntity> filter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<TileEntity> getTileEntity(Vector3i position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<TileEntity> getTileEntity(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setBlock(Vector3i position, BlockState block) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBlockType(Vector3i position, BlockType type) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBlockType(int x, int y, int z, BlockType type) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public MutableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MutableBlockVolume getBlockView(DiscreteTransform3 transform) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MutableBlockVolume getRelativeBlockView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector3i getBlockMin() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector3i getBlockMax() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector3i getBlockSize() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean containsBlock(Vector3i position) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public BlockState getBlock(Vector3i position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockType getBlockType(Vector3i position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UnmodifiableBlockVolume getUnmodifiableBlockView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MutableBlockVolume getBlockCopy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MutableBlockVolume getBlockCopy(StorageType type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ImmutableBlockVolume getImmutableBlockCopy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setBiome(Vector2i position, BiomeType biome) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBiome(int x, int z, BiomeType biome) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public MutableBiomeArea getBiomeView(Vector2i newMin, Vector2i newMax) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MutableBiomeArea getBiomeView(DiscreteTransform2 transform) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MutableBiomeArea getRelativeBiomeView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector2i getBiomeMin() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector2i getBiomeMax() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector2i getBiomeSize() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean containsBiome(Vector2i position) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsBiome(int x, int z) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public BiomeType getBiome(Vector2i position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UnmodifiableBiomeArea getUnmodifiableBiomeView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MutableBiomeArea getBiomeCopy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MutableBiomeArea getBiomeCopy(StorageType type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ImmutableBiomeArea getImmutableBiomeCopy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Vector3i coordinates, Class<T> propertyClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Class<T> propertyClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Property<?, ?>> getProperties(Vector3i coordinates) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Property<?, ?>> getProperties(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> Optional<E> get(Vector3i coordinates, Key<? extends BaseValue<E>> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> Optional<E> get(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> get(Vector3i coordinates, Class<T> manipulatorClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> get(int x, int y, int z, Class<T> manipulatorClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(Vector3i coordinates, Class<T> manipulatorClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(int x, int y, int z, Class<T> manipulatorClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> E getOrNull(Vector3i coordinates, Key<? extends BaseValue<E>> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> E getOrNull(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> E getOrElse(Vector3i coordinates, Key<? extends BaseValue<E>> key, E defaultValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> E getOrElse(int x, int y, int z, Key<? extends BaseValue<E>> key, E defaultValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(Vector3i coordinates, Key<V> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(int x, int y, int z, Key<V> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supports(Vector3i coordinates, Key<?> key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(int x, int y, int z, Key<?> key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(Vector3i coordinates, BaseValue<?> value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(int x, int y, int z, BaseValue<?> value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(Vector3i coordinates, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(Vector3i coordinates, DataManipulator<?, ?> manipulator) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(int x, int y, int z, DataManipulator<?, ?> manipulator) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ImmutableSet<Key<?>> getKeys(Vector3i coordinates) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ImmutableSet<Key<?>> getKeys(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ImmutableSet<ImmutableValue<?>> getValues(Vector3i coordinates) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ImmutableSet<ImmutableValue<?>> getValues(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> DataTransactionResult transform(Vector3i coordinates, Key<? extends BaseValue<E>> key, Function<E, E> function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> DataTransactionResult transform(int x, int y, int z, Key<? extends BaseValue<E>> key, Function<E, E> function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> DataTransactionResult offer(Vector3i coordinates, Key<? extends BaseValue<E>> key, E value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, Key<? extends BaseValue<E>> key, E value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> DataTransactionResult offer(Vector3i coordinates, BaseValue<E> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, BaseValue<E> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(Vector3i coordinates, DataManipulator<?, ?> manipulator) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(Vector3i coordinates, DataManipulator<?, ?> manipulator, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(Vector3i coordinates, Iterable<DataManipulator<?, ?>> manipulators) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, Iterable<DataManipulator<?, ?>> manipulators) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(Vector3i blockPosition, Iterable<DataManipulator<?, ?>> values, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult remove(Vector3i coordinates, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult remove(Vector3i coordinates, Key<?> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Key<?> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult undo(Vector3i coordinates, DataTransactionResult result) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult undo(int x, int y, int z, DataTransactionResult result) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(Vector3i to, DataHolder from) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, DataHolder from) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(Vector3i coordinatesTo, Vector3i coordinatesFrom) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(Vector3i to, DataHolder from, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, DataHolder from, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(Vector3i coordinatesTo, Vector3i coordinatesFrom, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<DataManipulator<?, ?>> getManipulators(Vector3i coordinates) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<DataManipulator<?, ?>> getManipulators(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean validateRawData(Vector3i position, DataView container) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean validateRawData(int x, int y, int z, DataView container) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setRawData(Vector3i position, DataView container) throws InvalidDataException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setRawData(int x, int y, int z, DataView container) throws InvalidDataException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public UUID getUniqueId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Weather getWeather() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getRemainingDuration() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getRunningDuration() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void forecast(Weather weather) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void forecast(Weather weather, long duration) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position, int radius) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume, double pitch) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume, double pitch, double minVolume) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendMessage(ChatType type, String... message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendMessage(ChatType type, Text... messages) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendMessage(ChatType type, Iterable<Text> messages) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendTitle(Title title) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resetTitle() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clearTitle() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Context getContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location<World> getLocation(Vector3i position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location<World> getLocation(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location<World> getLocation(Vector3d position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location<World> getLocation(double x, double y, double z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Difficulty getDifficulty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Chunk> getChunk(Vector3i position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Chunk> getChunk(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Chunk> loadChunk(Vector3i position, boolean shouldGenerate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Chunk> loadChunk(int x, int y, int z, boolean shouldGenerate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean unloadChunk(Chunk chunk) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterable<Chunk> getLoadedChunks() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Entity> getEntity(UUID uuid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WorldBorder getWorldBorder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<String> getGameRule(String gameRule) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getGameRules() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Dimension getDimension() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WorldGenerator getWorldGenerator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setWorldGenerator(WorldGenerator generator) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setKeepSpawnLoaded(boolean keepLoaded) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public WorldStorage getWorldStorage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Scoreboard getScoreboard() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public WorldCreationSettings getCreationSettings() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WorldProperties getProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location<World> getSpawnLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void triggerExplosion(Explosion explosion) {
        // TODO Auto-generated method stub
        
    }

}
