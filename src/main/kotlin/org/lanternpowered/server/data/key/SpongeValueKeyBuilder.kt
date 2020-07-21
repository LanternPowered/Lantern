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
package org.lanternpowered.server.data.key

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Comparator
import java.util.function.BiPredicate

class SpongeValueKeyBuilder<E : Any, V : Value<E>> : ValueKeyBuilderBase<E, V, SpongeValueKeyBuilder<E, V>, Key.Builder<E, V>>(), Key.Builder<E, V> {

    override fun <T : Any, N : Value<T>> type(token: TypeToken<N>): SpongeValueKeyBuilder<T, N> =
            apply { setType(token.uncheckedCast()) }.uncheckedCast()

    override fun comparator(comparator: Comparator<in E>) = apply { setComparator(comparator) }
    override fun includesTester(predicate: BiPredicate<in E, in E>?) = apply { setIncludesTester(predicate) }
}
