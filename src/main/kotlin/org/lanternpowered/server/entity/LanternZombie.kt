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
package org.lanternpowered.server.entity

import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.inventory.AbstractArmorEquipable
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes
import org.lanternpowered.server.network.entity.EntityProtocolTypes
import org.spongepowered.api.entity.living.monster.zombie.Zombie
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory

open class LanternZombie(creationData: EntityCreationData) : LanternAgent(creationData), Zombie, AbstractArmorEquipable {

    private val equipmentInventory: EquipmentInventory

    init {
        this.protocolType = EntityProtocolTypes.ZOMBIE
        this.equipmentInventory = VanillaInventoryArchetypes.ENTITY_EQUIPMENT.build()

        keyRegistry {
            register(LanternKeys.POSE, Pose.STANDING)
        }
    }

    override fun getInventory(): EquipmentInventory = this.equipmentInventory
}
