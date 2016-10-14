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
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

public class LanternBlockSnapshotBuilder implements BlockSnapshot.Builder {

    private UUID worldUUID;
    private BlockState blockState;
    private Vector3i position;

    @Nullable private UUID creator;
    @Nullable private UUID notifier;

    @Override
    public BlockSnapshot.Builder world(WorldProperties worldProperties) {
        this.worldUUID = checkNotNull(worldProperties, "worldProperties").getUniqueId();
        return this;
    }

    @Override
    public BlockSnapshot.Builder blockState(BlockState blockState) {
        this.blockState = checkNotNull(blockState, "blockState");
        return this;
    }

    @Override
    public BlockSnapshot.Builder position(Vector3i position) {
        this.position = checkNotNull(position, "position");
        return this;
    }

    @Override
    public BlockSnapshot.Builder from(Location<World> location) {
        checkNotNull(location, "location");
        this.worldUUID = location.getExtent().getProperties().getUniqueId();
        this.position = location.getBlockPosition();
        return this;
    }

    @Override
    public BlockSnapshot.Builder creator(UUID uuid) {
        this.creator = checkNotNull(uuid, "uuid");
        return this;
    }

    @Override
    public BlockSnapshot.Builder notifier(UUID uuid) {
        this.notifier = checkNotNull(uuid, "uuid");
        return this;
    }

    @Override
    public BlockSnapshot.Builder add(DataManipulator<?, ?> manipulator) {
        final Optional<BlockState> blockState = this.blockState.with(manipulator.asImmutable());
        if (blockState.isPresent()) {
            this.blockState = blockState.get();
        }
        return this;
    }

    @Override
    public BlockSnapshot.Builder add(ImmutableDataManipulator<?, ?> manipulator) {
        final Optional<BlockState> blockState = this.blockState.with(manipulator);
        if (blockState.isPresent()) {
            this.blockState = blockState.get();
        }
        return this;
    }

    @Override
    public <V> BlockSnapshot.Builder add(Key<? extends BaseValue<V>> key, V value) {
        final Optional<BlockState> blockState = this.blockState.with(key, value);
        if (blockState.isPresent()) {
            this.blockState = blockState.get();
        }
        return this;
    }

    @Override
    public BlockSnapshot.Builder from(BlockSnapshot holder) {
        final LanternBlockSnapshot snapshot = (LanternBlockSnapshot) checkNotNull(holder, "holder");
        this.creator = snapshot.getCreator().orElse(null);
        this.notifier = snapshot.getNotifier().orElse(null);
        this.worldUUID = snapshot.getWorldUniqueId();
        this.position = snapshot.getPosition();
        this.blockState = snapshot.getState();
        return this;
    }

    @Override
    public BlockSnapshot build() {
        checkState(this.position != null, "The position must be set.");
        checkState(this.blockState != null, "The block state must be set.");
        checkState(this.worldUUID != null, "The world must be set.");
        return new LanternBlockSnapshot(this.worldUUID, this.position, this.blockState,
                Optional.ofNullable(this.notifier), Optional.ofNullable(this.creator));
    }

    @Override
    public Optional<BlockSnapshot> build(DataView container) throws InvalidDataException {
        return null;
    }

    @Override
    public BlockSnapshot.Builder reset() {
        this.position = null;
        this.blockState = null;
        this.worldUUID = null;
        this.notifier = null;
        this.creator = null;
        return this;
    }
}
