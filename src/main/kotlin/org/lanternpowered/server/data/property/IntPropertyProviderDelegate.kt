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
import org.lanternpowered.api.data.property.DirectionRelativePropertyHolder
import org.lanternpowered.api.data.property.IntPropertyProvider
import org.lanternpowered.api.data.property.PropertyHolder
import org.lanternpowered.api.data.property.PropertyProvider
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.Direction
import java.util.OptionalInt

internal open class IntPropertyProviderDelegate internal constructor(providers: ImmutableList<PropertyProvider<Int>>) :
        PropertyProviderDelegate<Int>(providers), IntPropertyProvider {

    override fun getFor(propertyHolder: PropertyHolder) =
            super<PropertyProviderDelegate>.getFor(propertyHolder)

    override fun getFor(propertyHolder: DirectionRelativePropertyHolder, direction: Direction) =
            super<PropertyProviderDelegate>.getFor(propertyHolder, direction)

    override fun getIntFor(propertyHolder: PropertyHolder): OptionalInt {
        for (propertyStore in this.providers) {
            val optional = propertyStore.getIntFor(propertyHolder)
            if (optional.isPresent) {
                return optional
            }
        }
        return OptionalInt.empty()
    }

    override fun getIntFor(propertyHolder: DirectionRelativePropertyHolder, direction: Direction): OptionalInt {
        for (propertyStore in this.providers) {
            val optional = propertyStore.getIntFor(propertyHolder, direction)
            if (optional.isPresent) {
                return optional
            }
        }
        return OptionalInt.empty()
    }
}
