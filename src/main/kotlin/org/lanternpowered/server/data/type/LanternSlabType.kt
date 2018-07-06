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
import org.lanternpowered.server.text.translation.TranslationHelper.tr
import org.spongepowered.api.data.type.SlabType
import org.spongepowered.api.text.translation.Translation

enum class LanternSlabType(id: String, translationPart: String) :
        SlabType, CatalogType by DefaultCatalogType.minecraft(id), InternalCatalogType.EnumOrdinal {

    STONE           ("stone", "stone"),
    SAND            ("sandstone", "sand"),
    WOOD            ("wood_old", "wood"),
    COBBLESTONE     ("cobblestone", "cobble"),
    BRICK           ("brick", "brick"),
    SMOOTH_BRICK    ("stone_brick", "smoothStoneBrick"),
    NETHERBRICK     ("nether_brick", "netherBrick"),
    QUARTZ          ("quartz", "quartz"),
    RED_SAND        ("red_sandstone", "red_sandstone");

    private val translation: Translation

    init {
        val blockNumber = this.ordinal / 8
        this.translation = tr("tile.stoneSlab%s.%s.name",
                if (blockNumber > 0) blockNumber.toString() + "" else "", translationPart)
    }

    override fun getTranslation(): Translation = this.translation
    override fun toString(): String = asString()
}
