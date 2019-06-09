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
package org.lanternpowered.server.data.value

import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.ToStringHelper
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue
import java.util.Comparator
import java.util.Objects

abstract class LanternBoundedValue<E : Any> protected constructor(
        key: Key<out BoundedValue<E>>, value: E, protected val min: E, protected val max: E, private val comparator: Comparator<E>
) : LanternValue<E>(key, value), BoundedValue<E> {

    override fun getKey() = super.getKey().uncheckedCast<Key<out BoundedValue<E>>>()

    override fun getMinValue() = this.min
    override fun getMaxValue() = this.max
    override fun getComparator() = this.comparator

    override fun hashCode() = Objects.hash(this.key, this.value, this.min, this.max, this.comparator)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        other as LanternBoundedValue<*>?
        return this.key == other.key
                && this.value == other.value
                && this.min == other.min
                && this.max == other.max
                && this.comparator == other.comparator
    }

    override fun toString() = ToStringHelper(this)
            .add("key", this.key)
            .add("value", this.value)
            .add("min", this.min)
            .add("max", this.max)
            .add("comparator", this.comparator)
            .toString()
}
