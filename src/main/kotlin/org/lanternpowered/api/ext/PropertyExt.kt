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
package org.lanternpowered.api.ext

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.data.property.DirectionRelativePropertyHolder
import org.lanternpowered.api.data.property.DoublePropertyProvider
import org.lanternpowered.api.data.property.IntPropertyProvider
import org.lanternpowered.api.data.property.Property
import org.lanternpowered.api.data.property.PropertyBuilder
import org.lanternpowered.api.data.property.PropertyHolder
import org.lanternpowered.api.data.property.PropertyProvider
import org.lanternpowered.api.util.TypeToken
import org.spongepowered.api.util.Direction
import java.util.OptionalDouble
import java.util.OptionalInt

/**
 * Constructs a new [Property] with the given [CatalogKey] and value [TypeToken].
 */
fun <V> propertyOf(key: CatalogKey, valueType: TypeToken<V>, fn: PropertyBuilder<V>.() -> Unit = {}): Property<V> =
        builderOf<PropertyBuilder<V>>().key(key).valueType(valueType).apply(fn).build()

/**
 * Constructs a new [Property] with the given [CatalogKey] and value [TypeToken].
 */
inline fun <reified V> propertyOf(key: CatalogKey, fn: PropertyBuilder<V>.() -> Unit = {}): Property<V> =
        builderOf<PropertyBuilder<V>>().key(key).valueType(typeTokenOf<V>()).apply(fn).build()

/**
 * Gets the desired int property value for the provided [PropertyHolder] at
 * present time. A property may not be the same throughout the course of
 * the lifetime of the [PropertyHolder].
 *
 * @param propertyHolder The property holder to get a property from
 * @return The property value
 */
fun PropertyProvider<Int>.getIntFor(propertyHolder: PropertyHolder): OptionalInt {
    return if (this is IntPropertyProvider) {
        getIntFor(propertyHolder)
    } else {
        getFor(propertyHolder).map(OptionalInt::of).orElseGet(OptionalInt::empty)
    }
}

/**
 * Gets the desired double property value for the provided [PropertyHolder] at
 * present time. A property may not be the same throughout the course of
 * the lifetime of the [PropertyHolder].
 *
 * @param propertyHolder The property holder to get a property from
 * @return The property value
 */
fun PropertyProvider<Double>.getDoubleFor(propertyHolder: PropertyHolder): OptionalDouble {
    return if (this is DoublePropertyProvider) {
        getDoubleFor(propertyHolder)
    } else {
        getFor(propertyHolder).map(OptionalDouble::of).orElseGet(OptionalDouble::empty)
    }
}

/**
 * Gets the desired int property value for the provided [DirectionRelativePropertyHolder]
 * at present time. A property may not be the same throughout the course of
 * the lifetime of the [DirectionRelativePropertyHolder].
 *
 * @param propertyHolder The property holder to get a property from
 * @return The property value
 */
fun PropertyProvider<Int>.getIntFor(propertyHolder: DirectionRelativePropertyHolder, direction: Direction): OptionalInt {
    return if (this is IntPropertyProvider) {
        getIntFor(propertyHolder, direction)
    } else {
        getFor(propertyHolder, direction).map(OptionalInt::of).orElseGet(OptionalInt::empty)
    }
}

/**
 * Gets the desired double property value for the provided [DirectionRelativePropertyHolder]
 * at present time. A property may not be the same throughout the course of
 * the lifetime of the [DirectionRelativePropertyHolder].
 *
 * @param propertyHolder The property holder to get a property from
 * @return The property value
 */
fun PropertyProvider<Double>.getDoubleFor(propertyHolder: DirectionRelativePropertyHolder, direction: Direction): OptionalDouble {
    return if (this is DoublePropertyProvider) {
        getDoubleFor(propertyHolder, direction)
    } else {
        getFor(propertyHolder, direction).map(OptionalDouble::of).orElseGet(OptionalDouble::empty)
    }
}
