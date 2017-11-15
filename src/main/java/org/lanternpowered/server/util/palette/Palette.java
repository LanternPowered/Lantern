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

import java.util.Collection;
import java.util.Optional;

@SuppressWarnings({"unchecked"})
public interface Palette<T> {

    /**
     * Represents a invalid id.
     */
    int INVALID_ID = -1;

    /**
     * Gets the {@link PaletteType} of this palette.
     *
     * @return The palette type
     */
    PaletteType getType();

    /**
     * Gets the id for the given {@link T}.
     *
     * @param object The object
     * @return The id, or {@link Optional#empty()} if not found
     */
    default Optional<Integer> get(T object) {
        final int id = getId(object);
        return id == INVALID_ID ? Optional.empty() : Optional.of(id);
    }

    /**
     * Gets the id for the given {@link T}.
     *
     * @param object The object
     * @return The id, or {@link #INVALID_ID} if not found
     */
    int getId(T object);

    /**
     * Gets or assigns the id for the given {@link T}.
     *
     * @param object The object
     * @return The id
     */
    int getOrAssign(T object);

    /**
     * Gets the object that is assigned to the given id.
     *
     * @param id The id
     * @return The object, or {@link Optional#empty()} if not found
     */
    Optional<T> get(int id);

    /**
     * Assigns multiple {@link T}s at the same time.
     * <p>Using this method has performance wise a benefit over
     * calling {@link #getOrAssign(T)} multiple times.
     *
     * @param objects The objects
     * @return The assigned ids
     */
    default int[] getOrAssign(T... objects) {
        final int[] ids = new int[objects.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = getOrAssign(objects[i]);
        }
        return ids;
    }

    /**
     * Gets all the objects that are assigned.
     *
     * @return The assigned objects
     */
    Collection<T> getEntries();

    /**
     * Gets the size of the palette. (the amount of objects that are assigned.)
     *
     * @return The size of the palette
     */
    int size();
}
