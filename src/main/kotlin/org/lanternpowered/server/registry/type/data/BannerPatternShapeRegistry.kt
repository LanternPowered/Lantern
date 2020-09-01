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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.util.index.indexOf
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.data.type.BannerPatternShape

val BannerPatternShapeRegistry = internalCatalogTypeRegistry<BannerPatternShape> {
    fun register(internalId: Int, internalStringId: String, id: String) =
            register(internalId, LanternBannerPatternShape(minecraftKey(id), internalStringId))

    register(0, "b", "base")
    register(1, "bl", "square_bottom_left")
    register(2, "br", "square_bottom_right")
    register(3, "tl", "square_top_left")
    register(4, "tr", "square_top_right")
    register(5, "bs", "stripe_bottom")
    register(6, "ts", "stripe_top")
    register(7, "ls", "stripe_left")
    register(8, "rs", "stripe_right")
    register(9, "cs", "stripe_center")
    register(10, "ms", "stripe_middle")
    register(11, "drs", "stripe_downright")
    register(12, "dls", "stripe_downleft")
    register(13, "ss", "small_stripes")
    register(14, "cr", "cross")
    register(15, "sc", "straight_cross")
    register(16, "bt", "triangle_bottom")
    register(17, "tt", "triangle_top")
    register(18, "bts", "triangles_bottom")
    register(19, "tts", "triangles_top")
    register(20, "ld", "diagonal_left")
    register(21, "rd", "diagonal_up_right")
    register(22, "lud", "diagonal_up_left")
    register(23, "rud", "diagonal_right")
    register(24, "mc", "circle")
    register(25, "mr", "rhombus")
    register(26, "vh", "half_vertical")
    register(27, "hh", "half_horizontal")
    register(28, "vhr", "half_vertical_right")
    register(29, "hhb", "half_horizontal_bottom")
    register(30, "bo", "border")
    register(31, "cbo", "curly_border")
    register(32, "gra", "gradient")
    register(33, "gru", "gradient_up")
    register(34, "bri", "bricks")
    register(35, "gbl", "globe")
    register(36, "cre", "creeper")
    register(37, "sku", "skull")
    register(38, "flo", "flower")
    register(39, "moj", "mojang")
}

val BannerPatternInternalStringIdIndex = indexOf(BannerPatternShapeRegistry.all) { shape ->
    (shape as LanternBannerPatternShape).internalStringId
}

private class LanternBannerPatternShape(key: NamespacedKey, val internalStringId: String) : DefaultCatalogType(key), BannerPatternShape
