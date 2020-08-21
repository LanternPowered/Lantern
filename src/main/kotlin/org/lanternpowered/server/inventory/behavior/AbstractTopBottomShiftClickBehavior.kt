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
package org.lanternpowered.server.inventory.behavior

import org.lanternpowered.api.item.inventory.ExtendedContainer
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.server.inventory.PlayerTopBottomContainer

/**
 * A [ShiftClickBehavior] that is only applicable to a [PlayerTopBottomContainer].
 */
abstract class AbstractTopBottomShiftClickBehavior : AbstractShiftClickBehavior() {

    override fun getTarget(container: ExtendedContainer, slot: ExtendedSlot): Inventory? {
        return if (container is PlayerTopBottomContainer) {
            this.getTarget(container as PlayerTopBottomContainer, slot)
        } else {
            this.getDefaultTarget(container, slot)
        }
    }

    abstract fun getTarget(container: PlayerTopBottomContainer, slot: ExtendedSlot): Inventory?
}
