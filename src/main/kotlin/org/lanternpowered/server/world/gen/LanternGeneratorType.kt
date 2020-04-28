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
package org.lanternpowered.server.world.gen

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView

abstract class LanternGeneratorType protected constructor(key: CatalogKey) : DefaultCatalogType(key), IGeneratorType {

    // The maximum height the generator will generate the world,
    // for example 128 blocks in the nether and in overworld 256
    var defaultGeneratorHeight: Int = 256

    // The minimal spawn height
    var defaultMinimalSpawnHeight: Int = 1

    var defaultSeaLevel: Int = 62

    override fun getDefaultGeneratorSettings(): DataContainer = DataContainer.createNew()

    override fun getSeaLevel(settings: DataView): Int =
            settings.getInt(IGeneratorType.SEA_LEVEL).orElse(this.defaultSeaLevel)

    override fun getMinimalSpawnHeight(settings: DataView): Int =
            settings.getInt(IGeneratorType.MINIMAL_SPAWN_HEIGHT).orElse(this.defaultMinimalSpawnHeight)

    override fun getGeneratorHeight(settings: DataView): Int =
            settings.getInt(IGeneratorType.GENERATOR_HEIGHT).orElse(this.defaultGeneratorHeight)
}
