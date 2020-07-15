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

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.customInternalCatalogTypeRegistry
import org.spongepowered.api.data.type.BannerPatternShape

val BannerPatternShapeRegistry = customInternalCatalogTypeRegistry<BannerPatternShape, String> {
    fun register(internalId: String, id: String) =
            register(internalId, LanternBannerPatternShape(ResourceKey.minecraft(id)))

    register("b", "base")
    register("bo", "border")
    register("bri", "bricks")
    register("mc", "circle_middle")
    register("cre", "creeper")
    register("cr", "cross")
    register("cbo", "curly_border")
    register("ld", "diagonal_left")
    register("lud", "diagonal_left_mirror")
    register("rd", "diagonal_right")
    register("rud", "diagonal_right_mirror")
    register("flo", "flower")
    register("gra", "gradient")
    register("gru", "gradient_up")
    register("hh", "half_horizontal")
    register("hhb", "half_horizontal_mirror")
    register("vh", "half_vertical")
    register("vhr", "half_vertical_mirror")
    register("moj", "mojang")
    register("mr", "rhombus_middle")
    register("sku", "skull")
    register("bl", "square_bottom_left")
    register("br", "square_bottom_right")
    register("tl", "square_top_left")
    register("tr", "square_top_right")
    register("sc", "straight_cross")
    register("bs", "stripe_bottom")
    register("cs", "stripe_center")
    register("dls", "stripe_downleft")
    register("drs", "stripe_downright")
    register("ls", "stripe_left")
    register("ms", "stripe_middle")
    register("rs", "stripe_right")
    register("ss", "stripe_small")
    register("ts", "stripe_top")
    register("bts", "triangles_bottom")
    register("tts", "triangles_top")
    register("bt", "triangle_bottom")
    register("tt", "triangle_top")
}

private class LanternBannerPatternShape(key: ResourceKey) : DefaultCatalogType(key), BannerPatternShape
