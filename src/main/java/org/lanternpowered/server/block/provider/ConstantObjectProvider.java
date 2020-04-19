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

import org.checkerframework.checker.nullness.qual.Nullable;

public class ConstantObjectProvider<T> implements BlockObjectProvider<T> {

    private final T value;

    public ConstantObjectProvider(T value) {
        this.value = value;
    }

    @Override
    public T get(BlockState blockState, @Nullable Location location, @Nullable Direction face) {
        return this.value;
    }
}
