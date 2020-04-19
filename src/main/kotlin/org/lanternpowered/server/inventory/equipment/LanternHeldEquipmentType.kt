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
package org.lanternpowered.server.inventory.equipment

import org.lanternpowered.api.catalog.CatalogKey
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import org.spongepowered.api.item.inventory.equipment.HeldEquipmentType

class LanternHeldEquipmentType @JvmOverloads constructor(key: CatalogKey, childChecker: (EquipmentType) -> Boolean = { false }) :
        LanternEquipmentType(key, childChecker), HeldEquipmentType
