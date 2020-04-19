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

import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import kotlin.reflect.KProperty

internal class MutableKeyElementProperty<V : Value<E>, E : Any, H : DataHolder>(key: Key<V>) :
        KeyElementProperty<V, E, H>(key), MutableDataHolderProperty<H, E> {

    override fun setValue(thisRef: H, property: KProperty<*>, value: E) {
        thisRef as MutableDataHolder
        thisRef.offerFast(this.key, value)
    }
}
