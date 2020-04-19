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
package org.lanternpowered.server.network.entity;

public interface EntityProtocolInitContext {

    /**
     * Acquires the next free id.
     *
     * @return The id
     */
    int acquire();

    int[] acquire(int count);

    int[] acquire(int[] array);

    /**
     * Similar to {@link #acquire()}, but all the ids are following
     * the last one. For example: int[4] -> { 5, 6, 7, 8 }
     *
     * @return The ids
     */
    // TODO: Better name?
    int[] acquireRow(int count);

    /**
     * Similar to {@link #acquire()}, but all the ids are following
     * the last one. For example: int[4] -> { 5, 6, 7, 8 }
     *
     * @return The ids
     */
    // TODO: Better name?
    int[] acquireRow(int[] array);

    /**
     * Releases the id so that it can be reused.
     *
     * @param id The id
     */
    void release(int id);

    void release(int[] array);
}
