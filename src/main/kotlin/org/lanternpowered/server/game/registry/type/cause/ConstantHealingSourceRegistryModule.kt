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
import org.lanternpowered.api.cause.entity.health.HealingType
import org.lanternpowered.api.cause.entity.health.HealingTypes
import org.lanternpowered.api.cause.entity.health.source.HealingSource
import org.lanternpowered.api.cause.entity.health.source.HealingSourceBuilder
import org.lanternpowered.api.cause.entity.health.source.HealingSources
import org.lanternpowered.server.game.registry.CatalogMappingData
import org.lanternpowered.server.game.registry.CatalogMappingDataHolder
import org.spongepowered.api.registry.RegistryModule
import org.spongepowered.api.registry.util.RegistrationDependency
import java.util.HashMap

@RegistrationDependency(HealingTypeRegistryModule::class)
class ConstantHealingSourceRegistryModule : RegistryModule, CatalogMappingDataHolder {

    override fun getCatalogMappings(): List<CatalogMappingData> {
        val mappings = HashMap<String, HealingSource>()
        val register = { id: String, type: HealingType, fn: HealingSourceBuilder.() -> Unit -> mappings.put(id, HealingSource(type, fn)) }
        register("food", HealingTypes.FOOD) {}
        register("generic", HealingTypeRegistryModule.GENERIC) {}
        register("magic", HealingTypeRegistryModule.MAGIC) { magical() }
        return ImmutableList.of(CatalogMappingData(HealingSources::class, mappings))
    }
}
