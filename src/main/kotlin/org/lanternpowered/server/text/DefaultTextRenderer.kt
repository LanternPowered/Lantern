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

import org.lanternpowered.api.locale.Locale
import org.lanternpowered.api.text.translation.Translator
import java.text.MessageFormat

class DefaultTextRenderer(private val translator: Translator) : LanternTextRenderer<Locale>() {

    override fun translate(key: String, context: Locale): MessageFormat? =
            this.translator.translate(key, context)
}
