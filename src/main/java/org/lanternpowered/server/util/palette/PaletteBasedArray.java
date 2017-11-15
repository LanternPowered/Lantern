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
