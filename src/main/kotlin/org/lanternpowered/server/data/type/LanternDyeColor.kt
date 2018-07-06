/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

enum class LanternDyeColor(id: String, val translationPart: String, rgb: Int) :
        DyeColor, CatalogType by DefaultCatalogType.minecraft(id), InternalCatalogType.EnumOrdinal,
        Translatable by Translated("color.$translationPart.name") {

    WHITE       ("white", "white", 16777215),
    ORANGE      ("orange", "orange", 14188339),
    MAGENTA     ("magenta", "magenta", 11685080),
    LIGHT_BLUE  ("light_blue", "lightBlue", 6724056),
    YELLOW      ("yellow", "yellow", 15066419),
    LIME        ("lime", "lime", 8375321),
    PINK        ("pink", "pink", 15892389),
    GRAY        ("gray", "gray", 5000268),
    SILVER      ("silver", "silver", 10066329),
    CYAN        ("cyan", "cyan", 5013401),
    PURPLE      ("purple", "purple", 8339378),
    BLUE        ("blue", "blue", 3361970),
    BROWN       ("brown", "brown", 6704179),
    GREEN       ("green", "green", 6717235),
    RED         ("red", "red", 10040115),
    BLACK       ("black", "black", 1644825),
    ;

    private val color = Color.ofRgb(rgb)

    override fun getColor(): Color = this.color
    override fun toString(): String = asString()
}
