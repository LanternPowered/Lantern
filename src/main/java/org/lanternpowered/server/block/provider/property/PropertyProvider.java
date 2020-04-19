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
package org.lanternpowered.server.block.provider.property;

import org.lanternpowered.server.block.provider.BlockObjectProvider;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

import org.checkerframework.checker.nullness.qual.Nullable;

@FunctionalInterface
public interface PropertyProvider<V> extends BlockObjectProvider<V> {

    @Nullable
    @Override
    V get(BlockState blockState, @Nullable Location location, @Nullable Direction face);
}
