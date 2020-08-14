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
package org.lanternpowered.server.text

import net.kyori.adventure.translation.TranslationRegistry
import org.lanternpowered.api.locale.Locale
import java.text.MessageFormat

class DefaultTextRenderer(private val translationRegistry: TranslationRegistry) : LanternTextRenderer<Locale>() {

    override fun translate(key: String, context: Locale): MessageFormat? =
            this.translationRegistry.translate(key, context)
}
