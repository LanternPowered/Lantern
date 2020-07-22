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
package org.lanternpowered.server.state.property

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.server.state.identityStateKeyValueTransformer
import org.lanternpowered.api.key.NamespacedKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.state.EnumStateProperty
import java.util.Optional

internal class LanternEnumStateProperty<E : Enum<E>>(
        key: NamespacedKey, valueClass: Class<E>, possibleValues: ImmutableSet<E>, valueKey: Key<out Value<E>>
) : AbstractStateProperty<E, E>(key, valueClass, possibleValues, valueKey, identityStateKeyValueTransformer()), EnumStateProperty<E> {

    override var sortedPossibleValues: List<E> = ImmutableList.sortedCopyOf(this.possibleValues)

    override fun parseValue(value: String): Optional<E> {
        for (enumValue in valueClass.enumConstants) {
            if (enumValue.name.equals(value, ignoreCase = true)) {
                return enumValue.optional()
            }
        }
        return emptyOptional()
    }
}
