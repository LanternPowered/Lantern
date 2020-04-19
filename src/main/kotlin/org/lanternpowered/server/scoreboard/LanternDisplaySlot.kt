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
package org.lanternpowered.server.scoreboard

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.InternalCatalogType
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot
import org.spongepowered.api.text.format.TextColor

class LanternDisplaySlot(
        key: CatalogKey,
        private val teamColor: TextColor?,
        private val withTeamColor: (TextColor?) -> DisplaySlot,
        override val internalId: Int
) : DefaultCatalogType(key), DisplaySlot, InternalCatalogType {

    override fun withTeamColor(color: TextColor?) = this.withTeamColor.invoke(color)
    override fun getTeamColor() = this.teamColor.optional()
    override fun toStringHelper() = super.toStringHelper()
            .omitNullValues()
            .add("teamColor", this.teamColor)
}
