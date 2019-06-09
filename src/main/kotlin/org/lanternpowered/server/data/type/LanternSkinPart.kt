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

import com.google.common.collect.ImmutableSet
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.text.translation.Translated
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.type.SkinPart
import org.spongepowered.api.text.translation.Translatable

class LanternSkinPart(key: CatalogKey, val index: Int) : DefaultCatalogType(key), SkinPart,
        Translatable by Translated("options.modelPart.${key.value}") {

    private val mask: Int = this.index shl 1

    init {
        // Add to the lookup
        // TODO: Should this be moved to the registry?
        lookup[index] = this
    }

    companion object {

        private val lookup = Int2ObjectOpenHashMap<LanternSkinPart>()

        /**
         * Converts the bit pattern into a set of skin parts.
         *
         * @param bitPattern the bit pattern
         * @return the skin parts
         */
        @JvmStatic
        fun fromBitPattern(bitPattern: Int): Set<SkinPart> {
            val parts = ImmutableSet.builder<SkinPart>()
            val count = Integer.bitCount(bitPattern)
            for (i in 0 until count) {
                val part = lookup.get(i)
                if (part != null && bitPattern and part.mask != 0) {
                    parts.add(part)
                }
            }
            return parts.build()
        }

        /**
         * Converts the collection of skin parts into a bit pattern.
         *
         * @param skinParts the skin parts
         * @return the bit pattern
         */
        @JvmStatic
        fun toBitPattern(skinParts: Collection<SkinPart>): Int {
            var bitPattern = 0
            for (part in skinParts) {
                bitPattern = bitPattern or (part as LanternSkinPart).mask
            }
            return bitPattern
        }
    }
}
