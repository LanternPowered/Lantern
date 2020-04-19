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
