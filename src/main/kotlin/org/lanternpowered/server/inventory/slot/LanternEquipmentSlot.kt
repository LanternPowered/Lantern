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
package org.lanternpowered.server.inventory.slot

import org.lanternpowered.api.item.inventory.slot.ExtendedEquipmentSlot
import org.lanternpowered.server.inventory.InventoryView
import org.lanternpowered.server.item.predicate.EquipmentItemPredicate
import org.spongepowered.api.item.inventory.equipment.EquipmentType

class LanternEquipmentSlot : LanternFilteringSlot(), ExtendedEquipmentSlot {

    override fun isValidItem(type: EquipmentType): Boolean =
            (this.filter as? EquipmentItemPredicate)?.test(type) ?: true

    override fun instantiateView(): InventoryView<LanternEquipmentSlot> = View(this)

    private class View(override val backing: LanternEquipmentSlot) : AbstractEquipmentSlotView<LanternEquipmentSlot>()
}
