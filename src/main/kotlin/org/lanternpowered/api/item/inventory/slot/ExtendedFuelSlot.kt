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

typealias FuelSlot = org.spongepowered.api.item.inventory.slot.FuelSlot

/**
 * An extended version of [FuelSlot].
 */
interface ExtendedFuelSlot : FuelSlot, ExtendedInputSlot
