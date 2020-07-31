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
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.server.state.StateKeyValueTransformer
import org.lanternpowered.api.key.NamespacedKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.state.IntegerStateProperty

internal class LanternIntStateProperty<V>(
        key: NamespacedKey, possibleValues: ImmutableSet<Int>, valueKey: Key<out Value<V>>, keyValueTransformer: StateKeyValueTransformer<Int, V>
) : AbstractStateProperty<Int, V>(key, Int::class.java, possibleValues, valueKey, keyValueTransformer), IntegerStateProperty {

    override var sortedPossibleValues: List<Int> = ImmutableList.sortedCopyOf(this.possibleValues)

    override fun parseValue(value: String) = value.toIntOrNull().asOptional()
}
