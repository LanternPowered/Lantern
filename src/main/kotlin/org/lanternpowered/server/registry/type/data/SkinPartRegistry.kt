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
package org.lanternpowered.server.registry.type.data

import com.google.common.collect.ImmutableSet
import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.namespace.minecraftKey
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.InternalCatalogTypeRegistry
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.data.type.SkinPart

object SkinPartRegistry : InternalCatalogTypeRegistry<SkinPart> by internalCatalogTypeRegistry({
    fun register(id: String) =
            register(LanternSkinPart(minecraftKey(id)))

    register("cape")
    register("jacket")
    register("left_sleeve")
    register("right_sleeve")
    register("left_pants_leg")
    register("right_pants_leg")
    register("hat")
}) {

    /**
     * Converts the bit pattern into a set of skin parts.
     *
     * @param bitPattern the bit pattern
     * @return the skin parts
     */
    fun fromBitPattern(bitPattern: Int): Set<SkinPart> {
        val parts = ImmutableSet.builder<SkinPart>()
        val count = Integer.bitCount(bitPattern)
        for (index in 0 until count) {
            val part = get(index)
            if (part != null && bitPattern and (1 shl index) != 0) {
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
    fun toBitPattern(skinParts: Collection<SkinPart>): Int {
        var bitPattern = 0
        for (part in skinParts) {
            bitPattern = bitPattern or (1 shl getId(part))
        }
        return bitPattern
    }
}

private class LanternSkinPart(key: NamespacedKey) : DefaultCatalogType(key), SkinPart,
        TextRepresentable by translatableTextOf("options.modelPart.${key.value}")
