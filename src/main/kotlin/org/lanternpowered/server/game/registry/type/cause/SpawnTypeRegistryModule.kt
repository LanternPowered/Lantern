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
