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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.parameter.ParameterList
import org.lanternpowered.server.network.value.VillagerData

class ZombieVillagerEntityProtocol<E : LanternEntity>(entity: E) : AbstractZombieEntityProtocol<E>(entity) {

    companion object {
        private val TYPE = minecraftKey("zombie_villager")
    }

    private var lastVillagerData: VillagerData? = null
    private var lastIsConverting = false

    override val mobType: NamespacedKey get() = TYPE

    override fun spawn(parameterList: ParameterList) {
        super.spawn(parameterList)
        parameterList.add(EntityParameters.ZombieVillager.VILLAGER_DATA, this.entity.villagerData)
        parameterList.add(EntityParameters.ZombieVillager.IS_CONVERTING, this.entity.get(LanternKeys.IS_CONVERTING).orElse(false))
    }

    override fun update(parameterList: ParameterList) {
        super.update(parameterList)
        val villagerData = this.entity.villagerData
        if (villagerData != this.lastVillagerData) {
            parameterList.add(EntityParameters.ZombieVillager.VILLAGER_DATA, villagerData)
            this.lastVillagerData = villagerData
        }
        val isConverting = this.entity.get(LanternKeys.IS_CONVERTING).orElse(false)
        if (isConverting != this.lastIsConverting) {
            parameterList.add(EntityParameters.ZombieVillager.IS_CONVERTING, isConverting)
            this.lastIsConverting = isConverting
        }
    }
}
