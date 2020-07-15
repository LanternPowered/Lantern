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
import org.lanternpowered.api.text.format.TextStyleType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.lanternpowered.server.text.format.LanternTextStyle

val TextStyleRegistry = internalCatalogTypeRegistry<TextStyleType> {
    fun register(id: String, bold: Boolean? = null, italic: Boolean? = null, underline: Boolean? = null,
                 strikethrough: Boolean? = null, obfuscated: Boolean? = null) =
            register(LanternTextStyle.Type(ResourceKey.minecraft(id), bold, italic, underline, strikethrough, obfuscated))

    register("bold", bold = true)
    register("italic", italic = true)
    register("underline", underline = true)
    register("strikethrough", strikethrough = true)
    register("obfuscated", obfuscated = true)
    register("reset", bold = false, italic = false, underline = false, strikethrough = false, obfuscated = false)
}
