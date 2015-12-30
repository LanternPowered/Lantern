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

import com.google.common.collect.ImmutableSet;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.lanternpowered.server.catalog.SimpleLanternCatalogType;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.data.type.SkinPart;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Collection;
import java.util.Set;

@NonnullByDefault
public final class LanternSkinPart extends SimpleLanternCatalogType implements SkinPart {

    private static final TIntObjectMap<LanternSkinPart> lookup = new TIntObjectHashMap<>();

    private final int mask;
    private final int index;

    public LanternSkinPart(String identifier, int index) {
        super(identifier);
        this.mask = index << 1;
        this.index = index;
        // Add to the lookup
        // TODO: Should this be moved to the registry?
        lookup.put(index, this);
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public Translation getTranslation() {
        return LanternGame.get().getRegistry().getTranslationManager().get(
                "options.modelPart." + this.getId());
    }

    /**
     * Converts the bit pattern into a set of skin parts.
     *
     * @param bitPattern the bit pattern
     * @return the skin parts
     */
    public static Set<SkinPart> fromBitPattern(int bitPattern) {
        ImmutableSet.Builder<SkinPart> parts = ImmutableSet.builder();
        int count = Integer.bitCount(bitPattern);
        for (int i = 0; i < count; i++) {
            LanternSkinPart part = lookup.get(i);
            if (part != null && (bitPattern & part.mask) != 0) {
                parts.add(part);
            }
        }
        return parts.build();
    }

    /**
     * Converts the collection of skin parts into a bit pattern.
     *
     * @param skinParts the skin parts
     * @return the bit pattern
     */
    public static int toBitPattern(Collection<SkinPart> skinParts) {
        int bitPattern = 0;
        for (SkinPart part : skinParts) {
            bitPattern |= ((LanternSkinPart) part).mask;
        }
        return bitPattern;
    }

}
