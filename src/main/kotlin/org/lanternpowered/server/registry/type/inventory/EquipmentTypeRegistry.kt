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
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import org.spongepowered.api.item.inventory.equipment.HeldEquipmentType
import org.spongepowered.api.item.inventory.equipment.WornEquipmentType

val EquipmentTypeRegistry = catalogTypeRegistry<EquipmentType> {
    register(LanternEquipmentType(ResourceKey.minecraft("all")) { true })
    register(LanternEquipmentType(ResourceKey.minecraft("equipped")) {
        type -> type is LanternHeldEquipmentType || type is LanternWornEquipmentType
    })
    register(LanternHeldEquipmentType(ResourceKey.minecraft("held")) {
        type -> type is LanternHeldEquipmentType
    })
    register(LanternHeldEquipmentType(ResourceKey.minecraft("main_hand")))
    register(LanternHeldEquipmentType(ResourceKey.minecraft("off_hand")))
    register(LanternWornEquipmentType(ResourceKey.minecraft("worn")) {
        type -> type is LanternWornEquipmentType
    })
    register(LanternWornEquipmentType(ResourceKey.minecraft("boots")))
    register(LanternWornEquipmentType(ResourceKey.minecraft("chestplate")))
    register(LanternWornEquipmentType(ResourceKey.minecraft("headwear")))
    register(LanternWornEquipmentType(ResourceKey.minecraft("leggings")))
}

private open class LanternEquipmentType(
        key: ResourceKey, private val childChecker: (EquipmentType) -> Boolean
) : DefaultCatalogType(key), EquipmentType {

    override fun includes(other: EquipmentType): Boolean =
            other == this || this.childChecker(other)
}

private class LanternHeldEquipmentType(key: ResourceKey, childChecker: (EquipmentType) -> Boolean = { false }) :
        LanternEquipmentType(key, childChecker), HeldEquipmentType

private class LanternWornEquipmentType(key: ResourceKey, childChecker: (EquipmentType) -> Boolean = { _ -> false }) :
        LanternEquipmentType(key, childChecker), WornEquipmentType
