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

/**
 * A [TranslationRegistry] that attempts to find a translation in any of the
 * provided [TranslationRegistry]s, in the order that they were defined.
 */
class ComposedTranslationRegistry(registries: Iterable<TranslationRegistry>) : UnmodifiableTranslationRegistry() {

    constructor(vararg registry: TranslationRegistry) : this(registry.asList())

    private val registries = registries.toList()

    override fun translate(key: String, locale: Locale): MessageFormat? =
            this.registries.asSequence()
                    .map { registry -> registry.translate(key, locale) }
                    .filterNotNull()
                    .firstOrNull()
}
