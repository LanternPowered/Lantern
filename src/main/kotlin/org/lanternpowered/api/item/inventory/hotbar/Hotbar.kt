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
package org.lanternpowered.api.item.inventory.hotbar

import org.lanternpowered.api.util.optional.orNull
import org.spongepowered.api.item.inventory.Slot

typealias Hotbar = org.spongepowered.api.item.inventory.entity.Hotbar

/**
 * The [Slot] that's currently selected.
 */
val Hotbar.selectedSlot: Slot
    get() = this.getSlot(this.selectedSlotIndex).orNull() ?: error("The selected slot doesn't exist.")
