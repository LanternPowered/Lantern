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
package org.lanternpowered.api.data.type.hand

import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes
import org.spongepowered.api.item.inventory.equipment.HeldEquipmentType

fun HandType.getEquipmentType(): HeldEquipmentType =
        if (this == HandTypes.MAIN_HAND.get()) EquipmentTypes.MAIN_HAND.get() else EquipmentTypes.OFF_HAND.get()
