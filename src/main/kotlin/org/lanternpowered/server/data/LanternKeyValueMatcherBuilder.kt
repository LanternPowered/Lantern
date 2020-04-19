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
