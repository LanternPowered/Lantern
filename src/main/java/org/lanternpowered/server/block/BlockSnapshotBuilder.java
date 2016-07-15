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
import static com.google.common.base.Preconditions.checkState;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

// Using this class directly makes intellij complain,
// so let's just use a subclass until it's fixed.
public abstract class BlockSnapshotBuilder extends AbstractDataBuilder<BlockSnapshot> implements BlockSnapshot.Builder {

    public static BlockSnapshotBuilder create() {
        return new LanternBlockSnapshotBuilder();
    }

    public static BlockSnapshotBuilder createPositionless() {
        return new Positionless();
    }

    private static final class Positionless extends BlockSnapshotBuilder {
    }

    UUID worldUUID;
    private BlockState blockState;
    @Nullable private BlockState extendedBlockState;
    Vector3i position;

    @Nullable private UUID creator;
    @Nullable private UUID notifier;

    BlockSnapshotBuilder() {
        super(BlockSnapshot.class, 1);
    }

    /**
     * Sets the coordinates and world of this {@link BlockSnapshot} from a {@link Location}.
     *
     * @param location The location
     * @return This builder, for chaining
     */
    public BlockSnapshotBuilder location(Location<World> location) {
        this.worldUUID = location == null ? null : location.getExtent().getProperties().getUniqueId();
        this.position = location == null ? null : location.getBlockPosition();
        return this;
    }

    @Override
    public BlockSnapshotBuilder world(WorldProperties worldProperties) {
        this.worldUUID = worldProperties == null ? null : worldProperties.getUniqueId();
        return this;
    }

    @Override
    public BlockSnapshotBuilder blockState(BlockState blockState) {
        this.blockState = checkNotNull(blockState, "blockState");
        this.extendedBlockState = null;
        return this;
    }

    @Override
    public BlockSnapshotBuilder position(Vector3i position) {
        this.position = position;
        return this;
    }

    @Override
    public BlockSnapshotBuilder from(Location<World> location) {
        checkNotNull(location, "location");
        this.worldUUID = location.getExtent().getProperties().getUniqueId();
        this.position = location.getBlockPosition();
        this.blockState = location.getBlock();
        final World world = location.getExtent();
        this.creator = world.getCreator(location.getBlockPosition()).orElse(null);
        this.notifier = world.getNotifier(location.getBlockPosition()).orElse(null);
        final BlockState extendedState = ((LanternBlockType) this.blockState.getType())
                .getExtendedBlockStateProvider().get(this.blockState, location, null);
        this.extendedBlockState = extendedState == this.blockState ? null : extendedState;
        return this;
    }

    @Override
    public BlockSnapshotBuilder creator(UUID uuid) {
        this.creator = checkNotNull(uuid, "uuid");
        return this;
    }

    @Override
    public BlockSnapshotBuilder notifier(UUID uuid) {
        this.notifier = checkNotNull(uuid, "uuid");
        return this;
    }

    @Override
    public BlockSnapshotBuilder add(DataManipulator<?, ?> manipulator) {
        checkState(this.blockState != null, "The block state must be set before you can add data manipulators.");
        final Optional<BlockState> blockState = this.blockState.with(manipulator.asImmutable());
        if (blockState.isPresent()) {
            this.blockState = blockState.get();
        }
        return this;
    }

    @Override
    public BlockSnapshotBuilder add(ImmutableDataManipulator<?, ?> manipulator) {
        checkState(this.blockState != null, "The block state must be set before you can add data manipulators.");
        final Optional<BlockState> blockState = this.blockState.with(manipulator);
        if (blockState.isPresent()) {
            this.blockState = blockState.get();
        }
        return this;
    }

    @Override
    public <V> BlockSnapshotBuilder add(Key<? extends BaseValue<V>> key, V value) {
        checkState(this.blockState != null, "The block state must be set before you can add key values.");
        final Optional<BlockState> blockState = this.blockState.with(key, value);
        if (blockState.isPresent()) {
            this.blockState = blockState.get();
        }
        return this;
    }

    @Override
    public BlockSnapshotBuilder from(BlockSnapshot holder) {
        final LanternBlockSnapshot snapshot = (LanternBlockSnapshot) checkNotNull(holder, "holder");
        this.creator = snapshot.getCreator().orElse(null);
        this.notifier = snapshot.getNotifier().orElse(null);
        this.blockState = snapshot.getState();
        final BlockState extendedState = holder.getExtendedState();
        this.extendedBlockState = extendedState == this.blockState ? null : extendedState;
        final LanternBlockSnapshot.BlockLocation blockLocation = snapshot.location;
        //noinspection ConstantConditions
        this.worldUUID = blockLocation == null ? null : blockLocation.world.getUniqueId();
        //noinspection ConstantConditions
        this.position = blockLocation == null ? null : blockLocation.position;
        return this;
    }

    @Override
    public BlockSnapshot build() {
        checkState(this.blockState != null, "The block state must be set.");
        final LanternBlockSnapshot.BlockLocation blockLocation = this.worldUUID == null  || this.position == null ? null :
                new LanternBlockSnapshot.BlockLocation(this.worldUUID, this.position);
        return new LanternBlockSnapshot(blockLocation, this.blockState,
                this.extendedBlockState, Optional.ofNullable(this.creator), Optional.ofNullable(this.notifier));
    }

    @Override
    public Optional<BlockSnapshot> buildContent(DataView container) throws InvalidDataException {
        return null;
    }

    @Override
    public BlockSnapshotBuilder reset() {
        this.position = null;
        this.blockState = null;
        this.extendedBlockState = null;
        this.worldUUID = null;
        this.notifier = null;
        this.creator = null;
        return this;
    }
}
