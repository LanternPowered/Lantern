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

import com.google.common.collect.ImmutableList
import org.lanternpowered.api.cause.entity.damage.DamageType
import org.lanternpowered.api.cause.entity.damage.DamageTypes
import org.lanternpowered.api.cause.entity.damage.source.DamageSource
import org.lanternpowered.api.cause.entity.damage.source.DamageSourceBuilder
import org.lanternpowered.api.cause.entity.damage.source.DamageSources
import org.lanternpowered.server.game.registry.CatalogMappingData
import org.lanternpowered.server.game.registry.CatalogMappingDataHolder
import org.spongepowered.api.registry.RegistryModule
import org.spongepowered.api.registry.util.RegistrationDependency
import java.util.HashMap

@RegistrationDependency(DamageTypeRegistryModule::class)
class ConstantDamageSourceRegistryModule : RegistryModule, CatalogMappingDataHolder {

    override fun getCatalogMappings(): List<CatalogMappingData> {
        val mappings = HashMap<String, DamageSource>()
        val register = { id: String, type: DamageType, fn: DamageSourceBuilder.() -> Unit -> mappings.put(id, DamageSource(type, fn)) }
        register("drowning", DamageTypes.DROWN) { bypassesArmor() }
        register("falling", DamageTypes.FALL) { bypassesArmor() }
        register("fire_tick", DamageTypes.FIRE) {}
        register("generic", DamageTypes.GENERIC) {}
        register("magic", DamageTypes.MAGIC) { bypassesArmor().magical() }
        register("melting", DamageTypes.FIRE) { bypassesArmor() }
        register("poison", DamageTypeRegistryModule.POISON) { bypassesArmor() }
        register("starvation", DamageTypes.HUNGER) { bypassesArmor().absolute() }
        register("void", DamageTypes.VOID) { bypassesArmor().creative() }
        register("wither", DamageTypeRegistryModule.WITHER) { bypassesArmor() }
        return ImmutableList.of(CatalogMappingData(DamageSources::class, mappings))
    }
}
