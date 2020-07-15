/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.world.dimension

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.InternalCatalogType
import org.lanternpowered.server.world.LanternWorld
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.world.dimension.DimensionType
import org.spongepowered.api.world.gen.GeneratorType
import java.util.function.BiFunction

class LanternDimensionType<T : LanternDimension>(
        key: ResourceKey,
        name: String,
        override val internalId: Int,
        private val dimensionClass: Class<T>,
        /**
         * The default generator type of this dimension type. This one will be used
         * if there can't be one found in the world data.
         */
        val defaultGeneratorType: GeneratorType,
        val keepSpawnLoaded: Boolean,
        val doesWaterEvaporate: Boolean,
        private val hasSkylight: Boolean,
        val allowsPlayerRespawns: Boolean,
        private val supplier: BiFunction<LanternWorld, LanternDimensionType<T>, T>
) : DefaultCatalogType.Named(key, name), DimensionType, InternalCatalogType {

    /**
     * The shared [Context] for all the dimensions of this type.
     */
    private val context = Context(Context.DIMENSION_KEY, this.key.toString())

    /**
     * Creates a new dimension instance for the specified world.
     *
     * @param world The world to create the dimension for
     * @return The dimension instance
     */
    fun newDimension(world: LanternWorld): T {
        return this.supplier.apply(world, this)
    }

    override fun hasSkylight() = this.hasSkylight
    override fun getContext() = this.context

    override fun toStringHelper() = super.toStringHelper()
            .add("dimensionClass", this.dimensionClass.name)
}
