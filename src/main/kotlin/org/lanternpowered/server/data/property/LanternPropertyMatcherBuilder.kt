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
package org.lanternpowered.server.data.property

import org.spongepowered.api.data.property.Property
import org.spongepowered.api.data.property.PropertyMatcher

@Suppress("UNCHECKED_CAST")
class LanternPropertyMatcherBuilder<V> : PropertyMatcher.Builder<V> {

    private lateinit var operator: PropertyMatcher.Operator
    private var property: Property<V>? = null
    private var value: V? = null

    init {
        reset()
    }

    override fun <NV> property(property: Property<NV>) = apply {
        this.property = property as Property<V>
    } as LanternPropertyMatcherBuilder<NV>

    override fun operator(operator: PropertyMatcher.Operator) = apply { this.operator = operator }
    override fun value(value: V?) = apply { this.value = value }

    override fun build(): PropertyMatcher<V> {
        val property = checkNotNull(this.property) { "The property must be set" }
        return LanternPropertyMatcher(property, this.operator, this.value)
    }

    override fun from(value: PropertyMatcher<V>) = apply {
        this.property = value.property
        this.operator = value.operator
        this.value = value.value.orElse(null)
    }

    override fun reset() = apply {
        this.operator = PropertyMatcher.Operator.EQUAL
        this.property = null
        this.value = null
    }
}
