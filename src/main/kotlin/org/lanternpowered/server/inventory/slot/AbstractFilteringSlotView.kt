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

import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.slot.ExtendedFilteringSlot
import org.lanternpowered.server.inventory.AbstractSlot

abstract class AbstractFilteringSlotView<T> : AbstractSlotView<T>(), ExtendedFilteringSlot
    where T : AbstractSlot,
          T : ExtendedFilteringSlot {

    override fun isValidItem(stack: ItemStack): Boolean =
            this.backing.isValidItem(stack)

    override fun isValidItem(type: ItemType): Boolean =
            this.backing.isValidItem(type)
}
