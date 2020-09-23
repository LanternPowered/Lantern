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
package org.lanternpowered.server.inventory

import org.lanternpowered.api.item.inventory.Inventory2D
import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.item.inventory.slotIndex
import org.lanternpowered.api.item.inventory.slotPosition
import org.lanternpowered.server.data.GlobalKeyRegistry
import org.lanternpowered.api.data.Keys

object LanternInventoryData {

    fun init() {
        GlobalKeyRegistry.registerProvider(Keys.SLOT_INDEX) {
            supportedBy { this is Slot }
            get {
                this as Slot
                this.parent().slotIndex(this)
            }
        }
        GlobalKeyRegistry.registerProvider(Keys.SLOT_POSITION) {
            supportedBy { this is Slot && this.parent() is Inventory2D }
            get {
                this as Slot
                (this.parent() as Inventory2D).slotPosition(this)
            }
        }
    }
}
