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

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.world.LanternWorldNew
import org.spongepowered.api.world.dimension.Dimension
import org.spongepowered.api.world.dimension.DimensionType

open class LanternDimension protected constructor(
        private val world: LanternWorldNew,
        private val dimensionType: LanternDimensionType<*>
) : Dimension {

    override fun getType(): DimensionType = this.dimensionType
    override fun allowsPlayerRespawns(): Boolean = this.world.properties.allowsPlayerRespawns
    override fun doesWaterEvaporate(): Boolean = this.world.properties.doesWaterEvaporate
    override fun hasSky(): Boolean = this.dimensionType.hasSkylight()
    override fun hasSkylight(): Boolean = this.dimensionType.hasSkylight()

    override fun isSurfaceLike(): Boolean {
        return false // TODO
    }

    // override fun createGenerator(): TerrainGenerator<*> = this.world.properties.generatorType.createGenerator(world)

    override fun toString(): String = ToStringHelper(this)
            .add("worldUUID", this.world.uniqueId)
            .add("worldName", this.world.properties.directoryName)
            .add("dimensionType", this.dimensionType.key)
            .toString()
}
