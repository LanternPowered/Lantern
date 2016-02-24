/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
import org.lanternpowered.server.data.util.DataQueries;
import org.lanternpowered.server.world.WeakWorldReference;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

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
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer()
            .set(Queries.WORLD_ID, this.world.getUniqueId().toString())
            .createView(DataQueries.SNAPSHOT_WORLD_POSITION)
                .set(Queries.POSITION_X, this.position.getX())
                .set(Queries.POSITION_Y, this.position.getY())
                .set(Queries.POSITION_Z, this.position.getZ())
            .getContainer()
            .set(DataQueries.BLOCK_STATE, this.state);
    }

    @Override
    public BlockState getExtendedState() {
        // TODO Auto-generated method stub
        return this.state;
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
    public Optional<UUID> getCreator() {
        return Optional.empty();
    }

    @Override
    public Optional<UUID> getNotifier() {
        return Optional.empty();
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
