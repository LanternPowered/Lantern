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

import org.lanternpowered.api.data.property.Property
import org.lanternpowered.api.ext.asUnmodifiableMap
import org.lanternpowered.api.ext.orNull
import org.lanternpowered.api.ext.uncheckedCast
import java.util.Optional
import java.util.OptionalDouble
import java.util.OptionalInt

interface LocalPropertyHolder : PropertyHolderBase {

    /**
     * Gets the local [PropertyRegistry].
     *
     * @return The local property registry
     */
    val propertyRegistry: PropertyRegistry<out LocalPropertyHolder>

    /**
     * A convenient extension that applies changes as the caller type.
     */
    @JvmDefault
    fun <H : LocalPropertyHolder> H.propertyRegistry(
            fn: PropertyRegistry<H>.() -> Unit): PropertyRegistry<H> {
        return this.propertyRegistry.forHolderUnchecked<H>().apply(fn)
    }

    @JvmDefault
    override fun getProperties(): Map<Property<*>, *> {
        val properties = mutableMapOf<Property<*>, Any>()

        for ((property, store) in this.propertyRegistry.providers) {
            if (property !in properties) {
                val value = store.getFor(this).orNull()
                if (value != null) {
                    properties[property] = value
                }
            }
        }

        for ((property, store) in GlobalPropertyRegistry.providers) {
            if (property !in properties) {
                val value = store.uncheckedCast<IGlobalPropertyProvider<Any>>().getFor(this, true).orNull()
                if (value != null) {
                    properties[property] = value
                }
            }
        }

        return properties.asUnmodifiableMap()
    }

    @JvmDefault
    override fun <V : Any> getProperty(property: Property<V>): Optional<V> {
        val value = this.propertyRegistry.getProvider(property).getFor(this)
        if (value.isPresent) {
            return value
        }
        return super.getProperty(property)
    }

    @JvmDefault
    override fun getIntProperty(property: Property<Int>): OptionalInt {
        val value = this.propertyRegistry.getIntProvider(property).getIntFor(this)
        if (value.isPresent) {
            return value
        }
        return super.getIntProperty(property)
    }

    @JvmDefault
    override fun getDoubleProperty(property: Property<Double>): OptionalDouble {
        val value = this.propertyRegistry.getDoubleProvider(property).getDoubleFor(this)
        if (value.isPresent) {
            return value
        }
        return super.getDoubleProperty(property)
    }
}
