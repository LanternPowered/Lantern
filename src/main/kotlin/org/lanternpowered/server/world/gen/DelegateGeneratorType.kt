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

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.world.gen.GeneratorType

class DelegateGeneratorType(key: NamespacedKey, generatorType: GeneratorType) : DefaultCatalogType(key), IGeneratorType {

    /**
     * The actual [GeneratorType].
     */
    var generatorType: GeneratorType = generatorType
        set(generatorType) {
            field = generatorType
            // Invalidate these
            this.minimalSpawnHeight = Integer.MAX_VALUE
            this.generatorHeight = Integer.MAX_VALUE
            this.seaLevel = Integer.MAX_VALUE
        }

    private var minimalSpawnHeight: Int = 0
    private var generatorHeight: Int = 0
    private var seaLevel: Int = 0

    override fun getMinimalSpawnHeight(settings: DataView): Int {
        if (this.minimalSpawnHeight == Integer.MAX_VALUE) {
            this.minimalSpawnHeight = IGeneratorType.getMinimalSpawnHeight(this.generatorType, settings)
        }
        return this.minimalSpawnHeight
    }

    override fun getGeneratorHeight(settings: DataView): Int {
        if (this.generatorHeight == Integer.MAX_VALUE) {
            this.generatorHeight = IGeneratorType.getGeneratorHeight(this.generatorType, settings)
        }
        return this.generatorHeight
    }

    override fun getSeaLevel(settings: DataView): Int {
        if (this.seaLevel == Integer.MAX_VALUE) {
            this.seaLevel = IGeneratorType.getSeaLevel(this.generatorType, settings)
        }
        return this.seaLevel
    }

    override fun getDefaultGeneratorSettings(): DataContainer = this.generatorType.defaultGeneratorSettings
    // override fun createGenerator(world: ServerWorld): TerrainGenerator<*> = this.generatorType.createGenerator(world)

    override fun toStringHelper() = super.toStringHelper()
                .add("backing", this.generatorType.key)
}
