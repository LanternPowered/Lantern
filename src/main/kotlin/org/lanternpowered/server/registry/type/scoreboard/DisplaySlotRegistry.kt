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

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.text.format.NamedTextColor
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.api.util.index.requireKey
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot

@get:JvmName("get")
val DisplaySlotRegistry = internalCatalogTypeRegistry<DisplaySlot> {
    fun register(internalId: Int, id: String) =
            register(internalId, LanternDisplaySlot(NamespacedKey.minecraft(id), null, null))

    register(0, "list")
    register(2, "below_name")

    val sidebarByTeamColor = mutableMapOf<TextColor?, DisplaySlot>()
    val sidebarWithTeamColor = { teamColor: TextColor? -> sidebarByTeamColor[teamColor]!! }

    fun registerSidebar(internalId: Int, id: String, teamColor: NamedTextColor? = null) {
        val sidebar = register(internalId, LanternDisplaySlot(NamespacedKey.minecraft(id), teamColor, sidebarWithTeamColor))
        sidebarByTeamColor[teamColor] = sidebar
    }

    val colorIndex = NamedTextColor.values()
            .withIndex().associateTo(Object2IntOpenHashMap()) { (index, value) -> value to index }

    registerSidebar(1, "sidebar", null)
    for (teamColor in NamedTextColor.values()) {
        val id = "sidebar_" + NamedTextColor.NAMES.requireKey(teamColor)
        // val name = "sidebar.team." + textColor.key.value
        registerSidebar(3 + colorIndex.getInt(teamColor), id, teamColor)
    }
}

private class LanternDisplaySlot(
        key: NamespacedKey,
        private val teamColor: NamedTextColor?,
        private val withTeamColor: ((NamedTextColor?) -> DisplaySlot)?
) : DefaultCatalogType(key), DisplaySlot {

    override fun withTeamColor(color: NamedTextColor?) = this.withTeamColor?.invoke(color) ?: this
    override fun getTeamColor() = this.teamColor.optional()
    override fun toStringHelper() = super.toStringHelper()
            .omitNullValues()
            .add("teamColor", this.teamColor)
}
