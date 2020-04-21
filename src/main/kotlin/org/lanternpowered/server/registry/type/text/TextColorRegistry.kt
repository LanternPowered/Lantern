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
package org.lanternpowered.server.registry.type.text

import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2CharOpenHashMap
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.server.registry.InternalCatalogTypeRegistry
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.lanternpowered.server.text.TextConstants
import org.lanternpowered.server.text.format.LanternTextColor
import org.spongepowered.api.util.Color

object TextColorRegistry: InternalCatalogTypeRegistry<TextColor> by internalCatalogTypeRegistry({
    fun register(id: String, color: Color, code: Char? = null) {
        val textColor = LanternTextColor(CatalogKey.minecraft(id), color)
        register(textColor)
        if (code != null) {
            TextColorRegistry.toCode[textColor] = code
            TextColorRegistry.fromCode[code] = textColor
        }
    }

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
    register("none", Color.BLACK)
}) {

    private val toCode = Object2CharOpenHashMap<TextColor>().apply { defaultReturnValue(0.toChar()) }
    private val fromCode = Char2ObjectOpenHashMap<TextColor>()

    /**
     * Gets the formatting color code for the given [TextColor].
     */
    fun getCode(color: TextColor): Char? {
        val code = this.toCode.getChar(color)
        return if (code == 0.toChar()) null else code
    }

    /**
     * Gets the [TextColor] for the formatting code.
     */
    fun getByCode(code: Char): TextColor? = this.fromCode.get(code)
}
