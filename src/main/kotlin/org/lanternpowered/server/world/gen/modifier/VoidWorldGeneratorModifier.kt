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
package org.lanternpowered.server.world.gen.modifier

import org.lanternpowered.api.catalog.CatalogKeys
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.world.World
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.world.biome.BiomeType
import org.spongepowered.api.world.biome.BiomeTypes
import org.spongepowered.api.world.gen.WorldGenerator
import org.spongepowered.api.world.gen.WorldGeneratorModifier
import org.spongepowered.api.world.storage.WorldProperties

/**
 * A modifier that causes a [World] to generate with empty chunks. Useful for "lobby-like" worlds.
 */
object VoidWorldGeneratorModifier : DefaultCatalogType(CatalogKeys.sponge("void", "Void Modifier")), WorldGeneratorModifier {

    override fun modifyWorldGenerator(world: WorldProperties, settings: DataContainer, worldGenerator: WorldGenerator) {
        worldGenerator.generationPopulators.clear()
        worldGenerator.populators.clear()
        for (biome in allCatalogsOf<BiomeType>()) {
            val biomeSettings = worldGenerator.getBiomeSettings(biome)
            biomeSettings.generationPopulators.clear()
            biomeSettings.populators.clear()
            biomeSettings.groundCoverLayers.clear()
        }
        // TODO
        //worldGenerator.setBaseGenerationPopulator { _, _, _ -> }
        //worldGenerator.setBiomeGenerator { buffer -> buffer.biomeWorker.fill { _, _, _ -> BiomeTypes.VOID } }
    }
}
