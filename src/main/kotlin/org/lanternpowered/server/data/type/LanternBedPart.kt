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
import org.lanternpowered.server.catalog.InternalCatalogType
import org.lanternpowered.server.catalog.asString

enum class LanternBedPart(id: String) : CatalogType by DefaultCatalogType.minecraft(id), InternalCatalogType.EnumOrdinal {

    FOOT("foot"),
    HEAD("head");

    override fun toString(): String = asString()
}
