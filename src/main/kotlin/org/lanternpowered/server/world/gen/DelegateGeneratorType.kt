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
package org.lanternpowered.server.world.gen

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.world.World
import org.spongepowered.api.world.gen.GeneratorType
import org.spongepowered.api.world.gen.TerrainGenerator

class DelegateGeneratorType(key: CatalogKey, generatorType: GeneratorType) : DefaultCatalogType(key), IGeneratorType {

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

    override fun getGeneratorSettings(): DataContainer = this.generatorType.generatorSettings
    override fun createGenerator(world: World): TerrainGenerator<*> = this.generatorType.createGenerator(world)

    override fun toStringHelper() = super.toStringHelper()
                .add("backing", this.generatorType.key)
}
