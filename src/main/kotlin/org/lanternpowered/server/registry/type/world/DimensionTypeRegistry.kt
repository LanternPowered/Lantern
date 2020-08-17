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
package org.lanternpowered.server.registry.type.world

import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.world.dimension.DimensionType
import org.lanternpowered.server.world.dimension.LanternDimensionType
import org.lanternpowered.api.key.NamespacedKey

val DimensionTypeRegistry = catalogTypeRegistry<DimensionType> {
    // TODO: Cleanup

    register(LanternDimensionType(
            key = NamespacedKey.minecraft("the_nether"),
            name = "The Nether",
            internalId = -1,
            // defaultGeneratorType = GeneratorTypes.THE_NETHER.get(),
            keepSpawnLoaded = true,
            doesWaterEvaporate = true,
            hasSkylight = false,
            allowsPlayerRespawns = false,
            isCaveWorld = true,
            isSurfaceWorld = false
    ))

    register(LanternDimensionType(
            key = NamespacedKey.minecraft("overworld"),
            name = "Overworld",
            internalId = 0,
            // defaultGeneratorType = GeneratorTypes.OVERWORLD.get(),
            keepSpawnLoaded = true,
            doesWaterEvaporate = false,
            hasSkylight = true,
            allowsPlayerRespawns = true,
            isCaveWorld = false,
            isSurfaceWorld = true
    ))

    register(LanternDimensionType(
            key = NamespacedKey.minecraft("the_end"),
            name = "The End",
            internalId = 0,
            // defaultGeneratorType = GeneratorTypes.OVERWORLD.get(),
            keepSpawnLoaded = true,
            doesWaterEvaporate = true,
            hasSkylight = false,
            allowsPlayerRespawns = false,
            isCaveWorld = false,
            isSurfaceWorld = false
    ))
}
