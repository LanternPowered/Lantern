/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.block.provider;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.state.LanternBlockState;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("unchecked")
public class CachedSimpleObjectProvider<T> implements BlockObjectProvider<T> {

    private final Function<BlockState, T> function;
    private final Object[] values;

    public CachedSimpleObjectProvider(LanternBlockType blockType, Function<BlockState, T> simpleObjectProvider) {
        checkNotNull(blockType, "blockType");
        checkNotNull(simpleObjectProvider, "simpleObjectProvider");
        final Collection<BlockState> blockStates = blockType.getAllBlockStates();
        final Object[] values = new Object[blockStates.size()];
        for (BlockState blockState : blockStates) {
            values[((LanternBlockState) blockState).getInternalId()] = simpleObjectProvider.apply(blockState);
        }
        this.function = simpleObjectProvider;
        this.values = values;
    }

    @Override
    public T get(BlockState blockState, @Nullable Location location, @Nullable Direction face) {
        return (T) this.values[((LanternBlockState) blockState).getInternalId()];
    }

    public List<T> getValues() {
        return (List) Lists.newArrayList(this.values);
    }

    public Function<BlockState, T> getFunction() {
        return this.function;
    }
}
