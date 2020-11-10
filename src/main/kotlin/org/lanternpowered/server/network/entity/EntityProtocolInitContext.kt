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
package org.lanternpowered.server.network.entity

interface EntityProtocolInitContext {
    /**
     * Acquires the next free id.
     *
     * @return The id
     */
    fun acquire(): Int

    fun acquire(count: Int): IntArray

    fun acquire(array: IntArray): IntArray

    /**
     * Similar to [acquire], but all the ids are following
     * the last one. For example: int[4] -> { 5, 6, 7, 8 }
     *
     * @return The ids
     */
    fun acquireSequence(count: Int): IntArray

    /**
     * Similar to [acquire], but all the ids are following
     * the last one. For example: int[4] -> { 5, 6, 7, 8 }
     *
     * @return The ids
     */
    fun acquireSequence(array: IntArray): IntArray

    /**
     * Releases the id so that it can be reused.
     *
     * @param id The id
     */
    fun release(id: Int)

    fun release(array: IntArray)
}
