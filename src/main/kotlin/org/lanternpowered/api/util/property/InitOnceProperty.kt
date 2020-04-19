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
package org.lanternpowered.api.util.property

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class InitOnceProperty<T> : ReadWriteProperty<Any, T> {

    /**
     * The actual value.
     */
    private var value: Any = Empty

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return if (value != Empty) value as T else throw IllegalStateException("Value isn't initialized")
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = if (this.value == Empty) value as Any else throw IllegalStateException("Value is initialized")
    }

    companion object {

        /**
         * A object which represents "empty".
         */
        private object Empty
    }
}
