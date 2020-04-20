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
package org.lanternpowered.server.advancement

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.text.format.TextFormat
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.advancement.AdvancementType

class LanternAdvancementType(key: CatalogKey, private val textFormat: TextFormat) : DefaultCatalogType(key), AdvancementType {
    override fun getTextFormat(): TextFormat = this.textFormat
}
