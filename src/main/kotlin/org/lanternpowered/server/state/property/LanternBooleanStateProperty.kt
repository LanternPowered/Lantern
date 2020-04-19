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
import org.lanternpowered.server.state.StateKeyValueTransformer
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.state.BooleanStateProperty
import org.spongepowered.api.util.OptBool
import java.util.Optional

internal class LanternBooleanStateProperty<T>(
        key: CatalogKey, valueKey: Key<out Value<T>>, keyValueTransformer: StateKeyValueTransformer<Boolean, T>
) : AbstractStateProperty<Boolean, T>(key, Boolean::class.java, this.states, valueKey, keyValueTransformer), BooleanStateProperty {

    override val sortedPossibleValues: List<Boolean> get() = sortedStates

    override fun parseValue(value: String): Optional<Boolean> {
        return when (value.toLowerCase()) {
            "true" -> OptBool.TRUE
            "false" -> OptBool.FALSE
            else -> emptyOptional()
        }
    }

    companion object {
        private val states = ImmutableSet.of(true, false)
        private val sortedStates = ImmutableList.of(false, true)
    }
}
