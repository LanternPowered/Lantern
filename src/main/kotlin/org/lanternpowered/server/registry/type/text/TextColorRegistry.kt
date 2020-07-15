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

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.lanternpowered.server.text.format.LanternTextColor
import org.spongepowered.api.util.Color

val TextColorRegistry = internalCatalogTypeRegistry<TextColor> {
    fun register(id: String, color: Color) {
        val textColor = LanternTextColor(ResourceKey.minecraft(id), color)
        register(textColor)
    }

    register("black", Color.BLACK)
    register("dark_blue", Color.ofRgb(0x0000AA))
    register("dark_green", Color.ofRgb(0x00AA00))
    register("dark_aqua", Color.ofRgb(0x00AAAA))
    register("dark_red", Color.ofRgb(0xAA0000))
    register("dark_purple", Color.ofRgb(0xAA00AA))
    register("gold", Color.ofRgb(0xFFAA00))
    register("gray", Color.ofRgb(0xAAAAAA))
    register("dark_gray", Color.ofRgb(0x555555))
    register("blue", Color.ofRgb(0x5555FF))
    register("green", Color.ofRgb(0x55FF55))
    register("aqua", Color.ofRgb(0x55FFFF))
    register("red", Color.ofRgb(0xFF5555))
    register("light_purple", Color.ofRgb(0xFF55FF))
    register("yellow", Color.ofRgb(0xFFFF55))
    register("white", Color.WHITE)
    register("reset", Color.WHITE)
    register("none", Color.BLACK)
}
