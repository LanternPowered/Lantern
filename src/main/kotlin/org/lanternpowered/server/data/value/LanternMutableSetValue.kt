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

class LanternMutableSetValue<E>(key: Key<out SetValue<E>>, value: MutableSet<E>) :
        LanternCollectionValue.Mutable<E, MutableSet<E>, SetValue.Mutable<E>, SetValue.Immutable<E>>(key, value), SetValue.Mutable<E> {

    override fun getKey() = super.getKey().uncheckedCast<ValueKey<SetValue<E>, Set<E>>>()

    override fun asImmutable(): SetValue.Immutable<E> = this.key.valueConstructor.getImmutable(this.value).asImmutable()

    override fun copy() = LanternMutableSetValue(this.key, CopyHelper.copySet(this.value))
}
