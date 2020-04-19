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
import org.lanternpowered.server.ext.*
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.spongepowered.api.event.cause.entity.damage.DamageType
import org.spongepowered.api.event.cause.entity.damage.DamageTypes

class DamageTypeRegistryModule : AdditionalPluginCatalogRegistryModule<DamageType>(DamageTypes::class) {

    override fun registerDefaults() {
        val register = { id: String -> register(DamageType(CatalogKey.minecraft(id))) }
        register("attack")
        register("contact")
        register("custom")
        register("drown")
        register("explosive")
        register("fall")
        register("fire")
        register("generic")
        register("hunger")
        register("magic")
        register("magma")
        register("projectile")
        register("suffocate")
        register("sweeping_attack")
        register("void")
        register(POISON)
        register(WITHER)
        register(LIGHTNING)
    }

    companion object {

        @JvmField
        val POISON = DamageType(CatalogKey.minecraft("poison"))

        @JvmField
        val WITHER = DamageType(CatalogKey.minecraft("wither"))

        @JvmField
        val LIGHTNING = DamageType(CatalogKey.minecraft("lightning"))
    }
}
