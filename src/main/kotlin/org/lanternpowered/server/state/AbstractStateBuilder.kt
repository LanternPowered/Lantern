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

import org.spongepowered.api.data.DataHolderBuilder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.state.State
import kotlin.reflect.KClass

abstract class AbstractStateBuilder<S : State<S>, B : DataHolderBuilder.Immutable<S, B>>(
        private val stateType: KClass<S>, supportedVersion: Int = 1
) : AbstractDataBuilder<S>(stateType.java, supportedVersion), DataHolderBuilder.Immutable<S, B> {

    private var state: S? = null

    private fun checkState(): S = checkNotNull(this.state) { "The ${stateType.simpleName} must be set before applying data" }

    @Suppress("UNCHECKED_CAST")
    private inline fun apply(fn: () -> Unit): B {
        fn()
        return this as B
    }

    override fun reset() = apply {
        this.state = null
    }

    override fun add(value: Value<*>) = apply {
        checkState().with(value).ifPresent { state -> this.state = state }
    }

    override fun <V> add(key: Key<out Value<V>>, value: V) = apply {
        checkState().with(key, value).ifPresent { state -> this.state = state }
    }

    override fun from(state: S) = apply {
        this.state = state
    }

    override fun build(): S = checkNotNull(this.state) { "The ${stateType.simpleName} must be set" }
}
