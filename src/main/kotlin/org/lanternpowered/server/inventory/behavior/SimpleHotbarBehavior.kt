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

import org.lanternpowered.server.inventory.client.ClientContainer

open class SimpleHotbarBehavior(private val slots: Int) : HotbarBehavior {

    override var selectedSlotIndex: Int = 0

    override fun handleSelectedSlotChange(clientContainer: ClientContainer, slotIndex: Int) {
        this.selectedSlotIndex = slotIndex % this.slots
    }
}
