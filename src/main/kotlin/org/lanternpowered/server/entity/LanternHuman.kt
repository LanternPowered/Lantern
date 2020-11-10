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

import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.toPlain
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.inventory.equipment.AbstractArmorEquipable
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes
import org.lanternpowered.server.network.entity.EntityProtocolTypes
import org.lanternpowered.api.data.Keys
import org.lanternpowered.api.item.inventory.equipment.EquipmentInventory
import org.spongepowered.api.entity.living.Human
import java.util.UUID

class LanternHuman(creationData: EntityCreationData) : LanternAgent(creationData), Human, AbstractArmorEquipable {

    private val equipmentInventory: EquipmentInventory

    init {
        this.protocolType = EntityProtocolTypes.HUMAN
        this.equipmentInventory = VanillaInventoryArchetypes.ENTITY_EQUIPMENT.build()

        keyRegistry {
            register(LanternKeys.DISPLAYED_SKIN_PARTS, emptySet())
            register(LanternKeys.POSE, Pose.STANDING)
            register(Keys.SKIN_PROFILE_PROPERTY)
        }
    }

    override fun getName(): String = this.get(Keys.DISPLAY_NAME).map(Text::toPlain).orElse("Unknown")
    override fun getEquipment(): EquipmentInventory = this.equipmentInventory

    override fun useSkinFor(minecraftAccount: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun useSkinFor(minecraftUsername: String?): Boolean {
        TODO("Not yet implemented")
    }
}
