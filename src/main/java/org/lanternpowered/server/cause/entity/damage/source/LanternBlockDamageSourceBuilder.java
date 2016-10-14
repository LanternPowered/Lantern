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
package org.lanternpowered.server.cause.entity.damage.source;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.event.cause.entity.damage.source.BlockDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.common.AbstractDamageSourceBuilder;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class LanternBlockDamageSourceBuilder extends AbstractDamageSourceBuilder<BlockDamageSource, BlockDamageSource.Builder>
        implements BlockDamageSource.Builder {

    protected Location<World> location;
    protected BlockSnapshot blockSnapshot;

    @Override
    public BlockDamageSource.Builder block(Location<World> location) {
        this.location = checkNotNull(location, "location");
        if (this.blockSnapshot == null) {
            this.blockSnapshot = location.createSnapshot();
        }
        return this;
    }

    @Override
    public BlockDamageSource.Builder block(BlockSnapshot blockSnapshot) {
        this.blockSnapshot = checkNotNull(blockSnapshot, "blockSnapshot");
        if (this.location == null) {
            this.location = blockSnapshot.getLocation().orElse(null);
        }
        return this;
    }

    @Override
    public BlockDamageSource build() throws IllegalStateException {
        Location<World> location = this.location;
        BlockSnapshot blockSnapshot = this.blockSnapshot;
        if (location == null && blockSnapshot != null) {
            this.location = blockSnapshot.getLocation().orElse(null);
        } else if (location != null && blockSnapshot == null) {
            this.blockSnapshot = location.createSnapshot();
        }
        checkState(location != null, "The location must be set");
        checkState(blockSnapshot != null, "The block snapshot must be set");
        return new LanternBlockDamageSource(this, location, blockSnapshot);
    }
}
