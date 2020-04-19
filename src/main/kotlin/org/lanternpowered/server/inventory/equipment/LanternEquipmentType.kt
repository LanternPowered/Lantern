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
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.item.inventory.equipment.EquipmentType

open class LanternEquipmentType @JvmOverloads constructor(
        key: CatalogKey, private val childChecker: (EquipmentType) -> Boolean = { false }
) : DefaultCatalogType(key), EquipmentType {

    override fun includes(other: EquipmentType): Boolean {
        return (other == this || this.childChecker.invoke(other))
    }
}
