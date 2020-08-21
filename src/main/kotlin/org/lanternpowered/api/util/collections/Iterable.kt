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
package org.lanternpowered.api.util.collections

fun <T> Iterable<T>.contentEquals(other: Iterable<T>): Boolean {
    if (this is Collection<*> && other is Collection<*>)
        return this.contentEquals(other)
    return this.contentEquals0(other)
}

fun <T> Collection<T>.contentEquals(other: Collection<T>): Boolean {
    if (this.size != other.size)
        return false
    return this.contentEquals0(other)
}

fun <T> Iterable<T>.contentEquals0(that: Iterable<T>): Boolean {
    val thisItr = this.iterator()
    val thatItr = that.iterator()
    while (thisItr.hasNext()) {
        val thisValue = thisItr.next()
        if (!thatItr.hasNext())
            return false
        if (thisValue != thatItr.next())
            return false
    }
    return true
}
