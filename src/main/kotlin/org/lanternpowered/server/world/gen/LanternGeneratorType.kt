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
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.data.DataView

abstract class LanternGeneratorType protected constructor(key: CatalogKey) : DefaultCatalogType(key), IGeneratorType {

    // The maximum height the generator will generate the world,
    // for example 128 blocks in the nether and in overworld 256
    var defaultGeneratorHeight: Int = 256

    // The minimal spawn height
    var defaultMinimalSpawnHeight: Int = 1

    var defaultSeaLevel: Int = 62

    override fun getGeneratorSettings(): DataContainer = DataContainer.createNew()

    override fun getSeaLevel(settings: DataView): Int =
            settings.getInt(IGeneratorType.SEA_LEVEL).orElse(this.defaultSeaLevel)

    override fun getMinimalSpawnHeight(settings: DataView): Int =
            settings.getInt(IGeneratorType.MINIMAL_SPAWN_HEIGHT).orElse(this.defaultMinimalSpawnHeight)

    override fun getGeneratorHeight(settings: DataView): Int =
            settings.getInt(IGeneratorType.GENERATOR_HEIGHT).orElse(this.defaultGeneratorHeight)
}
