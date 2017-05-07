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
package org.lanternpowered.server.block.provider;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.provider.ObjectProvider;
import org.lanternpowered.server.block.state.LanternBlockState;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

public class CachedSimpleObjectProvider<T> implements ObjectProvider<T> {

    private final Object[] values;

    public CachedSimpleObjectProvider(LanternBlockType blockType, Function<BlockState, T> simpleObjectProvider) {
        checkNotNull(blockType, "blockType");
        checkNotNull(simpleObjectProvider, "simpleObjectProvider");
        final Collection<BlockState> blockStates = blockType.getAllBlockStates();
        final Object[] values = new Object[blockStates.size()];
        for (BlockState blockState : blockStates) {
            values[((LanternBlockState) blockState).getInternalId()] = simpleObjectProvider.apply(blockState);
        }
        this.values = values;
    }

    @Override
    public T get(BlockState blockState, @Nullable Location<World> location, @Nullable Direction face) {
        //noinspection unchecked
        return (T) this.values[((LanternBlockState) blockState).getInternalId()];
    }

    public List<T> getValues() {
        //noinspection unchecked
        return (List) Lists.newArrayList(this.values);
    }
}
