/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.block;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.lanternpowered.server.block.entity.LanternBlockEntityArchetype;
import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistration;
import org.lanternpowered.server.data.manipulator.DataManipulatorRegistry;
import org.lanternpowered.server.data.property.IStorePropertyHolder;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityArchetype;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "unchecked"})
public class LanternBlockSnapshot implements BlockSnapshot, IStorePropertyHolder {

    @Nullable final Location location;
    private final BlockState state;
    @Nullable private final UUID notifier;
    @Nullable private final UUID creator;
    @Nullable final LanternBlockEntity tileEntity;

    public LanternBlockSnapshot(UUID worldUUID, Vector3i position, BlockState blockState,
            @Nullable UUID creator, @Nullable UUID notifier, @Nullable TileEntity tileEntity) {
        this(new Location(worldUUID, position), blockState, creator, notifier, tileEntity);
    }

    public LanternBlockSnapshot(BlockState blockState, @Nullable UUID notifier,
            @Nullable UUID creator, @Nullable TileEntity tileEntity) {
        this(null, blockState, creator, notifier, tileEntity);
    }

    public LanternBlockSnapshot(@Nullable Location location, BlockState blockState,
            @Nullable UUID creator, @Nullable UUID notifier, @Nullable TileEntity tileEntity) {
        this.state = checkNotNull(blockState, "blockState");
        this.tileEntity = (LanternBlockEntity) tileEntity;
        this.location = location;
        this.notifier = notifier;
        this.creator = creator;
    }

    /**
     * Gets whether this {@link LanternBlockSnapshot}
     * doesn't have a {@link Location}.
     *
     * @return Is positionless
     */
    public boolean isPositionless() {
        return this.location == null;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        final DataContainer container = DataContainer.createNew()
                .set(DataQueries.BLOCK_STATE, this.state);
        if (this.location != null) {
            container.set(Queries.WORLD_ID, this.location.getWorld().getUniqueId());
            final DataView positionView = container.createView(DataQueries.SNAPSHOT_WORLD_POSITION);
            final Vector3i position = this.location.getBlockPosition();
            positionView.set(Queries.POSITION_X, position.getX());
            positionView.set(Queries.POSITION_Y, position.getY());
            positionView.set(Queries.POSITION_Z, position.getZ());
        }
        if (this.notifier != null) {
            container.set(Queries.NOTIFIER_ID, this.notifier);
        }
        if (this.creator != null) {
            container.set(Queries.CREATOR_ID, this.creator);
        }
        return container;
    }

    @Override
    public BlockState getState() {
        return this.state;
    }

    @Override
    public LanternBlockSnapshot copy() {
        return this;
    }

    @Override
    public Optional<Location> getLocation() {
        return Optional.ofNullable(this.location);
    }

    @Override
    public List<ImmutableDataManipulator<?, ?>> getManipulators() {
        return getContainers();
    }

    @Override
    public <T extends ImmutableDataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        final Optional<T> optManipulator = this.state.get(containerClass);
        if (optManipulator.isPresent() || this.tileEntity == null) {
            return optManipulator;
        }
        final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getBy(containerClass);
        return optRegistration.flatMap(registration -> this.tileEntity.get(registration.getManipulatorClass()));
    }

    @Override
    public <T extends ImmutableDataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        return get(containerClass);
    }

    @Override
    public boolean supports(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        if (this.state.supports(containerClass)) {
            return true;
        } else if (this.tileEntity == null) {
            return false;
        }
        final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getBy(containerClass);
        return optRegistration.map(registration -> this.tileEntity.supports(registration.getManipulatorClass())).orElse(false);
    }

    @Override
    public <E> Optional<BlockSnapshot> transform(Key<? extends Value<E>> key, Function<E, E> function) {
        final Optional<BlockState> optNewState = this.state.transform(key, function);
        if (optNewState.isPresent()) {
            return Optional.of(new LanternBlockSnapshot(this.location, optNewState.get(),
                    this.creator, this.notifier, this.tileEntity));
        } else if (this.tileEntity == null || !this.tileEntity.supports(key)) {
            return Optional.empty();
        }
        final LanternBlockEntity newTileEntity = copy(this.tileEntity);
        if (!newTileEntity.transformFast(key, function)) {
            return Optional.empty();
        }
        return Optional.of(new LanternBlockSnapshot(this.location, this.state,
                this.creator, this.notifier, newTileEntity));
    }

    @Override
    public <E> Optional<BlockSnapshot> with(Key<? extends Value<E>> key, E value) {
        final Optional<BlockState> optNewState = this.state.with(key, value);
        if (optNewState.isPresent()) {
            return Optional.of(new LanternBlockSnapshot(this.location, optNewState.get(),
                    this.creator, this.notifier, this.tileEntity));
        } else if (this.tileEntity == null || !this.tileEntity.supports(key)) {
            return Optional.empty();
        }
        final LanternBlockEntity newTileEntity = copy(this.tileEntity);
        if (!newTileEntity.offerFast(key, value)) {
            return Optional.empty();
        }
        return Optional.of(new LanternBlockSnapshot(this.location, this.state,
                this.creator, this.notifier, newTileEntity));
    }

    @Override
    public Optional<BlockSnapshot> with(Value<?> value) {
        final Optional<BlockState> optNewState = this.state.with(value);
        if (optNewState.isPresent()) {
            return Optional.of(new LanternBlockSnapshot(this.location, optNewState.get(),
                    this.creator, this.notifier, this.tileEntity));
        } else if (this.tileEntity == null || !this.tileEntity.supports(value.getKey())) {
            return Optional.empty();
        }
        final LanternBlockEntity newTileEntity = copy(this.tileEntity);
        if (!newTileEntity.offerFast(value)) {
            return Optional.empty();
        }
        return Optional.of(new LanternBlockSnapshot(this.location, this.state,
                this.creator, this.notifier, newTileEntity));
    }

    @Override
    public Optional<BlockSnapshot> with(ImmutableDataManipulator<?, ?> valueContainer) {
        final Optional<BlockState> optNewState = this.state.with(valueContainer);
        if (optNewState.isPresent()) {
            return Optional.of(new LanternBlockSnapshot(this.location, optNewState.get(),
                    this.creator, this.notifier, this.tileEntity));
        } else if (this.tileEntity == null) {
            return Optional.empty();
        }
        final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getBy(valueContainer.getClass());
        if (!optRegistration.isPresent() || !this.tileEntity.supports(optRegistration.get().getManipulatorClass())) {
            return Optional.empty();
        }
        final LanternBlockEntity newTileEntity = copy(this.tileEntity);
        if (!newTileEntity.offerFast(valueContainer.asMutable())) {
            return Optional.empty();
        }
        return Optional.of(new LanternBlockSnapshot(this.location, this.state,
                this.creator, this.notifier, newTileEntity));
    }

    @Override
    public Optional<BlockSnapshot> with(Iterable<ImmutableDataManipulator<?, ?>> valueContainers) {
        final Optional<BlockState> optNewState = this.state.with(valueContainers);
        if (optNewState.isPresent()) {
            return Optional.of(new LanternBlockSnapshot(this.location, optNewState.get(),
                    this.creator, this.notifier, this.tileEntity));
        } else if (this.tileEntity == null) {
            return Optional.empty();
        }
        final LanternBlockEntity newTileEntity = copy(this.tileEntity);
        if (!newTileEntity.offerFast(Streams.stream(valueContainers)
                .map(ImmutableDataManipulator::asMutable)
                .collect(Collectors.toList()))) {
            return Optional.empty();
        }
        return Optional.of(new LanternBlockSnapshot(this.location, this.state,
                this.creator, this.notifier, newTileEntity));
    }

    @Override
    public Optional<BlockSnapshot> without(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        // Data cannot be removed from the state
        if (this.tileEntity == null) {
            return Optional.empty();
        }
        final Optional<DataManipulatorRegistration> optRegistration = DataManipulatorRegistry.get().getBy(containerClass);
        if (!optRegistration.isPresent() || !this.tileEntity.supports(optRegistration.get().getManipulatorClass())) {
            return Optional.empty();
        }
        final LanternBlockEntity newTileEntity = copy(this.tileEntity);
        if (!newTileEntity.removeFast(optRegistration.get().getManipulatorClass())) {
            return Optional.empty();
        }
        return Optional.of(new LanternBlockSnapshot(this.location, this.state,
                this.creator, this.notifier, newTileEntity));
    }

    @Override
    public BlockSnapshot merge(BlockSnapshot that) {
        checkNotNull(that, "that");
        return merge(that, MergeFunction.IGNORE_ALL);
    }

    @Override
    public BlockSnapshot merge(BlockSnapshot that, MergeFunction function) {
        checkNotNull(that, "that");
        checkNotNull(function, "function");
        final LanternBlockSnapshot that0 = (LanternBlockSnapshot) that;
        if (that0.tileEntity == null || this.tileEntity == null) {
            return that0;
        }
        final LanternBlockEntity newTileEntity = copy(that0.tileEntity);
        newTileEntity.copyFrom(this.tileEntity, function);
        return new LanternBlockSnapshot(this.location, this.state,
                this.creator, this.notifier, newTileEntity);
    }

    @Override
    public <E> Optional<E> get(Key<? extends Value<E>> key) {
        final Optional<E> optResult = this.state.get(key);
        if (optResult.isPresent() || this.tileEntity == null) {
            return optResult;
        }
        return this.tileEntity.get(key);
    }

    @Override
    public <E, V extends Value<E>> Optional<V> getValue(Key<V> key) {
        final Optional<V> optResult = this.state.getValue(key);
        if (optResult.isPresent()) {
            return optResult;
        }
        return this.tileEntity == null ? Optional.empty() : this.tileEntity.getValue(key);
    }

    @Override
    public boolean supports(Key<?> key) {
        return this.state.supports(key) || (this.tileEntity != null && this.tileEntity.supports(key));
    }

    @Override
    public Set<Key<?>> getKeys() {
        if (this.tileEntity == null) {
            return this.state.getKeys();
        }
        final ImmutableSet.Builder<Key<?>> keys = ImmutableSet.builder();
        keys.addAll(this.state.getKeys());
        keys.addAll(this.tileEntity.getKeys());
        return keys.build();
    }

    @Override
    public Set<Value.Immutable<?>> getValues() {
        if (this.tileEntity == null) {
            return this.state.getValues();
        }
        final ImmutableSet.Builder<Value.Immutable<?>> values = ImmutableSet.builder();
        values.addAll(this.state.getValues());
        values.addAll(this.tileEntity.getValues());
        return values.build();
    }

    @Override
    public BlockSnapshot withState(BlockState blockState) {
        if (blockState.getType() == this.state.getType()) {
            return new LanternBlockSnapshot(this.location, blockState,
                    this.creator, this.notifier, this.tileEntity);
        }
        final LanternBlockEntity tileEntity = (LanternBlockEntity) ((LanternBlockType) blockState.getType()).getBlockEntityProvider()
                .map(provider -> provider.get(blockState, null, null))
                .orElse(null);
        if (tileEntity != null) {
            tileEntity.setBlock(blockState);
            if (this.tileEntity != null) {
                tileEntity.copyFromFastNoEvents(this.tileEntity);
            }
        }
        return new LanternBlockSnapshot(this.location, blockState,
                this.creator, this.notifier, tileEntity);
    }

    @Override
    public List<ImmutableDataManipulator<?, ?>> getContainers() {
        if (this.tileEntity == null) {
            return this.state.getManipulators();
        }
        final ImmutableList.Builder<ImmutableDataManipulator<?,?>> manipulators = ImmutableList.builder();
        manipulators.addAll(this.state.getManipulators());
        this.tileEntity.getContainers().forEach(manipulator -> manipulators.add(manipulator.asImmutable()));
        return manipulators.build();
    }

    @Override
    public UUID getWorldUniqueId() {
        if (this.location == null) {
            throw new IllegalStateException("This BlockSnapshot doesn't have a location.");
        }
        return this.location.getWorld().getUniqueId();
    }

    @Override
    public Vector3i getPosition() {
        if (this.location == null) {
            throw new IllegalStateException("This BlockSnapshot doesn't have a location.");
        }
        return this.location.getBlockPosition();
    }

    @Override
    public BlockSnapshot withLocation(Location location) {
        checkNotNull(location, "location");
        return new LanternBlockSnapshot(location, this.state,
                this.creator, this.notifier, this.tileEntity);
    }

    @Override
    public BlockSnapshot withContainer(DataContainer container) {
        return new LanternBlockSnapshotBuilder().build(container).get();
    }

    @Override
    public boolean restore(boolean force, BlockChangeFlag flag) {
        final Location loc = getLocation().orElseThrow(() -> new IllegalStateException("This BlockSnapshot doesn't have a location."));
        return restoreAt(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), force, flag);
    }

    public boolean restoreAt(World world, int x, int y, int z, boolean force, BlockChangeFlag flag) {
        if (!force && world.getBlock(x, y, z).getType() != this.state.getType()) {
            return false;
        }
        world.setBlock(x, y, z, this.state, flag);
        world.setCreator(x, y, z, this.creator);
        world.setNotifier(x, y, z, this.notifier);
        if (this.tileEntity != null) {
            final LanternBlockEntity tileEntity = (LanternBlockEntity) world.getTileEntity(x, y, z).orElse(null);
            if (tileEntity != null) {
                tileEntity.copyFromFastNoEvents(this.tileEntity);
            }
        }
        return true;
    }

    @Override
    public Optional<UUID> getCreator() {
        return Optional.ofNullable(this.creator);
    }

    @Override
    public Optional<UUID> getNotifier() {
        return Optional.ofNullable(this.notifier);
    }

    @Override
    public Optional<TileEntityArchetype> createArchetype() {
        return this.tileEntity == null ? Optional.empty() : Optional.of(this.tileEntity.createArchetype());
    }

    @Nullable
    static LanternBlockEntity copy(@Nullable LanternBlockEntity tileEntity) {
        return tileEntity == null ? null : LanternBlockEntityArchetype.copy(tileEntity);
    }
}
