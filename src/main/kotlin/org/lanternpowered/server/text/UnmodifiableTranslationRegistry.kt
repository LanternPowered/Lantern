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
import java.util.ResourceBundle
import java.util.function.Function

abstract class UnmodifiableTranslationRegistry : TranslationRegistry {

    @Deprecated(message = "Unmodifiable registries cannot be modified.", level = DeprecationLevel.HIDDEN)
    final override fun register(key: String, locale: Locale, format: MessageFormat) =
            throw UnsupportedOperationException()

    @Deprecated(message = "Unmodifiable registries cannot be modified.", level = DeprecationLevel.HIDDEN)
    final override fun registerAll(locale: Locale, formats: Map<String, MessageFormat>) =
            throw UnsupportedOperationException()

    @Deprecated(message = "Unmodifiable registries cannot be modified.", level = DeprecationLevel.HIDDEN)
    final override fun registerAll(locale: Locale, keys: Set<String>, function: Function<String, MessageFormat>) =
            throw UnsupportedOperationException()

    @Deprecated(message = "Unmodifiable registries cannot be modified.", level = DeprecationLevel.HIDDEN)
    final override fun registerAll(locale: Locale, resourceBundle: ResourceBundle, escapeSingleQuotes: Boolean) =
            throw UnsupportedOperationException()

    @Deprecated(message = "Unmodifiable registries cannot be modified.", level = DeprecationLevel.HIDDEN)
    final override fun registerAll(locale: Locale, resourceBundlePath: String, escapeSingleQuotes: Boolean) =
            throw UnsupportedOperationException()

    @Deprecated(message = "Unmodifiable registries cannot be modified.", level = DeprecationLevel.HIDDEN)
    final override fun unregister(key: String) =
            throw UnsupportedOperationException()

    companion object {

        fun of(registry: TranslationRegistry): UnmodifiableTranslationRegistry {
            if (registry is UnmodifiableTranslationRegistry)
                return registry
            return object : UnmodifiableTranslationRegistry() {
                override fun translate(key: String, locale: Locale): MessageFormat? =
                        registry.translate(key, locale)
            }
        }
    }
}
