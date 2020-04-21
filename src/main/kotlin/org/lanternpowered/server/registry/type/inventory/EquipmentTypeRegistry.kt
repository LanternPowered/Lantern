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
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import org.spongepowered.api.item.inventory.equipment.HeldEquipmentType
import org.spongepowered.api.item.inventory.equipment.WornEquipmentType

val EquipmentTypeRegistry = catalogTypeRegistry<EquipmentType> {
    register(LanternEquipmentType(CatalogKey.minecraft("all")) { true })
    register(LanternEquipmentType(CatalogKey.minecraft("equipped")) {
        type -> type is LanternHeldEquipmentType || type is LanternWornEquipmentType
    })
    register(LanternHeldEquipmentType(CatalogKey.minecraft("held")) {
        type -> type is LanternHeldEquipmentType
    })
    register(LanternHeldEquipmentType(CatalogKey.minecraft("main_hand")))
    register(LanternHeldEquipmentType(CatalogKey.minecraft("off_hand")))
    register(LanternWornEquipmentType(CatalogKey.minecraft("worn")) {
        type -> type is LanternWornEquipmentType
    })
    register(LanternWornEquipmentType(CatalogKey.minecraft("boots")))
    register(LanternWornEquipmentType(CatalogKey.minecraft("chestplate")))
    register(LanternWornEquipmentType(CatalogKey.minecraft("headwear")))
    register(LanternWornEquipmentType(CatalogKey.minecraft("leggings")))
}

private open class LanternEquipmentType(
        key: CatalogKey, private val childChecker: (EquipmentType) -> Boolean
) : DefaultCatalogType(key), EquipmentType {

    override fun includes(other: EquipmentType): Boolean =
            other == this || this.childChecker(other)
}

private class LanternHeldEquipmentType(key: CatalogKey, childChecker: (EquipmentType) -> Boolean = { false }) :
        LanternEquipmentType(key, childChecker), HeldEquipmentType

private class LanternWornEquipmentType(key: CatalogKey, childChecker: (EquipmentType) -> Boolean = { _ -> false }) :
        LanternEquipmentType(key, childChecker), WornEquipmentType
