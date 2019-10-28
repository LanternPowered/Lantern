/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
