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
package org.lanternpowered.server.util

import org.lanternpowered.api.locale.Locales
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

object LocaleCache {

    private val cache: MutableMap<String, Locale> = ConcurrentHashMap()

    /**
     * Gets the [Locale] for the given name.
     *
     * @param name The name
     * @return The locale
     */
    operator fun get(name: String): Locale = this.cache.computeIfAbsent(name.toLowerCase(Locale.ENGLISH)) {
        val parts = name.split("_".toRegex(), 3).toTypedArray()
        when (parts.size) {
            3 -> Locale(parts[0].toLowerCase(), parts[1].toUpperCase(), parts[2])
            2 -> Locale(parts[0].toLowerCase(), parts[1].toUpperCase())
            else -> Locale(parts[0])
        }
    }

    init {
        for (field in Locales::class.java.fields) {
            val name: String = field.name
            if (name.indexOf('_') < 0)
                continue
            this.cache[name.toLowerCase(Locale.ENGLISH)] = field.get(null) as Locale
        }
    }
}
