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
import java.text.MessageFormat
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

class LanternTranslationRegistry : TranslationRegistry {

    private val translations = ConcurrentHashMap<String, Translation>()

    override fun register(key: String, locale: Locale, format: MessageFormat) {
        this.translations.computeIfAbsent(key, ::Translation).register(locale, format)
    }

    override fun unregister(key: String) {
        this.translations.remove(key)
    }

    override fun translate(key: String, locale: Locale): MessageFormat? {
        return this.translations[key]?.translate(locale)
    }

    internal class Translation(private val key: String) {

        private val formats = ConcurrentHashMap<Locale, MessageFormat>()

        fun register(locale: Locale, format: MessageFormat) {
            val existing = this.formats.putIfAbsent(locale, format)
            require(existing == null) { "Translation already exists: $key for $locale" }
        }

        fun translate(locale: Locale): MessageFormat? {
            var format = this.formats[locale]
            if (format != null)
                return format
            format = this.formats[Locale(locale.language)] // without country
            if (format != null)
                return format
            return this.formats[Locale.US] // fallback to en_us
        }
    }
}
