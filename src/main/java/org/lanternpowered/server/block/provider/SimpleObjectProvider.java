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

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

public class SimpleObjectProvider<T> implements BlockObjectProvider<T> {

    private final Function<BlockState, T> provider;

    public SimpleObjectProvider(Function<BlockState, T> provider) {
        this.provider = provider;
    }

    @Override
    public T get(BlockState blockState, @Nullable Location location, @Nullable Direction face) {
        return this.provider.apply(blockState);
    }

    public Function<BlockState, T> getFunction() {
        return this.provider;
    }
}
