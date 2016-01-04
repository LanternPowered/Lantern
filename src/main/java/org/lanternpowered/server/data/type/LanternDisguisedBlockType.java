/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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

import org.lanternpowered.server.catalog.InternalCatalogType;
import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.data.type.DisguisedBlockType;
import org.spongepowered.api.text.translation.Translatable;
import org.spongepowered.api.text.translation.Translation;

public enum LanternDisguisedBlockType implements DisguisedBlockType, SimpleCatalogType, InternalCatalogType, Translatable {

    STONE                       ("stone", "stone"),
    COBBLESTONE                 ("cobblestone", "cobble"),
    STONEBRICK                  ("stone_brick", "brick"),
    MOSSY_STONEBRICK            ("mossy_brick", "mossybrick"),
    CRACKED_STONEBRICK          ("cracked_brick", "crackedbrick"),
    CHISELED_STONEBRICK         ("chiseled_brick", "chiseledbrick"),
    ;

    private final String identifier;
    private final Translation translation;

    LanternDisguisedBlockType(String identifier, String translationPart) {
        this.translation = LanternGame.get().getRegistry().getTranslationManager().get(
                "tile.monsterStoneEgg." + translationPart + ".name");
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
        return this.ordinal();
    }

}
