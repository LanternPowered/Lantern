/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
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

import org.lanternpowered.server.catalog.SimpleCatalogType;

public enum LanternPlantTypes implements LanternPlantType, SimpleCatalogType {

    DANDELION           (FlowerColor.YELLOW, 0, "dandelion"),
    POPPY               (FlowerColor.RED, 0, "poppy"),
    BLUE_ORCHID         (FlowerColor.RED, 1, "blue_orchid"),
    ALLIUM              (FlowerColor.RED, 2, "allium"),
    HOUSTONIA           (FlowerColor.RED, 3, "houstonia"),
    RED_TULIP           (FlowerColor.RED, 4, "red_tulip"),
    ORANGE_TULIP        (FlowerColor.RED, 5, "orange_tulip"),
    WHITE_TULIP         (FlowerColor.RED, 6, "white_tulip"),
    PINK_TULIP          (FlowerColor.RED, 7, "pink_tulip"),
    OXEYE_DAISY         (FlowerColor.RED, 8, "oxeye_daisy")
    ;

    private final FlowerColor flowerColor;
    private final String identifier;

    private final byte internalId;

    LanternPlantTypes(FlowerColor flowerColor, int internalId, String identifier) {
        this.internalId = (byte) internalId;
        this.flowerColor = flowerColor;
        this.identifier = identifier;
    }

    @Override
    public String getId() {
        return this.identifier;
    }

    @Override
    public FlowerColor getFlowerColor() {
        return this.flowerColor;
    }

    @Override
    public byte getInternalId() {
        return this.internalId;
    }
}
