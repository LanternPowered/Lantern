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

import org.lanternpowered.api.data.type.DyeColor
import org.lanternpowered.api.data.type.TopHat
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.lanternKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.type.DyeColors
import java.util.function.Supplier

val TopHatRegistry = catalogTypeRegistry<TopHat> {
    fun register(id: String, dyeColor: Supplier<out DyeColor>? = null) =
            register(LanternTopHat(lanternKey(id), dyeColor?.get()))

    register("black_top_hat", DyeColors.BLACK)
    register("blue_top_hat", DyeColors.BLUE)
    register("brown_top_hat", DyeColors.BROWN)
    register("cyan_top_hat", DyeColors.CYAN)
    register("gold_top_hat")
    register("gray_top_hat", DyeColors.GRAY)
    register("green_top_hat", DyeColors.GREEN)
    register("iron_top_hat")
    register("light_blue_top_hat", DyeColors.LIGHT_BLUE)
    register("lime_top_hat", DyeColors.LIME)
    register("magenta_top_hat", DyeColors.MAGENTA)
    register("orange_top_hat", DyeColors.ORANGE)
    register("pink_top_hat", DyeColors.PINK)
    register("purple_top_hat", DyeColors.PURPLE)
    register("red_top_hat", DyeColors.RED)
    register("light_gray_top_hat", DyeColors.LIGHT_GRAY)
    register("snow_top_hat")
    register("stone_top_hat")
    register("white_top_hat", DyeColors.WHITE)
    register("wood_top_hat")
    register("yellow_top_hat", DyeColors.YELLOW)
}

private class LanternTopHat(key: NamespacedKey, override val dyeColor: DyeColor? = null) : DefaultCatalogType(key), TopHat
