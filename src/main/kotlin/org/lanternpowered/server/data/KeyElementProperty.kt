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

internal open class KeyElementProperty<V : Value<E>, E : Any, H : DataHolder>(protected val key: Key<V>) : DataHolderProperty<H, E> {

    override fun getValue(thisRef: H, property: KProperty<*>): E =
            thisRef[this.key].orElseThrow { IllegalStateException("The key ${key.key} isn't present.") }
}
