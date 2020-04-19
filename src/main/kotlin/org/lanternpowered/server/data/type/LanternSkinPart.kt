/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
