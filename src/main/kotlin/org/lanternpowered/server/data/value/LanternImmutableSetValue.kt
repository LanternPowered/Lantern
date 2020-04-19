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

import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.data.key.ValueKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.SetValue

class LanternImmutableSetValue<E>(key: Key<out SetValue<E>>, value: MutableSet<E>) :
        LanternCollectionValue.Immutable<E, MutableSet<E>, SetValue.Immutable<E>, SetValue.Mutable<E>>(key, value), SetValue.Immutable<E> {

    override fun get() = CopyHelper.copySet(super.get())

    override fun getKey() = super.getKey().uncheckedCast<ValueKey<SetValue<E>, Set<E>>>()

    override fun withValue(value: MutableSet<E>): SetValue.Immutable<E> = this.key.valueConstructor.getImmutable(value).asImmutable()

    override fun asMutable() = LanternMutableSetValue(this.key, CopyHelper.copy(this.value))
}
