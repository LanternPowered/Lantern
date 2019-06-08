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

import org.spongepowered.api.data.property.Property
import org.spongepowered.api.data.property.PropertyHolder
import java.util.Optional
import java.util.OptionalDouble
import java.util.OptionalInt

interface IStorePropertyHolder : PropertyHolder {

    @JvmDefault
    override fun getProperties(): Map<Property<*>, *> = LanternPropertyRegistry.getPropertiesFor(this)

    @JvmDefault
    override fun getDoubleProperty(property: Property<Double>): OptionalDouble = LanternPropertyRegistry.getDoubleStore(property).getDoubleFor(this)

    @JvmDefault
    override fun getIntProperty(property: Property<Int>): OptionalInt = LanternPropertyRegistry.getIntStore(property).getIntFor(this)

    @JvmDefault
    override fun <V> getProperty(property: Property<V>): Optional<V> = LanternPropertyRegistry.getStore(property).getFor(this)
}
