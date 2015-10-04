package org.lanternpowered.server.block;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.lanternpowered.server.data.util.DataQueries;
import org.lanternpowered.server.world.WeakWorldReference;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

import static com.google.common.base.Preconditions.checkNotNull;

public class LanternBlockSnapshot implements BlockSnapshot {

    private final WeakWorldReference world;
    @Nullable private final Vector3i position;
    private final BlockState state;

    public LanternBlockSnapshot(Location<World> location, BlockState blockState) {
        this(new WeakWorldReference(checkNotNull(location, "location").getExtent()),
                location.getBlockPosition(), blockState);
    }

    public LanternBlockSnapshot(UUID worldUUID, Vector3i position, BlockState blockState) {
        this(new WeakWorldReference(checkNotNull(worldUUID, "worldUUID")), position, blockState);
    }

    private LanternBlockSnapshot(WeakWorldReference world, Vector3i position,
            BlockState blockState) {
        this.state = checkNotNull(blockState, "blockState");
        this.position = checkNotNull(position, "position");
        this.world = world;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer()
            .set(Location.WORLD_ID, this.world.getUniqueId().toString())
            .createView(DataQueries.SNAPSHOT_WORLD_POSITION)
                .set(Location.POSITION_X, this.position.getX())
                .set(Location.POSITION_Y, this.position.getY())
                .set(Location.POSITION_Z, this.position.getZ())
            .getContainer()
            .set(DataQueries.BLOCK_STATE, this.state);
    }

    @Override
    public BlockState getState() {
        return this.state;
    }

    @Override
    public LanternBlockSnapshot copy() {
        return new LanternBlockSnapshot(this.world == null ? null : this.world.copy(), this.position, this.state);
    }

    @Override
    public Optional<Location<World>> getLocation() {
        if (this.world == null) {
            return Optional.empty();
        }
        Optional<World> world = this.world.getWorld();
        if (!world.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(new Location<>(world.get(), this.position));
    }

    @Override
    public List<ImmutableDataManipulator<?, ?>> getManipulators() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends ImmutableDataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends ImmutableDataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supports(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <E> Optional<BlockSnapshot> transform(Key<? extends BaseValue<E>> key, Function<E, E> function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> Optional<BlockSnapshot> with(Key<? extends BaseValue<E>> key, E value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<BlockSnapshot> with(BaseValue<?> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<BlockSnapshot> with(ImmutableDataManipulator<?, ?> valueContainer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<BlockSnapshot> with(Iterable<ImmutableDataManipulator<?, ?>> valueContainers) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<BlockSnapshot> without(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockSnapshot merge(BlockSnapshot that) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockSnapshot merge(BlockSnapshot that, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> E getOrNull(Key<? extends BaseValue<E>> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> E getOrElse(Key<? extends BaseValue<E>> key, E defaultValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supports(Key<?> key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(BaseValue<?> baseValue) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Set<Key<?>> getKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<ImmutableValue<?>> getValues() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockSnapshot withState(BlockState blockState) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ImmutableDataManipulator<?, ?>> getContainers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UUID getWorldUniqueId() {
        return this.world.getUniqueId();
    }

    @Override
    public Vector3i getPosition() {
        return this.position;
    }

    @Override
    public BlockSnapshot withLocation(Location<World> location) {
        checkNotNull(location, "location");
        return new LanternBlockSnapshot(new WeakWorldReference(location.getExtent()),
                location.getBlockPosition(), this.state);
    }

    @Override
    public BlockSnapshot withContainer(DataContainer container) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean restore(boolean force, boolean notifyNeighbors) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Class<T> propertyClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Property<?, ?>> getApplicableProperties() {
        // TODO Auto-generated method stub
        return null;
    }
}
