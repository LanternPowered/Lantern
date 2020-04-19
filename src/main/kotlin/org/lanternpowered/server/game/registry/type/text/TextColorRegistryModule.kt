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
package org.lanternpowered.server.game.registry.type.text

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.catalog.CatalogKeys
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.api.text.format.TextColors
import org.lanternpowered.api.util.Color
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule
import org.lanternpowered.server.game.registry.EarlyRegistration
import org.lanternpowered.server.text.TextConstants
import org.lanternpowered.server.text.format.LanternTextColor

object TextColorRegistryModule : DefaultCatalogRegistryModule<TextColor>(TextColors::class) {

    @EarlyRegistration
    override fun registerDefaults() {
        val register = { name: String, color: Color, code: Char ->
            register(LanternTextColor.Formatting(CatalogKeys.minecraft(name), color, code)) }

        register("black", Color.BLACK, TextConstants.BLACK)
        register("dark_blue", Color.ofRgb(0x0000AA), TextConstants.DARK_BLUE)
        register("dark_green", Color.ofRgb(0x00AA00), TextConstants.DARK_GREEN)
        register("dark_aqua", Color.ofRgb(0x00AAAA), TextConstants.DARK_AQUA)
        register("dark_red", Color.ofRgb(0xAA0000), TextConstants.DARK_RED)
        register("dark_purple", Color.ofRgb(0xAA00AA), TextConstants.DARK_PURPLE)
        register("gold", Color.ofRgb(0xFFAA00), TextConstants.GOLD)
        register("gray", Color.ofRgb(0xAAAAAA), TextConstants.GRAY)
        register("dark_gray", Color.ofRgb(0x555555), TextConstants.DARK_GRAY)
        register("blue", Color.ofRgb(0x5555FF), TextConstants.BLUE)
        register("green", Color.ofRgb(0x55FF55), TextConstants.GREEN)
        register("aqua", Color.ofRgb(0x55FFFF), TextConstants.AQUA)
        register("red", Color.ofRgb(0xFF5555), TextConstants.RED)
        register("light_purple", Color.ofRgb(0xFF55FF), TextConstants.LIGHT_PURPLE)
        register("yellow", Color.ofRgb(0xFFFF55), TextConstants.YELLOW)
        register("white", Color.WHITE, TextConstants.WHITE)
        register("reset", Color.WHITE, TextConstants.RESET)

        register(LanternTextColor(CatalogKey.minecraft("none"), Color.BLACK))
    }
}
