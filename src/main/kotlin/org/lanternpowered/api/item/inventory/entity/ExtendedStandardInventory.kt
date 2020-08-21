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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.item.inventory.entity

import org.lanternpowered.api.item.inventory.ExtendedInventory
import org.lanternpowered.api.item.inventory.equipment.ExtendedEquipmentInventory
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot

typealias StandardInventory = org.spongepowered.api.item.inventory.entity.StandardInventory

/**
 * An extended version of [StandardInventory].
 */
interface ExtendedStandardInventory : ExtendedInventory, StandardInventory {

    override fun getArmor(): ExtendedEquipmentInventory

    override fun getEquipment(): ExtendedEquipmentInventory

    override fun getOffhand(): ExtendedSlot

    override fun getPrimary(): ExtendedPrimaryPlayerInventory
}
