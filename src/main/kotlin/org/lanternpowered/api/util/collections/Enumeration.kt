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

import java.util.Enumeration

fun <T> Sequence<T>.asEnumeration(): Enumeration<T> = this.iterator().asEnumeration()
fun <T> Iterator<T>.asEnumeration(): Enumeration<T> = IteratorEnumeration(this)

private class IteratorEnumeration<T>(private val iterator: Iterator<T>) : Enumeration<T> {
    override fun hasMoreElements(): Boolean = this.iterator.hasNext()
    override fun nextElement(): T = this.iterator.next()
}
