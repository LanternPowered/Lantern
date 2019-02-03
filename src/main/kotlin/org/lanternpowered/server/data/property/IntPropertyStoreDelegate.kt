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
import org.spongepowered.api.data.property.PropertyHolder
import org.spongepowered.api.data.property.store.IntPropertyStore
import org.spongepowered.api.data.property.store.PropertyStore
import org.spongepowered.api.util.Direction
import org.spongepowered.api.world.Location
import java.util.OptionalInt

class IntPropertyStoreDelegate internal constructor(propertyStores: ImmutableList<PropertyStore<Int>>) :
        PropertyStoreDelegate<Int>(propertyStores), IntPropertyStore {

    override fun getFor(propertyHolder: PropertyHolder) = super<PropertyStoreDelegate>.getFor(propertyHolder)
    override fun getFor(location: Location, direction: Direction) = super<PropertyStoreDelegate>.getFor(location, direction)

    override fun getIntFor(propertyHolder: PropertyHolder): OptionalInt {
        for (propertyStore in this.propertyStores) {
            if (propertyStore is IntPropertyStore) {
                val optional = propertyStore.getIntFor(propertyHolder)
                if (optional.isPresent) {
                    return optional
                }
            } else {
                val optional = propertyStore.getFor(propertyHolder)
                if (optional.isPresent) {
                    return OptionalInt.of(optional.get())
                }
            }
        }
        return OptionalInt.empty()
    }

    override fun getIntFor(location: Location, direction: Direction): OptionalInt {
        for (propertyStore in this.propertyStores) {
            if (propertyStore is IntPropertyStore) {
                val optional = propertyStore.getIntFor(location, direction)
                if (optional.isPresent) {
                    return optional
                }
            } else {
                val optional = propertyStore.getFor(location, direction)
                if (optional.isPresent) {
                    return OptionalInt.of(optional.get())
                }
            }
        }
        return OptionalInt.empty()
    }
}
