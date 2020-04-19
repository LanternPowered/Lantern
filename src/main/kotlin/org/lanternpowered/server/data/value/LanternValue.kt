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
package org.lanternpowered.server.data.value

import org.lanternpowered.api.util.ToStringHelper
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Objects

abstract class LanternValue<E : Any> protected constructor(
        private val key: Key<out Value<E>>,
        protected var value: E
) : Value<E> {

    override fun get() = this.value
    override fun getKey() = this.key

    override fun hashCode() = Objects.hash(this.key, this.value)

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null || javaClass != other.javaClass)
            return false
        other as LanternValue<*>
        return this.key == other.key && this.value == other.value
    }

    override fun toString() = ToStringHelper(this)
            .add("key", this.key)
            .add("value", this.value)
            .toString()
}
