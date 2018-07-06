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
package org.lanternpowered.server.data.type

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.InternalCatalogType
import org.lanternpowered.server.catalog.asString
import org.spongepowered.api.data.type.LogAxis
import org.spongepowered.api.util.Axis
import org.spongepowered.api.util.Direction

enum class LanternLogAxis(id: String, private val axis: Axis?, private val cycler: () -> LogAxis) :
        LogAxis, CatalogType by DefaultCatalogType.minecraft(id), InternalCatalogType.EnumOrdinal {

    Y       ("y", Axis.Y, { Z }),
    X       ("x", Axis.X, { Y }),
    Z       ("z", Axis.Z, { X }),
    NONE    ("none", null, { NONE });

    override fun cycleNext(): LogAxis = this.cycler()
    override fun toString(): String = asString()

    companion object {

        private val lookupByAxis = enumMapOf<Axis, LanternLogAxis>()

        init {
            for (axis in values()) {
                if (axis.axis != null) {
                    this.lookupByAxis[axis.axis] = axis
                }
            }
        }

        /**
         * Gets the log axis for the specified axis.
         *
         * @param axis the axis
         * @return the log axis
         */
        @JvmStatic
        fun fromAxis(axis: Axis): LanternLogAxis {
            return this.lookupByAxis[axis]!!
        }

        /**
         * Gets the log axis for the the specified direction.
         *
         * @param direction the direction
         * @return the log axis
         */
        @JvmStatic
        fun fromDirection(direction: Direction): LanternLogAxis {
            return fromAxis(Axis.getClosest(direction.asOffset()))
        }
    }

}
