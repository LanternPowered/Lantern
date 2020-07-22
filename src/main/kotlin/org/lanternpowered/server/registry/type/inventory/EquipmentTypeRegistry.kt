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
package org.lanternpowered.server.registry.type.inventory

import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.spongepowered.api.item.inventory.equipment.EquipmentGroup
import org.spongepowered.api.item.inventory.equipment.EquipmentGroups
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import java.util.function.Supplier

val EquipmentTypeRegistry = catalogTypeRegistry<EquipmentType> {
    fun register(id: String, group: Supplier<out EquipmentGroup>) =
            register(LanternEquipmentType(minecraftKey(id), group.get()))

    register("main_hand", EquipmentGroups.HELD)
    register("off_hand", EquipmentGroups.HELD)
    register("feet", EquipmentGroups.WORN)
    register("chest", EquipmentGroups.WORN)
    register("head", EquipmentGroups.WORN)
    register("legs", EquipmentGroups.WORN)
}

private open class LanternEquipmentType(
        key: NamespacedKey,
        private val group: EquipmentGroup
) : DefaultCatalogType(key), EquipmentType {

    override fun getGroup(): EquipmentGroup = this.group
}
