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
package org.lanternpowered.server.data.property.common;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.data.property.DirectionRelativePropertyStore;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.property.PropertyHolder;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

import javax.annotation.Nullable;

public abstract class AbstractBlockPropertyStore<T extends Property<?, ?>> extends AbstractLanternPropertyStore<T>
        implements DirectionRelativePropertyStore<T> {

    protected abstract Optional<T> getFor(BlockState blockState, @Nullable Location<World> location, @Nullable Direction direction);

    @Override
    public Optional<T> getFor(PropertyHolder propertyHolder) {
        return this.getFor0(propertyHolder, null);
    }

    @Override
    public Optional<T> getFor(PropertyHolder propertyHolder, Direction direction) {
        return this.getFor0(propertyHolder, checkNotNull(direction, "direction"));
    }

    @Override
    public Optional<T> getFor(Location<World> location, Direction direction) {
        return this.getFor0(location, checkNotNull(direction, "direction"));
    }

    private Optional<T> getFor0(PropertyHolder propertyHolder, @Nullable Direction direction) {
        checkNotNull(propertyHolder, "propertyHolder");
        if (propertyHolder instanceof BlockState) {
            return this.getFor((BlockState) propertyHolder, null, direction);
        } else if (propertyHolder instanceof BlockType) {
            return this.getFor(((BlockType) propertyHolder).getDefaultState(), null, direction);
        } else if (propertyHolder instanceof Location) {
            return this.getFor0((Location<World>) propertyHolder, direction);
        } else if (propertyHolder instanceof ItemType) {
            Optional<BlockType> type = ((ItemType) propertyHolder).getBlock();
            if (type.isPresent()) {
                return this.getFor(type.get().getDefaultState(), null, direction);
            }
        } else if (propertyHolder instanceof ItemStack) {
            Optional<BlockType> type = ((ItemStack) propertyHolder).getItem().getBlock();
            if (type.isPresent()) {
                return this.getFor(((LanternBlockType) type.get()).getStateFromItemStack((ItemStack) propertyHolder), null, direction);
            }
        }
        return Optional.empty();
    }

    private Optional<T> getFor0(Location<World> propertyHolder, @Nullable Direction direction) {
        try {
            return this.getFor(propertyHolder.getBlock(), propertyHolder, direction);
        // Can be thrown if the extent is gc
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }
}
