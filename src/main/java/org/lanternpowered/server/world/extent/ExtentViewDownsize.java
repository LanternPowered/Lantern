package org.lanternpowered.server.world.extent;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

import org.lanternpowered.server.util.VecHelper;
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
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.persistence.InvalidDataException;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.Extent;

public class ExtentViewDownsize extends AbstractExtent {

    private final Extent extent;
    private final Vector3i blockMin;
    private final Vector3i blockMax;
    private final Vector3i blockSize;
    private final Vector2i biomeMin;
    private final Vector2i biomeMax;
    private final Vector2i biomeSize;

    public ExtentViewDownsize(Extent extent, Vector3i blockMin, Vector3i blockMax) {
        this.extent = extent;
        this.blockMin = blockMin;
        this.blockMax = blockMax;
        this.blockSize = this.blockMax.sub(this.blockMin).add(Vector3i.ONE);
        this.biomeMin = blockMin.toVector2(true);
        this.biomeMax = blockMax.toVector2(true);
        this.biomeSize = this.biomeMax.sub(this.biomeMin).add(Vector2i.ONE);
    }

    @Override
    public UUID getUniqueId() {
        return this.extent.getUniqueId();
    }

    @Override
    public boolean isLoaded() {
        return this.extent.isLoaded();
    }

    @Override
    public Vector2i getBiomeMin() {
        return this.biomeMin;
    }

    @Override
    public Vector2i getBiomeMax() {
        return this.biomeMax;
    }

    @Override
    public Vector2i getBiomeSize() {
        return this.biomeSize;
    }

    @Override
    public boolean containsBiome(int x, int z) {
        return VecHelper.inBounds(x, z, this.biomeMin, this.biomeMax);
    }

    private void checkRange(int x, int z) {
        if (!VecHelper.inBounds(x, z, this.biomeMin, this.biomeMax)) {
            throw new PositionOutOfBoundsException(new Vector2i(x, z), this.biomeMin, this.biomeMax);
        }
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        this.checkRange(x, z);
        return this.extent.getBiome(x, z);
    }

    @Override
    public void setBiome(int x, int z, BiomeType biome) {
        this.checkRange(x, z);
        this.extent.setBiome(x, z, biome);
    }

    @Override
    public Location<? extends Extent> getLocation(int x, int y, int z) {
        this.checkRange(x, y, z);
        return new Location<ExtentViewDownsize>(this, x, y, z);
    }

    @Override
    public Location<? extends Extent> getLocation(double x, double y, double z) {
        this.checkRange(x, y, z);
        return new Location<ExtentViewDownsize>(this, x, y, z);
    }

    @Override
    public Vector3i getBlockMax() {
        return this.blockMax;
    }

    @Override
    public Vector3i getBlockMin() {
        return this.blockMin;
    }

    @Override
    public Vector3i getBlockSize() {
        return this.blockSize;
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, this.blockMin, this.blockMax);
    }

    private void checkRange(double x, double y, double z) {
        if (!VecHelper.inBounds(x, y, z, this.blockMin, this.blockMax)) {
            throw new PositionOutOfBoundsException(new Vector3d(x, y, z), this.blockMin.toDouble(), this.blockMax.toDouble());
        }
    }

    private void checkRange(int x, int y, int z) {
        if (!VecHelper.inBounds(x, y, z, this.blockMin, this.blockMax)) {
            throw new PositionOutOfBoundsException(new Vector3i(x, y, z), this.blockMin, this.blockMax);
        }
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        return this.getBlock(x, y, z).getType();
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        this.checkRange(x, y, z);
        return this.extent.getBlock(x, y, z);
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Vector3i coords, Direction direction, Class<T> propertyClass) {
        int x = coords.getX();
        int y = coords.getY();
        int z = coords.getZ();
        this.checkRange(x, y, z);
        return this.extent.getProperty(new Vector3i(x, y, z), direction, propertyClass);
    }

    /**
     * TODO: Use true/false as default notifyNeighbors setting?
     */
    @Override
    public void setBlock(int x, int y, int z, BlockState block, boolean notifyNeighbors) {
        this.checkRange(x, y, z);
        this.extent.setBlock(x, y, z, block, notifyNeighbors);
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block) {
        this.checkRange(x, y, z);
        this.extent.setBlock(x, y, z, block);
    }

    @Override
    public BlockSnapshot createSnapshot(int x, int y, int z) {
        this.checkRange(x, y, z);
        return this.extent.createSnapshot(x, y, z);
    }

    @Override
    public boolean restoreSnapshot(int x, int y, int z, BlockSnapshot snapshot, boolean force, boolean notifyNeighbors) {
        this.checkRange(x, y, z);
        return this.extent.restoreSnapshot(x, y, z, snapshot, force, notifyNeighbors);
    }

    @Override
    public Optional<Entity> restoreSnapshot(EntitySnapshot snapshot, Vector3d position) {
        this.checkRange(position.getX(), position.getY(), position.getZ());
        return this.extent.restoreSnapshot(snapshot, position);
    }

    @Override
    public boolean spawnEntity(Entity entity, Cause cause) {
        Vector3d position = entity.getLocation().getPosition();
        this.checkRange(position.getX(), position.getY(), position.getZ());
        return this.extent.spawnEntity(entity, cause);
    }

    @Override
    public void interactBlock(int x, int y, int z, Direction side) {
        this.checkRange(x, y, z);
        this.extent.interactBlock(x, y, z, side);
    }

    @Override
    public void interactBlockWith(int x, int y, int z, ItemStack itemStack, Direction side) {
        this.checkRange(x, y, z);
        this.extent.interactBlockWith(x, y, z, itemStack, side);
    }

    @Override
    public boolean digBlock(int x, int y, int z) {
        this.checkRange(x, y, z);
        return this.extent.digBlock(x, y, z);
    }

    @Override
    public boolean digBlockWith(int x, int y, int z, ItemStack itemStack) {
        this.checkRange(x, y, z);
        return this.extent.digBlockWith(x, y, z, itemStack);
    }

    @Override
    public int getBlockDigTimeWith(int x, int y, int z, ItemStack itemStack) {
        this.checkRange(x, y, z);
        return this.extent.getBlockDigTimeWith(x, y, z, itemStack);
    }

    @Override
    public boolean isBlockFacePowered(int x, int y, int z, Direction direction) {
        this.checkRange(x, y, z);
        return this.extent.isBlockFacePowered(x, y, z, direction);
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(int x, int y, int z, Direction direction) {
        this.checkRange(x, y, z);
        return this.extent.isBlockFaceIndirectlyPowered(x, y, z, direction);
    }

    @Override
    public Collection<Direction> getPoweredBlockFaces(int x, int y, int z) {
        this.checkRange(x, y, z);
        return this.extent.getPoweredBlockFaces(x, y, z);
    }

    @Override
    public Collection<Direction> getIndirectlyPoweredBlockFaces(int x, int y, int z) {
        this.checkRange(x, y, z);
        return this.extent.getIndirectlyPoweredBlockFaces(x, y, z);
    }

    @Override
    public boolean isBlockFlammable(int x, int y, int z, Direction faceDirection) {
        this.checkRange(x, y, z);
        return this.extent.isBlockFlammable(x, y, z, faceDirection);
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(int x, int y, int z) {
        this.checkRange(x, y, z);
        return this.extent.getScheduledUpdates(x, y, z);
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(int x, int y, int z, int priority, int ticks) {
        this.checkRange(x, y, z);
        return this.extent.addScheduledUpdate(x, y, z, priority, ticks);
    }

    @Override
    public void removeScheduledUpdate(int x, int y, int z, ScheduledBlockUpdate update) {
        this.checkRange(x, y, z);
        this.extent.removeScheduledUpdate(x, y, z, update);
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Class<T> propertyClass) {
        this.checkRange(x, y, z);
        return this.extent.getProperty(x, y, z, propertyClass);
    }

    @Override
    public Collection<Property<?, ?>> getProperties(int x, int y, int z) {
        this.checkRange(x, y, z);
        return this.extent.getProperties(x, y, z);
    }

    @Override
    public <E> Optional<E> get(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        this.checkRange(x, y, z);
        return this.extent.get(x, y, z, key);
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> get(int x, int y, int z, Class<T> manipulatorClass) {
        this.checkRange(x, y, z);
        return this.extent.get(x, y, z, manipulatorClass);
    }

    @Override
    public ImmutableSet<ImmutableValue<?>> getValues(int x, int y, int z) {
        this.checkRange(x, y, z);
        return this.extent.getValues(x, y, z);
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(int x, int y, int z, Class<T> manipulatorClass) {
        this.checkRange(x, y, z);
        return this.extent.getOrCreate(x, y, z, manipulatorClass);
    }

    @Override
    public <E> E getOrNull(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        this.checkRange(x, y, z);
        return this.extent.getOrNull(x, y, z, key);
    }

    @Override
    public <E> E getOrElse(int x, int y, int z, Key<? extends BaseValue<E>> key, E defaultValue) {
        this.checkRange(x, y, z);
        return this.extent.getOrElse(x, y, z, key, defaultValue);
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(int x, int y, int z, Key<V> key) {
        this.checkRange(x, y, z);
        return this.extent.getValue(x, y, z, key);
    }

    @Override
    public boolean supports(int x, int y, int z, Key<?> key) {
        this.checkRange(x, y, z);
        return this.extent.supports(x, y, z, key);
    }

    @Override
    public boolean supports(int x, int y, int z, BaseValue<?> value) {
        this.checkRange(x, y, z);
        return this.extent.supports(x, y, z, value);
    }

    @Override
    public boolean supports(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        this.checkRange(x, y, z);
        return this.extent.supports(x, y, z, manipulatorClass);
    }

    @Override
    public boolean supports(int x, int y, int z, DataManipulator<?, ?> manipulator) {
        this.checkRange(x, y, z);
        return this.extent.supports(x, y, z, manipulator);
    }

    @Override
    public ImmutableSet<Key<?>> getKeys(int x, int y, int z) {
        this.checkRange(x, y, z);
        return this.extent.getKeys(x, y, z);
    }

    @Override
    public <E> DataTransactionResult transform(int x, int y, int z, Key<? extends BaseValue<E>> key, Function<E, E> function) {
        this.checkRange(x, y, z);
        return this.extent.transform(x, y, z, key, function);
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, BaseValue<E> value) {
        this.checkRange(x, y, z);
        return this.extent.offer(x, y, z, value);
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, Key<? extends BaseValue<E>> key, E value) {
        this.checkRange(x, y, z);
        return this.extent.offer(x, y, z, key, value);
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator) {
        this.checkRange(x, y, z);
        return this.extent.offer(x, y, z, manipulator);
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator, MergeFunction function) {
        this.checkRange(x, y, z);
        return this.extent.offer(x, y, z, manipulator, function);
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, Iterable<DataManipulator<?, ?>> manipulators) {
        this.checkRange(x, y, z);
        return this.extent.offer(x, y, z, manipulators);
    }

    @Override
    public DataTransactionResult offer(Vector3i blockPosition, Iterable<DataManipulator<?, ?>> values, MergeFunction function) {
        this.checkRange(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
        return this.extent.offer(blockPosition, values, function);
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Key<?> key) {
        this.checkRange(x, y, z);
        return this.extent.remove(x, y, z, key);
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        this.checkRange(x, y, z);
        return this.extent.remove(x, y, z, manipulatorClass);
    }

    @Override
    public DataTransactionResult undo(int x, int y, int z, DataTransactionResult result) {
        this.checkRange(x, y, z);
        return this.extent.undo(x, y, z, result);
    }

    @Override
    public Collection<DataManipulator<?, ?>> getManipulators(int x, int y, int z) {
        this.checkRange(x, y, z);
        return this.extent.getManipulators(x, y, z);
    }

    @Override
    public boolean validateRawData(int x, int y, int z, DataView container) {
        this.checkRange(x, y, z);
        return this.extent.validateRawData(x, y, z, container);
    }

    @Override
    public void setRawData(int x, int y, int z, DataView container) throws InvalidDataException {
        this.checkRange(x, y, z);
        this.extent.setRawData(x, y, z, container);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, DataHolder from) {
        this.checkRange(xTo, yTo, zTo);
        return this.extent.copyFrom(xTo, yTo, zTo, from);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom) {
        this.checkRange(xTo, yTo, zTo);
        this.checkRange(xFrom, yFrom, zFrom);
        return this.extent.copyFrom(xTo, yTo, zTo, xFrom, yFrom, zFrom);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, DataHolder from, MergeFunction function) {
        this.checkRange(xTo, yTo, zTo);
        return this.extent.copyFrom(xTo, yTo, zTo, from, function);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom, MergeFunction function) {
        this.checkRange(xTo, yTo, zTo);
        this.checkRange(xFrom, yFrom, zFrom);
        return this.extent.copyFrom(xTo, yTo, zTo, xFrom, yFrom, zFrom, function);
    }

    @Override
    public Collection<TileEntity> getTileEntities() {
        final Collection<TileEntity> tileEntities = this.extent.getTileEntities();
        for (Iterator<TileEntity> iterator = tileEntities.iterator(); iterator.hasNext(); ) {
            final TileEntity tileEntity = iterator.next();
            final Location<World> block = tileEntity.getLocation();
            if (!VecHelper.inBounds(block.getX(), block.getY(), block.getZ(), this.blockMin, this.blockMax)) {
                iterator.remove();
            }
        }
        return tileEntities;
    }

    @Override
    public Collection<TileEntity> getTileEntities(Predicate<TileEntity> filter) {
        // Order matters! Bounds filter before the argument filter so it doesn't see out of bounds entities
        return this.extent.getTileEntities(Predicates.<TileEntity>and(new TileEntityInBounds(this.blockMin, this.blockMax), filter));
    }

    @Override
    public Optional<TileEntity> getTileEntity(int x, int y, int z) {
        this.checkRange(x, y, z);
        return this.extent.getTileEntity(x, y, z);
    }

    @Override
    public Optional<Entity> createEntity(EntityType type, Vector3i position) {
        this.checkRange(position.getX(), position.getY(), position.getZ());
        return this.extent.createEntity(type, position);
    }

    @Override
    public Collection<Entity> getEntities() {
        final Collection<Entity> entities = this.extent.getEntities();
        for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext(); ) {
            final Entity tileEntity = iterator.next();
            final Location<World> block = tileEntity.getLocation();
            if (!VecHelper.inBounds(block.getX(), block.getY(), block.getZ(), this.blockMin, this.blockMax)) {
                iterator.remove();
            }
        }
        return entities;
    }

    @Override
    public Collection<Entity> getEntities(Predicate<Entity> filter) {
        // Order matters! Bounds filter before the argument filter so it doesn't see out of bounds entities
        return this.extent.getEntities(Predicates.<Entity>and(new EntityInBounds(this.blockMin, this.blockMax), filter));
    }

    @Override
    public Optional<Entity> createEntity(EntityType type, Vector3d position) {
        this.checkRange(position.getX(), position.getY(), position.getZ());
        return this.extent.createEntity(type, position);
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer) {
        // TODO once entity containers are implemented
        // checkRange(position.getX(), position.getY(), position.getZ());
        return Optional.absent();
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer, Vector3d position) {
        this.checkRange(position.getX(), position.getY(), position.getZ());
        return this.extent.createEntity(entityContainer, position);
    }

    @Override
    public Extent getExtentView(Vector3i newMin, Vector3i newMax) {
        this.checkRange(newMin.getX(), newMin.getY(), newMin.getZ());
        this.checkRange(newMax.getX(), newMax.getY(), newMax.getZ());
        return new ExtentViewDownsize(this.extent, newMin, newMax);
    }

    @Override
    public Extent getExtentView(DiscreteTransform3 transform) {
        return new ExtentViewTransform(this, transform);
    }

    @Override
    public Extent getRelativeExtentView() {
        return this.getExtentView(DiscreteTransform3.fromTranslation(this.getBlockMin().negate()));
    }

    private static class EntityInBounds implements Predicate<Entity> {

        private final Vector3i min;
        private final Vector3i max;

        private EntityInBounds(Vector3i min, Vector3i max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public boolean apply(Entity input) {
            final Location<World> block = input.getLocation();
            return VecHelper.inBounds(block.getX(), block.getY(), block.getZ(), this.min, this.max);
        }
    }

    private static class TileEntityInBounds implements Predicate<TileEntity> {

        private final Vector3i min;
        private final Vector3i max;

        private TileEntityInBounds(Vector3i min, Vector3i max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public boolean apply(TileEntity input) {
            final Location<World> block = input.getLocation();
            return VecHelper.inBounds(block.getX(), block.getY(), block.getZ(), this.min, this.max);
        }
    }
}
