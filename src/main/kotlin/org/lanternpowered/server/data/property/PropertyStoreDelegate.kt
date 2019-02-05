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
package org.lanternpowered.server.data.property

import com.google.common.collect.ImmutableList
import org.spongepowered.api.data.property.DirectionRelativePropertyHolder
import org.spongepowered.api.data.property.PropertyHolder
import org.spongepowered.api.data.property.store.PropertyStore
import org.spongepowered.api.util.Direction
import java.util.Optional

open class PropertyStoreDelegate<V> internal constructor(internal val propertyStores: ImmutableList<PropertyStore<V>>) : PropertyStore<V> {

    override fun getFor(propertyHolder: PropertyHolder): Optional<V> {
        for (propertyStore in this.propertyStores) {
            val optional = propertyStore.getFor(propertyHolder)
            if (optional.isPresent) {
                return optional
            }
        }
        return Optional.empty()
    }

    override fun getFor(propertyHolder: DirectionRelativePropertyHolder, direction: Direction): Optional<V> {
        for (propertyStore in this.propertyStores) {
            val optional = propertyStore.getFor(propertyHolder, direction)
            if (optional.isPresent) {
                return optional
            }
        }
        return Optional.empty()
    }

    override fun getPriority() = Integer.MAX_VALUE
}
