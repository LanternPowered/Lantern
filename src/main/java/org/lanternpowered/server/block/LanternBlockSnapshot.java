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
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.world.WeakWorldReference;
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
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

public class LanternBlockSnapshot implements BlockSnapshot, AbstractPropertyHolder {

    private static Cause RESTORE_CAUSE = Cause.of(NamedCause.of("EMPTY", "EMPTY"));

    /**
     * Represents the {@link Location} of a block.
     */
    static final class BlockLocation {

        final WeakWorldReference world;
        final Vector3i position;

        public BlockLocation(Location<World> location) {
            this(new WeakWorldReference(location.getExtent()), location.getBlockPosition());
        }

        public BlockLocation(World world, Vector3i position) {
            this(new WeakWorldReference(world), position);
        }

        public BlockLocation(UUID worldUUID, Vector3i position) {
            this(new WeakWorldReference(worldUUID), position);
        }

        private BlockLocation(WeakWorldReference world, Vector3i position) {
            this.position = checkNotNull(position, "position");
            this.world = world;
        }
    }

    @Nullable final BlockLocation location;
    private final BlockState state;
    @Nullable private final BlockState extendedState;
    private final Optional<UUID> notifier;
    private final Optional<UUID> creator;
    @Nullable final Map<Key<?>, Object> tileEntityData;

    public LanternBlockSnapshot(Location<World> location, BlockState blockState, @Nullable BlockState extendedState,
            Optional<UUID> creator, Optional<UUID> notifier, @Nullable Map<Key<?>, Object> tileEntityData) {
        this(new BlockLocation(checkNotNull(location, "location")), blockState, extendedState, creator, notifier, tileEntityData);
    }

    public LanternBlockSnapshot(UUID worldUUID, Vector3i position, BlockState blockState, @Nullable BlockState extendedState,
            Optional<UUID> creator, Optional<UUID> notifier, @Nullable Map<Key<?>, Object> tileEntityData) {
        this(new BlockLocation(worldUUID, position), blockState, extendedState, creator, notifier, tileEntityData);
    }

    public LanternBlockSnapshot(BlockState blockState, @Nullable BlockState extendedState,
            Optional<UUID> notifier, Optional<UUID> creator, @Nullable Map<Key<?>, Object> tileEntityData) {
        this((BlockLocation) null, blockState, extendedState, creator, notifier, tileEntityData);
    }

    LanternBlockSnapshot(@Nullable BlockLocation location, BlockState blockState, @Nullable BlockState extendedState,
            Optional<UUID> creator, Optional<UUID> notifier, @Nullable Map<Key<?>, Object> tileEntityData) {
        this.extendedState = extendedState;
        this.notifier = checkNotNull(notifier, "notifier");
        this.creator = checkNotNull(creator, "creator");
        this.state = checkNotNull(blockState, "blockState");
        this.tileEntityData = tileEntityData;
        this.location = location;
    }

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
            container.set(Queries.WORLD_ID, this.location.world.getUniqueId());
            final DataView positionView = container.createView(DataQueries.SNAPSHOT_WORLD_POSITION);
            positionView.set(Queries.POSITION_X, this.location.position.getX());
            positionView.set(Queries.POSITION_Y, this.location.position.getY());
            positionView.set(Queries.POSITION_Z, this.location.position.getZ());
        }
        this.notifier.ifPresent(notifier -> container.set(Queries.NOTIFIER_ID, notifier));
        this.creator.ifPresent(creator -> container.set(Queries.CREATOR_ID, creator));
        return container;
    }

    @Override
    public BlockState getExtendedState() {
        return this.extendedState == null ? this.state : this.extendedState;
    }

    @Override
    public BlockState getState() {
        return this.state;
    }

    @Override
    public LanternBlockSnapshot copy() {
        return new LanternBlockSnapshot(this.location, this.state, extendedState, this.creator, this.notifier, tileEntityData);
    }

    @Override
    public Optional<Location<World>> getLocation() {
        if (this.location == null) {
            return Optional.empty();
        }
        Optional<World> world = this.location.world.getWorld();
        if (!world.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(new Location<>(world.get(), this.location.position));
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
        if (this.location == null) {
            throw new IllegalStateException("This BlockSnapshot doesn't have a location.");
        }
        return this.location.world.getUniqueId();
    }

    @Override
    public Vector3i getPosition() {
        if (this.location == null) {
            throw new IllegalStateException("This BlockSnapshot doesn't have a location.");
        }
        return this.location.position;
    }

    @Override
    public BlockSnapshot withLocation(Location<World> location) {
        checkNotNull(location, "location");
        return new LanternBlockSnapshot(location, this.state, extendedState, this.creator, this.notifier, tileEntityData);
    }

    @Override
    public BlockSnapshot withContainer(DataContainer container) {
        return new LanternBlockSnapshotBuilder().build(container).get();
    }

    @Override
    public boolean restore(boolean force, BlockChangeFlag flag) {
        if (this.location == null) {
            throw new IllegalStateException("This BlockSnapshot doesn't have a location.");
        }
        Location<World> loc = this.getLocation().orElse(null);
        if (loc == null || (!force && loc.getBlockType() != this.state.getType())) {
            return false;
        }
        loc.setBlock(this.state, flag, RESTORE_CAUSE);
        final World world = loc.getExtent();
        world.setCreator(this.location.position, this.creator.orElse(null));
        world.setNotifier(this.location.position, this.notifier.orElse(null));
        if (this.tileEntityData != null) {
            final TileEntity tileEntity = loc.getTileEntity().orElse(null);
            if (tileEntity != null) {
                //noinspection unchecked
                this.tileEntityData.forEach((key, value) -> tileEntity.offer((Key) key, value));
            }
        }
        return true;
    }

    @Override
    public Optional<UUID> getCreator() {
        return this.creator;
    }

    @Override
    public Optional<UUID> getNotifier() {
        return this.notifier;
    }

    @Override
    public Optional<TileEntityArchetype> createArchetype() {
        return null;
    }
}
