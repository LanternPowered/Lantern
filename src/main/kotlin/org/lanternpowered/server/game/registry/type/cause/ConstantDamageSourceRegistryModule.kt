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
