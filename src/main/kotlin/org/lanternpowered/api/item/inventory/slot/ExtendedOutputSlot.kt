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

typealias OutputSlot = org.spongepowered.api.item.inventory.slot.OutputSlot

/**
 * An extended version of [OutputSlot].
 */
interface ExtendedOutputSlot : OutputSlot, ExtendedSlot
