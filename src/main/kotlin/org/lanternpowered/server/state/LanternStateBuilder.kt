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

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import org.lanternpowered.api.namespace.NamespacedKey
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.state.State
import org.spongepowered.api.state.StateProperty

internal class LanternStateBuilder<S : State<S>>(
        val key: NamespacedKey,
        val dataContainer: DataContainer,
        val stateContainer: AbstractStateContainer<S>,
        val stateValues: ImmutableMap<StateProperty<*>, Comparable<*>>,
        val values: ImmutableSet<Value.Immutable<*>>,
        val internalId: Int
) : StateBuilder<S> {

    val whenCompleted: MutableList<() -> Unit> = mutableListOf()

    override fun whenCompleted(fn: () -> Unit) {
        this.whenCompleted += fn
    }
}
