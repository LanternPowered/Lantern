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
package org.lanternpowered.api.item.inventory.archetype

import org.lanternpowered.api.item.inventory.slot.ExtendedSlot

/**
 * Represents an archetype of an [ExtendedSlot].
 */
interface SlotArchetype<T : ExtendedSlot> : InventoryArchetype<T> {

}
