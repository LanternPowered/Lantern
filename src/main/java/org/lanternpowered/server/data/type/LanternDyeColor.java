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
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.Color;

public enum LanternDyeColor implements DyeColor, SimpleCatalogType, InternalCatalogType {

    WHITE           ("white", "white", Color.ofRgb(16777215)),
    ORANGE          ("orange", "orange", Color.ofRgb(14188339)),
    MAGENTA         ("magenta", "magenta", Color.ofRgb(11685080)),
    LIGHT_BLUE      ("light_blue", "lightBlue", Color.ofRgb(6724056)),
    YELLOW          ("yellow", "yellow", Color.ofRgb(15066419)),
    LIME            ("lime", "lime", Color.ofRgb(8375321)),
    PINK            ("pink", "pink", Color.ofRgb(15892389)),
    GRAY            ("gray", "gray", Color.ofRgb(5000268)),
    SILVER          ("silver", "silver", Color.ofRgb(10066329)),
    CYAN            ("cyan", "cyan", Color.ofRgb(5013401)),
    PURPLE          ("purple", "purple", Color.ofRgb(8339378)),
    BLUE            ("blue", "blue", Color.ofRgb(3361970)),
    BROWN           ("brown", "brown", Color.ofRgb(6704179)),
    GREEN           ("green", "green", Color.ofRgb(6717235)),
    RED             ("red", "red", Color.ofRgb(10040115)),
    BLACK           ("black", "black", Color.ofRgb(1644825)),
    ;

    private final String identifier;
    private final String translationBaseKey;
    private final Translation translation;
    private final Color color;

    LanternDyeColor(String identifier, String translationPart, Color color) {
        this.translation = tr("color." + translationPart + ".name");
        this.translationBaseKey = translationPart;
        this.identifier = identifier;
        this.color = color;
    }

    @Override
    public String getId() {
        return this.identifier;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public Translation getTranslation() {
        return this.translation;
    }

    @Override
    public int getInternalId() {
        return ordinal();
    }

    public String getTranslationPart() {
        return this.translationBaseKey;
    }
}
