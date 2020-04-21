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
package org.lanternpowered.server.registry.type.data

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.event.cause.entity.spawn.SpawnType

val SpawnTypeRegistry = catalogTypeRegistry<SpawnType> {
    fun register(id: String) =
            register(LanternSpawnType(CatalogKey.minecraft(id)))

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

private class LanternSpawnType(key: CatalogKey) : DefaultCatalogType(key), SpawnType
