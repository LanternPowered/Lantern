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

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.lanternpowered.server.registry.type.text.TextColorRegistry
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot

@get:JvmName("get")
val DisplaySlotRegistry = internalCatalogTypeRegistry<DisplaySlot> {
    fun register(internalId: Int, id: String) =
            register(internalId, LanternDisplaySlot(ResourceKey.minecraft(id), null, null))

    register(0, "list")
    register(2, "below_name")

    val sidebarByTeamColor = mutableMapOf<TextColor?, DisplaySlot>()
    val sidebarWithTeamColor = { teamColor: TextColor? -> sidebarByTeamColor[teamColor]!! }

    fun registerSidebar(internalId: Int, id: String, teamColor: TextColor? = null) {
        val sidebar = register(internalId, LanternDisplaySlot(ResourceKey.minecraft(id), teamColor, sidebarWithTeamColor))
        sidebarByTeamColor[teamColor] = sidebar
    }

    registerSidebar(1, "sidebar", null)
    for (teamColor in TextColorRegistry) {
        val id = "sidebar_" + teamColor.key.value
        // val name = "sidebar.team." + textColor.key.value
        registerSidebar(3 + TextColorRegistry.getId(teamColor), id, teamColor)
    }
}

private class LanternDisplaySlot(
        key: ResourceKey,
        private val teamColor: TextColor?,
        private val withTeamColor: ((TextColor?) -> DisplaySlot)?
) : DefaultCatalogType(key), DisplaySlot {

    override fun withTeamColor(color: TextColor?) = this.withTeamColor?.invoke(color) ?: this
    override fun getTeamColor() = this.teamColor.optional()
    override fun toStringHelper() = super.toStringHelper()
            .omitNullValues()
            .add("teamColor", this.teamColor)
}
