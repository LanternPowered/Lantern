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
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.text.format.TextStyleType
import org.lanternpowered.api.text.format.TextStyles
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule
import org.lanternpowered.server.game.registry.EarlyRegistration
import org.lanternpowered.server.text.TextConstants
import org.lanternpowered.server.text.format.LanternTextStyle

object TextStyleRegistryModule : DefaultCatalogRegistryModule<TextStyleType>(TextStyles::class) {

    var NONE: LanternTextStyle by initOnce()
        private set

    @EarlyRegistration
    override fun registerDefaults() {
        register(LanternTextStyle.Real(CatalogKey.minecraft("bold"), TextConstants.BOLD, bold = true))
        register(LanternTextStyle.Real(CatalogKey.minecraft("italic"), TextConstants.ITALIC, italic = true))
        register(LanternTextStyle.Real(CatalogKey.minecraft("underline"), TextConstants.UNDERLINE, underline = true))
        register(LanternTextStyle.Real(CatalogKey.minecraft("strikethrough"), TextConstants.STRIKETHROUGH, strikethrough = true))
        register(LanternTextStyle.Real(CatalogKey.minecraft("obfuscated"), TextConstants.OBFUSCATED, obfuscated = true))
        register(LanternTextStyle.Real(CatalogKey.minecraft("reset"), TextConstants.RESET,
                false, false, false, false, false))
        NONE = register(LanternTextStyle.None(CatalogKey.minecraft("none")))
    }
}
