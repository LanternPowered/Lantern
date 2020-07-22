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
import org.lanternpowered.api.world.WorldArchetype
import org.lanternpowered.api.world.WorldArchetypeBuilder
import org.lanternpowered.api.world.worldArchetypeOf
import org.lanternpowered.api.namespace.NamespacedKey
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.world.SerializationBehaviors
import org.spongepowered.api.world.difficulty.Difficulties
import org.spongepowered.api.world.dimension.DimensionTypes
import org.spongepowered.api.world.gen.GeneratorTypes

val WorldArchetypeRegistry = catalogTypeRegistry<WorldArchetype> {
    fun register(id: String, fn: WorldArchetypeBuilder.() -> Unit) =
            register(worldArchetypeOf(NamespacedKey.minecraft(id), fn))

    val overworld = register("overworld") {
        enabled(true)
        loadOnStartup(true)
        keepSpawnLoaded(true)
        commandsEnabled(true)
        gameMode(GameModes.SURVIVAL)
        generatorType(GeneratorTypes.DEFAULT)
        generateSpawnOnLoad(true)
        generateStructures(true)
        generateBonusChest(false)
        dimensionType(DimensionTypes.OVERWORLD)
        difficulty(Difficulties.NORMAL)
        hardcore(false)
        pvpEnabled(true)
        serializationBehavior(SerializationBehaviors.AUTOMATIC)
    }

    register("the_nether") {
        from(overworld)
        generatorType(GeneratorTypes.THE_NETHER)
        dimensionType(DimensionTypes.THE_NETHER)
    }

    register("the_end") {
        from(overworld)
        generatorType(GeneratorTypes.THE_END)
        dimensionType(DimensionTypes.THE_END)
    }

    register("the_void") {
        from(overworld)
        // TODO
    }
}
