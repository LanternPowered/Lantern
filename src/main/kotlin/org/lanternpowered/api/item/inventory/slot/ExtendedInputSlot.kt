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
package org.lanternpowered.api.item.inventory.slot

typealias InputSlot = org.spongepowered.api.item.inventory.slot.InputSlot

/**
 * An extended version of [InputSlot].
 */
interface ExtendedInputSlot : InputSlot, ExtendedFilteringSlot
