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
package org.lanternpowered.server.boss

import org.lanternpowered.api.boss.BossBarColor
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.server.catalog.DefaultCatalogType

class LanternBossBarColor(key: CatalogKey, private val color: TextColor) : DefaultCatalogType(key), BossBarColor {
    override fun getColor(): TextColor = this.color
}
