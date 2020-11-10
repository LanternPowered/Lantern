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
package org.lanternpowered.server.network.entity.vanilla

import org.lanternpowered.api.data.Keys
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.parameter.ParameterList
import org.lanternpowered.server.network.value.VillagerData
import org.spongepowered.api.data.type.ProfessionTypes
import org.spongepowered.api.data.type.VillagerTypes

class VillagerEntityProtocol<E : LanternEntity>(entity: E) : AgeableEntityProtocol<E>(entity) {

    companion object {
        private val TYPE = minecraftKey("villager")
    }

    private var lastVillagerData: VillagerData? = null

    override val mobType: NamespacedKey get() = TYPE

    override fun spawn(parameterList: ParameterList) {
        super.spawn(parameterList)
        parameterList.add(EntityParameters.Villager.VILLAGER_DATA, this.entity.villagerData)
    }

    override fun update(parameterList: ParameterList) {
        super.update(parameterList)
        val villagerData = this.entity.villagerData
        if (villagerData != this.lastVillagerData) {
            parameterList.add(EntityParameters.Villager.VILLAGER_DATA, villagerData)
            this.lastVillagerData = villagerData
        }
    }
}

val LanternEntity.villagerData: VillagerData
    get() {
        val type = this.get(Keys.VILLAGER_TYPE).orElseGet(VillagerTypes.PLAINS)
        val profession = this.get(Keys.PROFESSION_TYPE).orElseGet(ProfessionTypes.NONE)
        val level = this.get(Keys.PROFESSION_LEVEL).orElse(0)
        return VillagerData(type, profession, level)
    }
