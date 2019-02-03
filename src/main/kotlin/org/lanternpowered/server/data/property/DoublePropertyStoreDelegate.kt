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
import org.spongepowered.api.data.property.store.DoublePropertyStore
import org.spongepowered.api.data.property.store.PropertyStore
import org.spongepowered.api.util.Direction
import org.spongepowered.api.world.Location
import java.util.OptionalDouble

class DoublePropertyStoreDelegate internal constructor(propertyStores: ImmutableList<PropertyStore<Double>>) :
        PropertyStoreDelegate<Double>(propertyStores), DoublePropertyStore {

    override fun getFor(propertyHolder: PropertyHolder) = super<PropertyStoreDelegate>.getFor(propertyHolder)
    override fun getFor(location: Location, direction: Direction) = super<PropertyStoreDelegate>.getFor(location, direction)

    override fun getDoubleFor(propertyHolder: PropertyHolder): OptionalDouble {
        for (propertyStore in this.propertyStores) {
            if (propertyStore is DoublePropertyStore) {
                val optional = propertyStore.getDoubleFor(propertyHolder)
                if (optional.isPresent) {
                    return optional
                }
            } else {
                val optional = propertyStore.getFor(propertyHolder)
                if (optional.isPresent) {
                    return OptionalDouble.of(optional.get())
                }
            }
        }
        return OptionalDouble.empty()
    }

    override fun getDoubleFor(location: Location, direction: Direction): OptionalDouble {
        for (propertyStore in this.propertyStores) {
            if (propertyStore is DoublePropertyStore) {
                val optional = propertyStore.getDoubleFor(location, direction)
                if (optional.isPresent) {
                    return optional
                }
            } else {
                val optional = propertyStore.getFor(location, direction)
                if (optional.isPresent) {
                    return OptionalDouble.of(optional.get())
                }
            }
        }
        return OptionalDouble.empty()
    }
}
