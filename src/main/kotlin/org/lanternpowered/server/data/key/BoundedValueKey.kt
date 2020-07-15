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
import org.lanternpowered.server.data.value.BoundedValueConstructor
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.data.value.BoundedValue
import java.util.function.BiPredicate

/**
 * Represents the [ValueKey] of a [BoundedValue].
 */
class BoundedValueKey<V : BoundedValue<E>, E : Any>(
        key: ResourceKey,
        valueToken: TypeToken<V>,
        elementToken: TypeToken<E>,
        elementComparator: Comparator<in E>,
        elementIncludesTester: BiPredicate<in E, in E>,
        defaultElementSupplier: () -> E?,
        requiresExplicitRegistration: Boolean,
        val minimum: () -> E,
        val maximum: () -> E
) : ValueKey<V, E>(key, valueToken, elementToken, elementComparator, elementIncludesTester,
        defaultElementSupplier, requiresExplicitRegistration) {

    override val valueConstructor: BoundedValueConstructor<V, E>
        get() = super.valueConstructor as BoundedValueConstructor<V, E>
}
