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

import org.lanternpowered.api.util.optional.orNull
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import kotlin.reflect.KProperty

internal open class OptionalKeyElementProperty<V : Value<E>, E : Any, H : DataHolder>(val key: Key<V>) : DataHolderProperty<H, E?> {

    override fun getValue(thisRef: H, property: KProperty<*>) = thisRef[this.key].orNull()
}
