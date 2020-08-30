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
package org.lanternpowered.server.util.collections

class SlicedArrayIterator<T>(
        private val array: Array<T>,
        offset: Int = 0,
        private val size: Int = array.size
) : Iterator<T> {

    private var index = offset

    override fun hasNext(): Boolean = this.index < this.size

    override fun next(): T {
        if (!this.hasNext())
            throw NoSuchElementException()
        return this.array[this.index++]
    }
}
