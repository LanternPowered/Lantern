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
package org.lanternpowered.server.inventory.entity.hotbar

import org.lanternpowered.api.item.inventory.hotbar.Hotbar
import org.lanternpowered.api.item.inventory.hotbar.selectedSlot
import org.lanternpowered.server.inventory.AbstractSlot
import org.lanternpowered.server.inventory.slot.AbstractSlotView

open class SelectedHotbarSlotView(private val hotbar: Hotbar) : AbstractSlotView<AbstractSlot>() {

    override val backing: AbstractSlot
        get() = this.hotbar.selectedSlot as AbstractSlot
}
