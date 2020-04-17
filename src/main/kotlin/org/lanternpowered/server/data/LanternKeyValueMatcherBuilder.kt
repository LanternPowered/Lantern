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
package org.lanternpowered.server.data

import org.lanternpowered.server.data.key.ValueKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.KeyValueMatcher
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.value.Value
import java.util.Optional

@Suppress("UNCHECKED_CAST")
class LanternKeyValueMatcherBuilder<V : Any> : KeyValueMatcher.Builder<V> {

    private lateinit var operator: KeyValueMatcher.Operator
    private var key: ValueKey<out Value<V>, V>? = null
    private var value: V? = null

    init {
        reset()
    }

    override fun <NV : Any> key(key: Key<out Value<NV>>) = apply {
        this.key = key as ValueKey<out Value<V>, V>
    } as LanternKeyValueMatcherBuilder<NV>

    override fun value(value: Value<out V>?) = apply {
        if (value != null) {
            this.key = value.key as ValueKey<out Value<V>, V>
            this.value = value.get()
        } else {
            this.value = null
        }
    }

    override fun operator(operator: KeyValueMatcher.Operator) = apply { this.operator = operator }
    override fun value(value: V?) = apply { this.value = value }

    override fun build(): KeyValueMatcher<V> {
        val key = checkNotNull(this.key) { "The key must be set" }
        return LanternKeyValueMatcher(key, this.operator, this.value)
    }

    override fun from(value: KeyValueMatcher<V>) = apply {
        key(value.key)
        this.operator = value.operator
        this.value = value.value.orElse(null)
    }

    override fun reset() = apply {
        this.operator = KeyValueMatcher.Operator.EQUAL
        this.key = null
        this.value = null
    }

    override fun build(container: DataView): Optional<KeyValueMatcher<V>> {
        TODO()
    }
}
