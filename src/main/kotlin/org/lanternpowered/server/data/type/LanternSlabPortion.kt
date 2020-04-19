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
package org.lanternpowered.server.data.type

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.asString
import org.spongepowered.api.data.type.SlabPortion

enum class LanternSlabPortion(id: String) : SlabPortion, CatalogType by DefaultCatalogType.minecraft(id) {

    TOP         ("top"),
    BOTTOM      ("bottom"),
    DOUBLE      ("full"),
    ;

    override fun toString(): String = asString()
}
