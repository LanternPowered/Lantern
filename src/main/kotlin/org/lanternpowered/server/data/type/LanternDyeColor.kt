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
import org.lanternpowered.server.text.translation.Translated
import org.spongepowered.api.data.type.DyeColor
import org.spongepowered.api.text.translation.Translatable
import org.spongepowered.api.util.Color

enum class LanternDyeColor(id: String, rgb: Int) : DyeColor, CatalogType by DefaultCatalogType.minecraft(id),
        InternalCatalogType.EnumOrdinal, Translatable by Translated("color.$id.name") {

    WHITE       ("white", 16777215),
    ORANGE      ("orange", 14188339),
    MAGENTA     ("magenta", 11685080),
    LIGHT_BLUE  ("light_blue", 6724056),
    YELLOW      ("yellow", 15066419),
    LIME        ("lime", 8375321),
    PINK        ("pink", 15892389),
    GRAY        ("gray", 5000268),
    LIGHT_GRAY  ("light_gray", 10066329),
    CYAN        ("cyan", 5013401),
    PURPLE      ("purple", 8339378),
    BLUE        ("blue", 3361970),
    BROWN       ("brown", 6704179),
    GREEN       ("green", 6717235),
    RED         ("red", 10040115),
    BLACK       ("black", 1644825),
    ;

    private val color = Color.ofRgb(rgb)

    override fun getColor(): Color = this.color
    override fun toString(): String = asString()
}
