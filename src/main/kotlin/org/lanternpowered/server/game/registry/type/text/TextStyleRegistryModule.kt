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
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule
import org.lanternpowered.server.game.registry.EarlyRegistration
import org.lanternpowered.server.text.TextConstants
import org.lanternpowered.server.text.format.LanternTextStyle
import org.spongepowered.api.text.format.TextStyle
import org.spongepowered.api.text.format.TextStyles

class TextStyleRegistryModule : DefaultCatalogRegistryModule<TextStyle.Base>(TextStyles::class) {

    @EarlyRegistration
    override fun registerDefaults() {
        register(LanternTextStyle.Formatting(CatalogKey.minecraft("bold"), TextConstants.BOLD, bold = true))
        register(LanternTextStyle.Formatting(CatalogKey.minecraft("italic"), TextConstants.ITALIC, italic = true))
        register(LanternTextStyle.Formatting(CatalogKey.minecraft("underline"), TextConstants.UNDERLINE, underline = true))
        register(LanternTextStyle.Formatting(CatalogKey.minecraft("strikethrough"), TextConstants.STRIKETHROUGH, strikethrough = true))
        register(LanternTextStyle.Formatting(CatalogKey.minecraft("obfuscated"), TextConstants.OBFUSCATED, obfuscated = true))
        register(LanternTextStyle.Formatting(CatalogKey.minecraft("reset"), TextConstants.RESET,
                false, false, false, false, false))
        register(LanternTextStyle(CatalogKey.minecraft("none")))
    }
}
