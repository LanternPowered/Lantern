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
