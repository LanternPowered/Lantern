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
import org.spongepowered.api.data.type.RailDirection

enum class LanternRailDirection(id: String) : RailDirection,
        CatalogType by DefaultCatalogType.minecraft(id), InternalCatalogType.EnumOrdinal {

    NORTH_SOUTH         ("north_south"),
    EAST_WEST           ("east_west"),
    ASCENDING_EAST      ("ascending_east"),
    ASCENDING_WEST      ("ascending_west"),
    ASCENDING_NORTH     ("ascending_north"),
    ASCENDING_SOUTH     ("ascending_south"),
    SOUTH_EAST          ("south_east"),
    SOUTH_WEST          ("south_west"),
    NORTH_WEST          ("north_west"),
    NORTH_EAST          ("north_east");

    private lateinit var next: RailDirection

    override fun cycleNext(): RailDirection = this.next
    override fun toString(): String = asString()

    companion object {

        init {
            val values = values()
            for (i in values.indices) {
                values[i].next = values[(i + 1) % values.size]
            }
        }
    }
}
