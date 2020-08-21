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
package org.lanternpowered.server.inventory.equipment

import org.lanternpowered.server.inventory.AbstractChildrenInventory
import org.lanternpowered.server.inventory.AbstractMutableInventory
import org.lanternpowered.server.inventory.InventoryView

class LanternEquipmentInventory(children: List<AbstractMutableInventory>) : AbstractChildrenInventory(), AbstractEquipmentInventory {

    init {
        this.init(children)
    }

    override fun instantiateView(): InventoryView<AbstractMutableInventory> {
        TODO("Not yet implemented")
    }
}
