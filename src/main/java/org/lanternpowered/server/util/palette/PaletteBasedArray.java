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
package org.lanternpowered.server.util.palette;

import org.lanternpowered.server.util.collect.array.VariableValueArray;

public interface PaletteBasedArray<T> {

    /**
     * Gets the capacity.
     *
     * @return The capacity
     */
    int getCapacity();

    /**
     * Gets the {@link T} at the given index.
     *
     * @param index The index
     * @return The object
     */
    T get(int index);

    /**
     * Sets the {@link T} at the given index.
     *
     * @param index The index
     * @param object The object
     * @return The previous assigned value
     */
    T set(int index, T object);

    /**
     * Gets the {@link Palette} of this palette based object
     * array. Does not allow removal of {@link T}s.
     *
     * @return The object palette
     */
    Palette<T> getPalette();

    /**
     * Gets the backing {@link VariableValueArray} which
     * holds the integer ids.
     *
     * @return The backing ids array
     */
    VariableValueArray getBacking();

    /**
     * Creates a copy of this {@link PaletteBasedArray}.
     *
     * @return The copy
     */
    PaletteBasedArray<T> copy();
}
