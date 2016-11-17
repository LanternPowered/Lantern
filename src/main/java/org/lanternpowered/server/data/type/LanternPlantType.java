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
package org.lanternpowered.server.data.type;

import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import org.lanternpowered.server.catalog.InternalCatalogType;
import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.spongepowered.api.data.type.PlantType;
import org.spongepowered.api.text.translation.Translation;

public enum LanternPlantType implements PlantType, SimpleCatalogType, InternalCatalogType {

    DANDELION           (0, "dandelion", "dandelion"),
    POPPY               (16, "poppy", "poppy"),
    BLUE_ORCHID         (17, "blue_orchid", "blueOrchid"),
    ALLIUM              (18, "allium", "allium"),
    HOUSTONIA           (19, "houstonia", "houstonia"),
    RED_TULIP           (20, "red_tulip", "tulipRed"),
    ORANGE_TULIP        (21, "orange_tulip", "tulipOrange"),
    WHITE_TULIP         (22, "white_tulip", "tulipWhite"),
    PINK_TULIP          (23, "pink_tulip", "tulipPink"),
    OXEYE_DAISY         (24, "oxeye_daisy", "oxeyeDaisy")
    ;

    private final String identifier;
    private final Translation translation;

    private final byte internalId;

    LanternPlantType(int internalId, String identifier, String translationPart) {
        this.translation = tr("tile.%s.%s.name", internalId < 16 ? "flower1" : "flower2", translationPart);
        this.internalId = (byte) internalId;
        this.identifier = identifier;
    }

    @Override
    public Translation getTranslation() {
        return this.translation;
    }

    @Override
    public String getId() {
        return this.identifier;
    }

    @Override
    public int getInternalId() {
        return this.internalId;
    }
}
