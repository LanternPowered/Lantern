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
import org.lanternpowered.server.inventory.AbstractSlot
import org.spongepowered.api.item.inventory.equipment.EquipmentType

abstract class AbstractEquipmentSlotView<T> : AbstractFilteringSlotView<T>(), ExtendedEquipmentSlot
    where T : AbstractSlot,
          T : ExtendedEquipmentSlot {

    override fun isValidItem(type: EquipmentType): Boolean =
            this.backing.isValidItem(type)
}
