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
package org.lanternpowered.server.state

import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.state.State
import org.spongepowered.api.state.StateProperty

/**
 * Represents a property within a [State].
 */
interface IStateProperty<S : Comparable<S>, V> : StateProperty<S> {

    /**
     * The [Key] that is linked to this state property.
     */
    val valueKey: Key<out Value<V>>

    /**
     * A list where the values are sorted by their natural ordering.
     */
    val sortedPossibleValues: List<S>

    /**
     * The [StateKeyValueTransformer] to transform between state and key value types.
     */
    val keyValueTransformer: StateKeyValueTransformer<S, V>
}
