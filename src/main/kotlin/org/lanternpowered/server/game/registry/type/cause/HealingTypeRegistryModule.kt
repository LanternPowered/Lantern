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

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.server.ext.*
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.spongepowered.api.event.cause.entity.health.HealingType
import org.spongepowered.api.event.cause.entity.health.HealingTypes

class HealingTypeRegistryModule : AdditionalPluginCatalogRegistryModule<HealingType>(HealingTypes::class) {

    override fun registerDefaults() {
        val register = { id: String -> register(HealingType(ResourceKey.minecraft(id))) }
        register("boss")
        register("food")
        register("plugin")
        register("potion")
        register("undead")
        register(GENERIC)
        register(MAGIC)
    }

    companion object {

        @JvmField
        val GENERIC = HealingType(ResourceKey.minecraft("generic"))

        @JvmField
        val MAGIC = HealingType(ResourceKey.minecraft("magic"))
    }
}
