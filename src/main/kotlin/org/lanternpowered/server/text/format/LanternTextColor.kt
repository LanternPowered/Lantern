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
package org.lanternpowered.server.text.format

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.util.Color

open class LanternTextColor(key: CatalogKey, private val color: Color) : DefaultCatalogType(key), TextColor {

    override fun getColor() = this.color

    class Formatting(key: CatalogKey, color: Color, override val code: Char) : LanternTextColor(key, color), FormattingCodeHolder {

        override fun toStringHelper() = super.toStringHelper()
                .add("code", this.code)
    }
}
