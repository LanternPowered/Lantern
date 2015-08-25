package org.lanternpowered.server.world.chunk;

import java.util.Collection;
import java.util.UUID;

import org.lanternpowered.server.block.LanternBlockSnapshot;
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
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.persistence.InvalidDataException;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

public class LanternChunk implements Chunk {

    /**
     * Converts the vector2i into a vector3i with x -> x and y -> z.
     * 
     * @param vector2 the vector2
     * @return the vector3
     */
    public static Vector3i fromVector2(Vector2i vector2) {
        return new Vector3i(vector2.getX(), 0, vector2.getY());
    }

    /**
     * Converts the vector3i into a vector2i with x -> x and z -> y.
     * 
     * @param vector3 the vector3
     * @return the vector2
     */
    public static Vector2i toVector2(Vector3i vector3) {
        return vector3.toVector2(true);
    }

    @Override
    public BlockSnapshot getBlockSnapshot(Vector3i position) {
        return new LanternBlockSnapshot(position, this.getBlock(position));
    }

    @Override
    public BlockSnapshot getBlockSnapshot(int x, int y, int z) {
        return new LanternBlockSnapshot(new Vector3i(x, y, z), this.getBlock(x, y, z));
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
    public <T extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> Optional<T> get(Vector3i coordinates, Class<T> manipulatorClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> Optional<T> get(int x, int y, int z, Class<T> manipulatorClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> Optional<T> getOrCreate(Vector3i coordinates,
            Class<T> manipulatorClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> Optional<T> getOrCreate(int x, int y, int z,
            Class<T> manipulatorClass) {
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
    public boolean supports(Vector3i coordinates, Class<? extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> manipulatorClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(int x, int y, int z, Class<? extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> manipulatorClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(Vector3i coordinates, org.spongepowered.api.data.manipulator.DataManipulator<?, ?> manipulator) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(int x, int y, int z, org.spongepowered.api.data.manipulator.DataManipulator<?, ?> manipulator) {
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
    public DataTransactionResult offer(Vector3i coordinates, org.spongepowered.api.data.manipulator.DataManipulator<?, ?> manipulator) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, org.spongepowered.api.data.manipulator.DataManipulator<?, ?> manipulator) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(Vector3i coordinates, org.spongepowered.api.data.manipulator.DataManipulator<?, ?> manipulator,
            MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, org.spongepowered.api.data.manipulator.DataManipulator<?, ?> manipulator,
            MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(Vector3i coordinates, Iterable<org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> manipulators) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, Iterable<org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> manipulators) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(Vector3i blockPosition, Iterable<org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> values,
            MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult remove(Vector3i coordinates,
            Class<? extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> manipulatorClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z,
            Class<? extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> manipulatorClass) {
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
    public Collection<org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> getManipulators(Vector3i coordinates) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> getManipulators(int x, int y, int z) {
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
    public Location<Chunk> getLocation(Vector3i position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location<Chunk> getLocation(int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location<Chunk> getLocation(Vector3d position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location<Chunk> getLocation(double x, double y, double z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector3i getPosition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public World getWorld() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isPopulated() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean loadChunk(boolean generate) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean unloadChunk() {
        // TODO Auto-generated method stub
        return false;
    }

}
