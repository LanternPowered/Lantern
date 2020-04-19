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
package org.lanternpowered.server.game.registry.type.cause

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.cause.entity.spawn.LanternSpawnType
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.spongepowered.api.event.cause.entity.spawn.SpawnType
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes

class SpawnTypeRegistryModule : AdditionalPluginCatalogRegistryModule<SpawnType>(SpawnTypes::class) {

    override fun registerDefaults() {
        val register = { id: String -> register(LanternSpawnType(CatalogKey.minecraft(id))) }
        register("block_spawning")
        register("breeding")
        register("chunk_load")
        register("custom")
        register("dispense")
        register("dropped_item")
        register("experience")
        register("falling_block")
        register("mob_spawner")
        register("passive")
        register("placement")
        register("plugin")
        register("projectile")
        register("spawn_egg")
        register("structure")
        register("tnt_ignite")
        register("weather")
        register("world_spawner")
    }
}
