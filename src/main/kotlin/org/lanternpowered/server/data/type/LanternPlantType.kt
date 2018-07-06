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
import org.lanternpowered.api.text.translation.Translatable
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.InternalCatalogType
import org.lanternpowered.server.catalog.asString
import org.lanternpowered.server.text.translation.Translated
import org.spongepowered.api.data.type.PlantType

enum class LanternPlantType(override val internalId: Int, id: String, translationPart: String) :
        PlantType, CatalogType by DefaultCatalogType.minecraft(id), InternalCatalogType,
        Translatable by Translated("tile.${if (internalId < 16) "flower1" else "flower2"}.$translationPart.name") {

    DANDELION       (0, "dandelion", "dandelion"),
    POPPY           (16, "poppy", "poppy"),
    BLUE_ORCHID     (17, "blue_orchid", "blueOrchid"),
    ALLIUM          (18, "allium", "allium"),
    HOUSTONIA       (19, "houstonia", "houstonia"),
    RED_TULIP       (20, "red_tulip", "tulipRed"),
    ORANGE_TULIP    (21, "orange_tulip", "tulipOrange"),
    WHITE_TULIP     (22, "white_tulip", "tulipWhite"),
    PINK_TULIP      (23, "pink_tulip", "tulipPink"),
    OXEYE_DAISY     (24, "oxeye_daisy", "oxeyeDaisy");

    override fun toString(): String = asString()
}
