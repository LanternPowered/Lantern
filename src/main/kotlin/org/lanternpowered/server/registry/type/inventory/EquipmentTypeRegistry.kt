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
import org.lanternpowered.api.namespace.NamespacedKey
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import org.spongepowered.api.item.inventory.equipment.HeldEquipmentType
import org.spongepowered.api.item.inventory.equipment.WornEquipmentType

val EquipmentTypeRegistry = catalogTypeRegistry<EquipmentType> {
    register(LanternEquipmentType(NamespacedKey.minecraft("all")) { true })
    register(LanternEquipmentType(NamespacedKey.minecraft("equipped")) {
        type -> type is LanternHeldEquipmentType || type is LanternWornEquipmentType
    })
    register(LanternHeldEquipmentType(NamespacedKey.minecraft("held")) {
        type -> type is LanternHeldEquipmentType
    })
    register(LanternHeldEquipmentType(NamespacedKey.minecraft("main_hand")))
    register(LanternHeldEquipmentType(NamespacedKey.minecraft("off_hand")))
    register(LanternWornEquipmentType(NamespacedKey.minecraft("worn")) {
        type -> type is LanternWornEquipmentType
    })
    register(LanternWornEquipmentType(NamespacedKey.minecraft("boots")))
    register(LanternWornEquipmentType(NamespacedKey.minecraft("chestplate")))
    register(LanternWornEquipmentType(NamespacedKey.minecraft("headwear")))
    register(LanternWornEquipmentType(NamespacedKey.minecraft("leggings")))
}

private open class LanternEquipmentType(
        key: NamespacedKey, private val childChecker: (EquipmentType) -> Boolean
) : DefaultCatalogType(key), EquipmentType {

    override fun includes(other: EquipmentType): Boolean =
            other == this || this.childChecker(other)
}

private class LanternHeldEquipmentType(key: NamespacedKey, childChecker: (EquipmentType) -> Boolean = { false }) :
        LanternEquipmentType(key, childChecker), HeldEquipmentType

private class LanternWornEquipmentType(key: NamespacedKey, childChecker: (EquipmentType) -> Boolean = { _ -> false }) :
        LanternEquipmentType(key, childChecker), WornEquipmentType
