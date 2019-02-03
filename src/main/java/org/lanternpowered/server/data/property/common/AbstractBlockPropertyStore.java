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
import org.spongepowered.api.data.property.PropertyHolder;
import org.spongepowered.api.data.property.store.PropertyStore;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractBlockPropertyStore<V> implements PropertyStore<V>, DirectionRelativePropertyStore<V> {

    protected abstract Optional<V> getFor(BlockState blockState, @Nullable Location location, @Nullable Direction direction);

    @Override
    public Optional<V> getFor(PropertyHolder propertyHolder) {
        return getFor0(propertyHolder, null);
    }

    @Override
    public Optional<V> getFor(PropertyHolder propertyHolder, Direction direction) {
        return getFor0(propertyHolder, checkNotNull(direction, "direction"));
    }

    @Override
    public Optional<V> getFor(Location location, Direction direction) {
        return getFor0(location, checkNotNull(direction, "direction"));
    }

    private Optional<V> getFor0(PropertyHolder propertyHolder, @Nullable Direction direction) {
        checkNotNull(propertyHolder, "propertyHolder");
        if (propertyHolder instanceof BlockState) {
            return getFor((BlockState) propertyHolder, null, direction);
        } else if (propertyHolder instanceof BlockType) {
            return getFor(((BlockType) propertyHolder).getDefaultState(), null, direction);
        } else if (propertyHolder instanceof Location) {
            return getFor0((Location) propertyHolder, direction);
        } else if (propertyHolder instanceof ItemType) {
            final Optional<BlockType> type = ((ItemType) propertyHolder).getBlock();
            if (type.isPresent()) {
                return getFor(type.get().getDefaultState(), null, direction);
            }
        } else if (propertyHolder instanceof ItemStack) {
            final Optional<BlockType> type = ((ItemStack) propertyHolder).getType().getBlock();
            if (type.isPresent()) {
                return getFor(((LanternBlockType) type.get()).getStateFromItemStack((ItemStack) propertyHolder), null, direction);
            }
        }
        return Optional.empty();
    }

    private Optional<V> getFor0(Location propertyHolder, @Nullable Direction direction) {
        try {
            return getFor(propertyHolder.getBlock(), propertyHolder, direction);
        // Can be thrown if the extent is gc
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }
}
