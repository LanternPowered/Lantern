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

import org.spongepowered.api.world.gen.TerrainGenerator
import org.spongepowered.api.world.gen.TerrainGeneratorConfig

open class LanternTerrainGenerator<C : TerrainGeneratorConfig>(
        private val generationSettings: C
) : TerrainGenerator<C> {

    override fun getGenerationSettings(): C = this.generationSettings
}
