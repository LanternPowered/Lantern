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
import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.world.WeakWorldReference;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

public class LanternLocatableBlockBuilder extends AbstractDataBuilder<LocatableBlock> implements LocatableBlock.Builder {

    @Nullable public BlockState blockState;
    @Nullable public Vector3i position;
    @Nullable public WeakWorldReference world;
    @Nullable public Location location;

    public LanternLocatableBlockBuilder() {
        super(LocatableBlock.class, 1);
    }

    @Override
    public LocatableBlock.Builder state(BlockState blockState) {
        this.blockState = checkNotNull(blockState, "blockState");
        return this;
    }

    @Override
    public LocatableBlock.Builder location(Location location) {
        checkNotNull(location, "location");
        this.blockState = location.getBlock();
        this.position = location.getBlockPosition();
        this.world = new WeakWorldReference(location.getWorld());
        this.location = location;
        return this;
    }

    @Override
    public LocatableBlock.Builder position(Vector3i position) {
        this.position = checkNotNull(position, "position");
        this.location = null;
        return this;
    }

    @Override
    public LocatableBlock.Builder position(int x, int y, int z) {
        this.position = new Vector3i(x, y, z);
        this.location = null;
        return this;
    }

    @Override
    public LocatableBlock.Builder world(World world) {
        checkNotNull(world, "world");
        this.world = new WeakWorldReference(world);
        this.location = null;
        return this;
    }

    @Override
    public LocatableBlock.Builder from(LocatableBlock value) {
        checkNotNull(value, "value");
        final LanternLocatableBlock block = (LanternLocatableBlock) value;
        this.blockState = block.blockState;
        final Location location = block.location;
        this.position = block.location.getPosition().toInt();
        this.world = location.getWorldIfAvailable().map(WeakWorldReference::new).orElse(null);
        this.location = null;
        return this;
    }

    @Override
    public LocatableBlock build() {
        checkNotNull(this.position, "position must be set");
        checkNotNull(this.world, "world must be set");
        BlockState blockState = this.blockState;
        if (blockState == null) {
            final World world = this.world.getWorld().orElseThrow(() -> new IllegalStateException("World is unavailable."));
            blockState = world.getBlock(this.position);
        }
        final Location location;
        if (this.location != null) {
            location = this.location;
        } else {
            location = this.world.toLocation(this.position);
        }
        return new LanternLocatableBlock(location, blockState);
    }

    @Override
    public LanternLocatableBlockBuilder reset() {
        this.world = null;
        this.location = null;
        this.position = null;
        this.blockState = null;
        return this;
    }

    @Override
    protected Optional<LocatableBlock> buildContent(DataView container) throws InvalidDataException {
        final int x = container.getInt(Queries.POSITION_X)
                .orElseThrow(() -> new InvalidDataException("Could not locate an \"x\" coordinate in the container!"));
        final int y = container.getInt(Queries.POSITION_Y)
                .orElseThrow(() -> new InvalidDataException("Could not locate an \"y\" coordinate in the container!"));
        final int z = container.getInt(Queries.POSITION_Z)
                .orElseThrow(() -> new InvalidDataException("Could not locate an \"z\" coordinate in the container!"));
        final BlockState blockState = container.getCatalogType(DataQueries.BLOCK_STATE, BlockState.class)
                .orElseThrow(() -> new InvalidDataException("Could not locate a BlockState"));
        final UUID worldId = container.getObject(Queries.WORLD_ID, UUID.class)
                .orElseThrow(() -> new InvalidDataException("Could not locate a UUID"));
        return Sponge.getServer().getWorld(worldId)
                .map(world -> new LanternLocatableBlockBuilder()
                        .position(x, y, z)
                        .world(world)
                        .state(blockState)
                        .build());
    }
}
