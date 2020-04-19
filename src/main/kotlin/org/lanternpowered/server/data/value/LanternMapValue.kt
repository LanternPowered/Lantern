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
import org.spongepowered.api.data.value.MapValue

abstract class LanternMapValue<K, V> protected constructor(key: Key<out MapValue<K, V>>, value: MutableMap<K, V>) :
        LanternValue<MutableMap<K, V>>(key, value), MapValue<K, V> {

    override fun getKey() = super.getKey().uncheckedCast<ValueKey<MapValue<K, V>, Map<K, V>>>()

    override fun size() = this.value.size

    override fun containsKey(key: K) = this.value.containsKey(key)

    override fun containsValue(value: V) = this.value.containsValue(value)

    override fun keySet() = get().keys

    override fun entrySet() = get().entries

    override fun values() = get().values
}
