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

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import org.lanternpowered.api.catalog.CatalogKey
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.state.State
import org.spongepowered.api.state.StateProperty

internal class LanternStateBuilder<S : State<S>>(
        val key: CatalogKey,
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
