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

import org.lanternpowered.server.catalog.InternalCatalogType;
import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.data.type.PlantType;
import org.spongepowered.api.text.translation.Translation;

public enum LanternPlantType implements PlantType, SimpleCatalogType, InternalCatalogType {

    DANDELION           (FlowerColor.YELLOW, 0, "dandelion", "dandelion"),
    POPPY               (FlowerColor.RED, 0, "poppy", "poppy"),
    BLUE_ORCHID         (FlowerColor.RED, 1, "blue_orchid", "blueOrchid"),
    ALLIUM              (FlowerColor.RED, 2, "allium", "allium"),
    HOUSTONIA           (FlowerColor.RED, 3, "houstonia", "houstonia"),
    RED_TULIP           (FlowerColor.RED, 4, "red_tulip", "tulipRed"),
    ORANGE_TULIP        (FlowerColor.RED, 5, "orange_tulip", "tulipOrange"),
    WHITE_TULIP         (FlowerColor.RED, 6, "white_tulip", "tulipWhite"),
    PINK_TULIP          (FlowerColor.RED, 7, "pink_tulip", "tulipPink"),
    OXEYE_DAISY         (FlowerColor.RED, 8, "oxeye_daisy", "oxeyeDaisy")
    ;

    private final FlowerColor flowerColor;
    private final String identifier;
    private final Translation translation;

    private final byte internalId;

    LanternPlantType(FlowerColor flowerColor, int internalId, String identifier, String translationPart) {
        final String part0 = flowerColor == FlowerColor.YELLOW ? "flower1" : "flower2";
        this.translation = Lantern.getGame().getRegistry().getTranslationManager().get(
                "tile."  + part0 + "." + translationPart + ".name");
        this.internalId = (byte) internalId;
        this.flowerColor = flowerColor;
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

    /**
     * Gets the flower type of the plant, this is needed because the plant types
     * are used by two different blocks.
     *
     * @return the flower color
     */
    public FlowerColor getFlowerColor() {
        return this.flowerColor;
    }

    public enum FlowerColor {
        YELLOW,
        RED,
        ;
    }
}
