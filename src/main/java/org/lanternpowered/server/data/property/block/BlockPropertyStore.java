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
package org.lanternpowered.server.data.property.block;

import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.provider.property.PropertyProvider;
import org.lanternpowered.server.data.property.common.AbstractBlockPropertyStore;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class BlockPropertyStore<V> extends AbstractBlockPropertyStore<V> {

    private final Class<V> propertyType;

    public BlockPropertyStore(Class<V> propertyType) {
        this.propertyType = propertyType;
    }

    @Override
    protected Optional<V> getFor(BlockState blockState, @Nullable Location location,
             @Nullable Direction direction) {
        final Optional<PropertyProvider<V>> provider = ((LanternBlockType) blockState.getType())
                .getPropertyProviderCollection().get(this.propertyType);
        return provider.isPresent() ? Optional.of(provider.get().get(blockState, location, direction)) : Optional.empty();
    }
}
