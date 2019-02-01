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
package org.lanternpowered.server.world.dimension

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.InternalCatalogType
import org.lanternpowered.server.world.LanternWorld
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.world.Dimension
import org.spongepowered.api.world.DimensionType
import org.spongepowered.api.world.gen.GeneratorType
import java.util.function.BiFunction

class LanternDimensionType<T : LanternDimension>(
        key: CatalogKey,
        override val internalId: Int,
        private val dimensionClass: Class<T>,
        /**
         * The default generator type of this dimension type. This one will be used
         * if there can't be one found in the world data.
         */
        val defaultGeneratorType: GeneratorType,
        val keepSpawnLoaded: Boolean,
        val doesWaterEvaporate: Boolean,
        val hasSky: Boolean,
        val allowsPlayerRespawns: Boolean,
        private val supplier: BiFunction<LanternWorld, LanternDimensionType<T>, T>
) : DefaultCatalogType(key), DimensionType, InternalCatalogType {

    /**
     * The shared [Context] for all the [Dimension]s of this type.
     */
    val dimensionContext = Context(Context.DIMENSION_KEY, this.key.toString())

    /**
     * Creates a new dimension instance for the specified world.
     *
     * @param world The world to create the dimension for
     * @return The dimension instance
     */
    fun newDimension(world: LanternWorld): T {
        return this.supplier.apply(world, this)
    }

    override fun getDimensionClass() = this.dimensionClass
    override fun toStringHelper() = super.toStringHelper()
            .add("dimensionClass", this.dimensionClass.name)
}
