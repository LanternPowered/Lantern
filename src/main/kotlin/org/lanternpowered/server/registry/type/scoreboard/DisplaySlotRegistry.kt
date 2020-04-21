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
@file:JvmName("DisplaySlotRegistry")
package org.lanternpowered.server.registry.type.scoreboard

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.lanternpowered.server.registry.type.text.TextColorRegistry
import org.lanternpowered.server.scoreboard.LanternDisplaySlot
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot

@get:JvmName("get")
val DisplaySlotRegistry = internalCatalogTypeRegistry<DisplaySlot> {
    fun register(internalId: Int, id: String) =
            register(internalId, LanternDisplaySlot(CatalogKey.minecraft(id), null, null))

    register(0, "list")
    register(2, "below_name")

    val sidebarByTeamColor = mutableMapOf<TextColor?, DisplaySlot>()
    val sidebarWithTeamColor = { teamColor: TextColor? -> sidebarByTeamColor[teamColor]!! }

    fun registerSidebar(internalId: Int, id: String, teamColor: TextColor? = null) {
        val sidebar = register(internalId, LanternDisplaySlot(CatalogKey.minecraft(id), teamColor, sidebarWithTeamColor))
        sidebarByTeamColor[teamColor] = sidebar
    }

    registerSidebar(1, "sidebar", null)
    for (teamColor in TextColorRegistry) {
        val id = "sidebar_" + teamColor.key.value
        // val name = "sidebar.team." + textColor.key.value
        registerSidebar(3 + TextColorRegistry.getId(teamColor), id, teamColor)
    }
}
